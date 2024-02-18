package com.example.usercenter.service.impl;



import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.config.RedisTemplateConfig;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.mapper.UserMapper;

import com.example.usercenter.model.dto.LoginUser;
import com.example.usercenter.model.dto.UserRegisterRequest;
import com.example.usercenter.model.vo.UserVO;
import com.example.usercenter.service.UserService;
import com.example.usercenter.utils.JWTUtils;
import com.example.usercenter.utils.RateLimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.model.User;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
* @author 余某某
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-07-28 20:30:39
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;
    private Map<String, String> map = new HashMap<>();

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String QQ = userRegisterRequest.getQQ();
        String userName = userRegisterRequest.getUserName();
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 6 || userAccount.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短或过长");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        if (userPassword.length() < 6 || userPassword.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //  qq长度限制
        if (QQ.length() < 5 || QQ.length() > 10)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "QQ长度过长或过短");

        //  不允许账号重复 通过锁保证线程安全 即同时出现多个账号可能一样的问题 不保证分布式的线程安全
        // 共享的资源是账号字符串，所以锁定字符串，如果出现相同字符串，由于他在线程池 所以是共享资源
        synchronized (userAccount.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密

            String encryptPassword = bCryptPasswordEncoder.encode(userPassword);
            // todo 3. 分配 accessKey, secretKey 也是保证数据安全性 暂时没有用，后面记得优化

            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName(userName);
            user.setQQ(QQ);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public String userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 6 && userAccount.length() > 20 || userPassword.length() < 6 && userPassword.length() > 20)
            return null;
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
//        这里进行用户的认证 也就是用户校验
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                UsernamePasswordAuthenticationToken(userAccount, userPassword);
        Authentication authenticate = authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);
//        认证通过则返回一个认证对象,失败那么它就会为空
        if (Objects.isNull(authenticate)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号或密码错误");
        }
//  认证成功则会把成功的对象传给你 也就是DetailService 我们实现了它并重写了
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        User user1 = loginUser.getUser();
        // 3. 用户脱敏
        String jwt = JWTUtils.getToken(user1);
        String userId = Long.toString(user1.getId());
        User safetyUser = getSafetyUser(user1);
        redisTemplate.opsForValue().set(userId, safetyUser);
        redisTemplate.expire(userId, 5 * 60 * 24, TimeUnit.SECONDS);

        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user1);
        return jwt;
    }

    @Override
    public User getSafetyUser(User originUser) {
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUserAvatar(originUser.getUserAvatar());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUpdateTime(originUser.getUpdateTime());
        return safetyUser;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null)
            throw new BusinessException(40400, "登录状态过期请重新登录");
        DecodedJWT decodedJWT = JWTUtils.decodeToken(token);
        String userId = decodedJWT.getSubject();
//        从redis中获取用户信息
//        可以通过instanceof判断是不是子类
        User user = (User) redisTemplate.opsForValue().get(userId);
        if (user == null)
            throw new BusinessException(40400, "登录状态过期请重新登录");
        return user;
    }


    @Override
    public boolean isAdmin(HttpServletRequest request) {
        return false;
    }
    @Override
    public boolean isAdmin(User user) {
//        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
//        todo 记得改

        return true;
    }
    @Override
    public boolean userLogout() {
//        删除redis中的值就行
//        可以通过securityContextHolder获取 通过jwt过滤器就会在哪里存储用户信息
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
//        如果存在用户那么删除
        redisTemplate.delete(Long.toString(user.getId()));
        return true;
    }

    @Override
    public boolean verifyUserAccount(String userAccount) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if(user!=null)
        return true;
        return false;
    }

    @Override
    public boolean resettingPassword(String password1, String password2,String userAccount) {
// 数据校验 不允许数据为空 且两次密码均要一致且账号要存在(账号校验在前面已校验)
        if(password1 == null || password2 == null||userAccount == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if(!password1.equals(password2))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
      QueryWrapper<User>queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("userAccount",userAccount);
      User user = userMapper.selectOne(queryWrapper);
       String encryptPassword =bCryptPasswordEncoder.encode(password1);
       user.setUserPassword(encryptPassword);
        int update = userMapper.update(user, queryWrapper);
        if(update == 0)
            return false;
        else return true;

    }

    @Override
    public boolean verifyEmail(String code) {
        if(code == null || map.containsKey(code))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if(code.equals(map.get("code")))
            return true;
        else return false;
    }
//todo  记得限制发送验证码的频率
    @Override
    public boolean sendEmail(String email,String userAccount) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setSubject("验证码邮件");//主题
            //生成随机数
            int min = 100000; // 最小值
            int max = 999999; // 最大值
            Random random = new Random();
            int code = random.nextInt(max - min + 1) + min;
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount",userAccount);
            User user = userMapper.selectOne(queryWrapper);
            if(!user.getQQ().equals(email))
                throw new BusinessException(40000,"你输入的邮箱和账号绑定的不一致，请输入正确的邮箱");
//           new RateLimiterUtil().getRemainingSeconds();
            email = email +"@qq.com";
            mailMessage.setText("您收到的验证码是"+code);
            mailMessage.setTo(email);
            mailMessage.setFrom("2532578435@qq.com");
            mailSender.send(mailMessage);
            map.put("code",Integer.toString(code));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }

        return userList.stream().map(user -> {UserVO userVO = new UserVO();
            userVO.setId(user.getId());userVO.setUserAvatar(user.getUserAvatar());
        userVO.setUserRole(user.getUserRole());userVO.setUserName(user.getUserName());
        return userVO;}).collect(Collectors.toList());
    }
}




