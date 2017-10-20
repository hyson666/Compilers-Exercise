package sample;

import java.util.ArrayList;

/**
 * 重构在这里生成了记录处理步骤的类，同时方便表格的输出
 * index 步骤编号
 * analyzeStackStr 分析栈
 * str 剩余输入串
 * useExpStr 所用产生式
 */
public class AnalyzeStep {
    public Integer index;
    public String analyzeStackStr;
    public String str;
    public String useExpStr;
    public String action;

    public ArrayList<AnalyzeStep> analyzeSteps = new ArrayList<AnalyzeStep>();

    // 设置对象值的函数
    public void setValue(Integer now_index, String now_analyseStacckStr, String now_str, String now_useExpStr, String now_action) {
        index = now_index;
        analyzeStackStr = now_analyseStacckStr;
        str = now_str;
        useExpStr = now_useExpStr;
        action = now_action;
    }
}
