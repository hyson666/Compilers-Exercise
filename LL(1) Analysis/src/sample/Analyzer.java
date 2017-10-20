package sample;
import java.util.ArrayList;
import java.util.Stack;

/**
 *  句子分析器
 *  说明：对输入的句子进行分析然后输出表格数据
 */

public class Analyzer {

    public Analyzer() {
        super();
        analyzeStack = new Stack<Character>();
        // 结束符放入进行初始化
        analyzeStack.push('#');
    }

    // 步骤序列
    public ArrayList<AnalyzeStep> analyzeSteps;
    // 分析栈
    private Stack<Character> analyzeStack;
    // 输入串
    private String str;
    // 采用的文法分析器
    private Grammer LL1;

    /**
     * 用LL1文法初始化分析器
     */
    public void initLL1(Grammer gs) {
        LL1 = gs;
    }

    /**
     * 设置输入串
     */
    public void setString(String inputStr) {
        str = inputStr;
    }

    public void analyze() {
        analyzeSteps = new ArrayList<AnalyzeStep>();
        // 开始符进入栈中
        analyzeStack.push('E');
        int index = 0;
        // 对开始状态进行初始化
        AnalyzeStep temp = new AnalyzeStep();
        temp.setValue(index, analyzeStack.toString(), str, " ", "初始化");
        analyzeSteps.add(temp);
        // 定义之后表示的终结符和非终结符
        Character A,b;
        // 定义表示的动作状态
        String action;
        // 定义使用的产生式以及其逆向行驶
        String proc,proc_inverse;
        while(!analyzeStack.toString().equals("[#]")) {
            if(analyzeStack.toString().equals("#")) break;
            //System.out.println(str);
            // 每次无论什么操作步骤先加一没错
            index++;
            A = analyzeStack.peek();
            b = str.charAt(0);
            // 不需要getNext的情况，把栈顶弹出，并且寻找匹配式
            if(A != b) {
                AnalyzeStep step = new AnalyzeStep();
                String nowUseProc = LL1.searchAnalyzeTable(A,b);

                // 根据老师的输出，应该是先修改栈再录入状态的,这一段代码要注意一下
                if(nowUseProc == null) {
                    action = " ";
                    step.setValue(index, analyzeStack.toString(), str, "无法匹配", action);
                } else {
                    String[] tempStr = nowUseProc.split("->");
                    proc = tempStr[1];
                    // 如果产生的是空集，没有匹配，执行POP操作
                    if(tempStr[1].equals("ε")) {
                        analyzeStack.pop();
                        step.setValue(index, analyzeStack.toString(), str, nowUseProc, "POP");
                        analyzeSteps.add(step);
                        continue;
                    }
                    proc_inverse = new StringBuilder(proc).reverse().toString();
                    action = "POP,PUSH(" + proc_inverse + ")";
                    analyzeStack.pop();
                    // 反向入栈
                    for(int i = proc.length() - 1 ; i >= 0 ; --i) {
                        char pushChar = proc.charAt(i);
                        analyzeStack.push(pushChar);
                    }
                    step.setValue(index,analyzeStack.toString(), str , nowUseProc, action);
                    analyzeSteps.add(step);
                }
            } else {
                // 如果相等可以的话要改str，且执行弹出操作
                AnalyzeStep step = new AnalyzeStep();
                analyzeStack.pop();
                str = str.substring(1);
                step.setValue(index, analyzeStack.toString(), str, " ", "GETNEXT(I)");
                analyzeSteps.add(step);
            }
        }

    }
}
