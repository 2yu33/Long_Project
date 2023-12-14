package com.example.usercenter;

import com.example.usercenter.contant.UserConstant;
import com.example.usercenter.manager.AiManager;
import com.example.usercenter.manager.RedisLimiterManager;
import com.example.usercenter.mapper.ChartMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.Test;

import org.model.Chart;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@SpringBootTest
@Slf4j
class UserCenterApplicationTests {
@Resource
PasswordEncoder passwordEncoder;
@Resource
AiManager aiManager;
@Resource
    ChartMapper chartMapper;
@Resource
    RedisLimiterManager redisLimiterManager;
    @Test
    void contextLoads() throws IOException {
//        RestHighLevelClient esClient = new RestHighLevelClient(RestClient.builder
//                (new HttpHost("localhost",9200,"http")));
//        CreateIndexRequest request = new CreateIndexRequest("user");
//        CreateIndexResponse createIndexResponse = esClient.indices().create(request,
//                RequestOptions.DEFAULT);
//        boolean acknowledged = createIndexResponse.isAcknowledged();
//        System.out.println("索引操作"+ acknowledged);
//        esClient.close();

      }


}
