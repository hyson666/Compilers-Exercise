package sample;

import java.util.*;

/**
 * LR1主控程序
 * @author HYSON
 */

public class MainControl {
    MainControl(String inputStr, Grammer lr1) {
        this.lr1 = lr1;
        this.inputStr = inputStr;
        this.analysis();
    }

    private Grammer lr1;
    //输入串
    private String inputStr;
    // 步骤序列定义
    public ArrayList<AnalysisStep> analysisSteps = new ArrayList<AnalysisStep>();
    // 状态栈
    private Stack<Integer> stateStack = new Stack<Integer>();
    // 符号
    private String symbolStr = "";

    private void analysis() {
        stateStack.push(0);
        int stepId = 0;
        String actionStr = "初始化";
        AnalysisStep lastStep;
        while(true) {
            lastStep = new AnalysisStep(++stepId, stateStack.toString(), inputStr, symbolStr, actionStr);
            analysisSteps.add(lastStep);
            Integer nowState = stateStack.peek();
            HashMap<Character, String> actionMap = lr1.actionFuncMap.get(nowState);
            HashMap<Character, Integer> gotoMap = lr1.gotoFuncMap.get(nowState);
            // 栈顶第一个符号
            if(inputStr.length() == 0) break;
            Character nowChar = inputStr.charAt(0);
            // 确定是sx还是rx,sx是移入，rx是规约
            String action = actionMap.get(nowChar);
            if(action==null) break;
            if (action.charAt(0) == 'S') {
                // 转化出来的数字进入状态栈
                //stateStack.push(Integer.valueOf(action.substring(1)));
                // 写入新状态
                actionStr = "移入";
                stateStack.push(Integer.valueOf(action.substring(1)));
                symbolStr += inputStr.charAt(0);
                inputStr = inputStr.substring(1);
            } else if (action.charAt(0) == 'r') {
                //actionStr = "移入";
                Integer procId = Integer.valueOf(action.substring(1));
                Set<String> procSet = lr1.numOfGs.keySet();
                // 查找对应表达式，进行算法操作
                for(String proc : procSet) {
                    if(Objects.equals(lr1.numOfGs.get(proc), procId)) {
                        String[] strSplit = proc.split("->");
                        String rightStr = strSplit[1], leftStr = strSplit[0];
                        symbolStr = symbolStr.substring(0,symbolStr.length() - rightStr.length());
                        symbolStr += leftStr.toString();
                        int length = rightStr.length();
                        // 把proc长度个栈顶字符弹出去
                        while(length > 0) {
                            stateStack.pop();
                            length-- ;
                        }
                        nowState = stateStack.peek();
                        gotoMap = lr1.gotoFuncMap.get(nowState);
                        char[] leftChar = leftStr.toCharArray();
                        stateStack.push(gotoMap.get(leftChar[0]));
                        actionStr = "根据" + proc +"规约";
                        break;
                    }
                }
            } else if (Objects.equals(action, "acc")) {
                break;
            } else {
                // 错误处理
                System.out.print("我也不知道怎么办啊！");
            }
        }
    }

}
