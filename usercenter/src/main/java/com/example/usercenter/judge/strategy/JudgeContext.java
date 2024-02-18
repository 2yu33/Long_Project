package com.example.usercenter.judge.strategy;


import com.example.usercenter.judge.codesandbox.model.JudgeInfo;
import com.example.usercenter.model.dto.question.JudgeCase;
import lombok.Data;
import org.model.Question;
import org.model.QuestionSubmit;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
