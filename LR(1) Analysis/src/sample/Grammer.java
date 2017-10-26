package sample;

import apple.laf.JRSUIUtils;
import javafx.beans.property.IntegerProperty;

import java.io.Serializable;
import java.util.*;

public class Grammer implements Serializable{

    Grammer(ArrayList<String> tempGsArray) {
        super();
        // 初始化变量
        nvSet = new TreeSet<Character>();
        ntSet = new TreeSet<Character>();
        gsArray = new ArrayList<String>();
        expressionMap = new HashMap<Character, ArrayList<String>>();
        firstMap = new HashMap<Character, TreeSet<Character>>();
        firstSetLength = new HashMap<Character, Integer>();
        statesMap = new HashMap<Integer, State>();
        hadExtend = new boolean[100];


        // 计算分析器
        this.setGsArray(tempGsArray);
        this.getNvNt();
        this.initExpMaps();
        this.getFirst();
        this.initLr1ProjectsSet();
    }

    /**
     * 定义如下变量：
     * 1. 表达式列表gsArray
     * 2. 非终结符集合nvSet
     * 3. 终结符集合ntSet
     * 4. 非终结符到表达式的映射expressionMap
     * 5. First集合映射firstMap
     */
    private ArrayList<String> gsArray;
    private TreeSet<Character> nvSet;
    private TreeSet<Character> ntSet;
    private HashMap<Character, ArrayList<String>> expressionMap;
    private HashMap<Character, TreeSet<Character>> firstMap;
    private HashMap<Character, Integer> firstSetLength;
    private HashMap<Integer, State> statesMap;
    private boolean[] hadExtend;


    private void setGsArray(ArrayList<String> gsArrary) {
        this.gsArray = gsArrary;
    }


    private void getNvNt() {
        for (String gsItem : gsArray) {
            String[] str = gsItem.split("->");
            // 拆分之后的第一个字符串的第一个字母就是Nv的元素
            String charItemStr = str[0];
            char charItem = charItemStr.charAt(0);
            nvSet.add(charItem);
        }
        for (String gsItem : gsArray) {
            String[] str = gsItem.split("->");
            // 在生成式当中排除Nv集合的元素就是Nt
            String produceStr = str[1];
            for (int i = 0; i < produceStr.length(); ++i) {
                char charItem = produceStr.charAt(i);
                if(!nvSet.contains(charItem) && charItem != '|') {
                    ntSet.add(charItem);
                }
            }
        }
    }


    private void initExpMaps() {
        expressionMap = new HashMap<Character, ArrayList<String>>();
        for (String gsItem : gsArray) {
            String[] str = gsItem.split("->");
            String nvStr = str[0];
            String rightStr = str[1];
            char charItem = nvStr.charAt(0);
            if (!expressionMap.containsKey(charItem)) {
                ArrayList<String> expArr = new ArrayList<String>();
                String[] produceStr = rightStr.split("\\|");
                // 数组转为list然后放入表中
                expArr.addAll(Arrays.asList(produceStr));
                expressionMap.put(charItem, expArr);
            }
        }
    }


    private boolean calcFirstFinish(TreeSet<Character> ntSet) {
        for (Character nowNt : ntSet) {
            int nowCnt = firstMap.get(nowNt).size();
            if(nowCnt != firstSetLength.get(nowNt)) {
                firstSetLength.put(nowNt, nowCnt);
                return false;
            }
        }
        return true;
    }


    private void getFirst() {
        for(Character tempKey : nvSet) {
            firstMap.put(tempKey, new TreeSet<Character>());
            firstSetLength.put(tempKey, 0);
        }
        do {
            for (Character nvItem : nvSet) {
                ArrayList<String> procArray = expressionMap.get(nvItem);
                for (String nowProc : procArray) {
                    TreeSet<Character> firstSet = firstMap.get(nvItem);
                    calcFirst(firstSet, nowProc, nvItem);
                }
            }
        }while(!calcFirstFinish(nvSet));
    }


    private void calcFirst(TreeSet<Character> firstSet, String nowProc, Character nvItem) {
        Character firstChar = nowProc.charAt(0);
        if (firstChar == 'ε' || ntSet.contains(firstChar)) {
            firstSet.add(firstChar);
            firstMap.put(nvItem, firstSet);
        } else {
            for(int i = 0; i < nowProc.length(); ++i) {
                Character nowChar = nowProc.charAt(i);
                if(nvSet.contains(nowChar)) {
                    firstSet.addAll(firstMap.get(nowChar));
                    if(!firstMap.get(nowChar).contains('ε')){
                        break;
                    }
                } else {
                    firstSet.add(nowChar);
                }
            }
        }
    }


    /**
     * 检查能否生成新的项目
     * TODO:进行项目的产生式拓展
     */
    private Lr1project checkNewProject(Lr1project nowProject, Character receiveItem){
        if(nowProject.pos >= nowProject.getRight().length()) {
            return null;
        } else if(nowProject.getRight().charAt(nowProject.pos) == receiveItem) {
            //TODO: 确定tempProject能否略去（原类值是否会改变？）
            Lr1project tempProject = (Lr1project) nowProject.clone();
            tempProject.pos += 1;
            return tempProject;
        }
        return null;
    }


    /**
     * 增广函数
     * 注意：同样利用副本，不然又引发修改错误
     * @param tempState 临时状态
     * @return 增广后的状态
     */
    private State addProcToState(State tempState) {
        int lastSum = 0;
        do{
            lastSum = tempState.projectSet.size();
            // 制作状态副本
            State copyState = (State) tempState.clone();
            for(Lr1project tempProject : copyState.projectSet) {
                if(tempProject.pos < tempProject.getRight().length()) {
                    Character nvItem = tempProject.getRight().charAt(tempProject.pos);
                    if(nvSet.contains(nvItem)) {
                        for(String procItem : expressionMap.get(nvItem)) {
                            tempState.projectSet.add(new Lr1project(nvItem + "->" + procItem, 0));
                        }
                    }
                }
            }
        }while(tempState.projectSet.size() != lastSum);
        return tempState;
    }

    /**
     * 初始化LR1项目规范族，这是一个递推的过程
     * 一直到延展不出新的状态的时候就结束！
     */
    private void initLr1ProjectsSet() {
        // 定义初始状态0
        String startProc = "S->E";
        Lr1project startProjects = new Lr1project(startProc, 0);
        State startState = new State();
        startState.projectSet.add(startProjects);
        startState = addProcToState(startState);
        statesMap.put(0,startState);

        // 设立总状态数为1，判断有无增加状态数来决定是否退出循环
        int totalState = 1, lastTotal = 0;
        TreeSet<Character> nvntSet = new TreeSet<Character>();
        nvntSet.addAll(nvSet);
        nvntSet.addAll(ntSet);

        // 利用各个符号，对当前的所有状态都延展一次，同时更新totalState
        // stateID必须是副本，不然会引起集合修改异常
        do {
            TreeSet<Integer> stateIdSet = new TreeSet<Integer>();
            stateIdSet.addAll(statesMap.keySet());
            lastTotal = totalState;
            for(Integer stateID : stateIdSet) {
                State state = (State) statesMap.get(stateID).clone();
                for (Character nvntItem : nvntSet) {
                    State tempState = new State();
                    for (Lr1project projectItem : state.projectSet) {
                        Lr1project tempProject = checkNewProject(projectItem, nvntItem);
                        if (tempProject != null) {
                            tempState.projectSet.add(tempProject);
                            tempState.projectSet.remove(projectItem);
                            //tempState = addProcToState(tempProject, tempState);
                            tempState = addProcToState(tempState);
                        }
                    }
                    if (!statesMap.values().contains(tempState) && !tempState.projectSet.isEmpty()) {
                        statesMap.put(totalState, tempState);
                        totalState++;
                    }
                }
            }
        }while(totalState != lastTotal);
    }
}

/**
 * 单个项目对象
 * 配合对项目集的比较重新定义了equals（）以及hashCode
 * 由于为了更加直观不采用表达式编号，而直接记录当前表达式，所以必须定义getRight()来获取产生式的值
 * 同样需要制作副本，重写了clone()函数
 * 每一个对象两个属性：产生式（左、右）、点位
 */
class Lr1project implements Cloneable{
    Lr1project(String proc, Integer pos) {
        this.proc = proc;
        this.pos = pos;
    }
    public String proc;
    public Integer pos;

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null) {
            return false;
        }
        Lr1project lr1project = (Lr1project)obj;
        return Objects.equals(this.proc, lr1project.proc) && Objects.equals(this.pos, lr1project.pos);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for(int i=0;i<this.proc.length();i++){        //从字符串的左边开始计算
            int letterValue = this.proc.charAt(i) - 96;//将获取到的字符串转换成数字，比如a的码值是97，则97-96=1 就代表a的值，同理b=2；
            hash = ((hash << 5) + letterValue) % 1000;//防止编码溢出，对每步结果都进行取模运算
        }
        return hash;
    }

    public String getRight() {
        String[] splitStr = this.proc.split("->");
        // System.out.println(splitStr[0]);
        return splitStr[1];
    }

    @Override
    public Object clone() {
        Lr1project lr1project = new Lr1project(null, 0);
        lr1project.pos = this.pos;
        lr1project.proc = this.proc;
        return lr1project;
    }
}

/**
 * 状态对象
 * 因为需要制作副本，所以重写了clone()函数
 * 此外还需要实现对两个state对象的比较，重写了equal函数，不然会重复得爆炸
 * 每个状态暂时只需要维护一个项目集
 */
class State implements Cloneable {
    State() {
        projectSet = new HashSet<Lr1project>();
    }

    public HashSet<Lr1project> projectSet;

    @Override
    public Object clone() {
        State state = new State();
        HashSet<Lr1project> tempSet = new HashSet<Lr1project>();
        tempSet.addAll(this.projectSet);
        state.projectSet = tempSet;
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        State state = (State) obj;
        HashSet<Lr1project> set1 = this.projectSet;
        HashSet<Lr1project> set2 = state.projectSet;
        if (set1 == null && set2 == null) {
            return true; // Both are null
        }

        if (set1 == null || set2 == null || set1.size() != set2.size()
                || set1.size() == 0 || set2.size() == 0) {
            return false;
        }

        Iterator ite1 = set1.iterator();
        Iterator ite2 = set2.iterator();

        boolean isFullEqual = true;

        while (ite2.hasNext()) {
            if (!set1.contains(ite2.next())) {
                isFullEqual = false;
            }
        }
        return isFullEqual;
    }
}