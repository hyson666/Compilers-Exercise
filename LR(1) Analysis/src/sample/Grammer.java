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
     * 6. First集合长度映射firstSetLength
     * 7. 状态编号、项目集映射statesMap
     */
    private ArrayList<String> gsArray;
    private TreeSet<Character> nvSet;
    private TreeSet<Character> ntSet;
    private HashMap<Character, ArrayList<String>> expressionMap;
    private HashMap<Character, TreeSet<Character>> firstMap;
    private HashMap<Character, Integer> firstSetLength;
    public HashMap<Integer, State> statesMap;


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
     * TODO: 加入firstBa集:判断当前nvItem的下一个是否合法，不合法返回'#'，合法返回first（B）
     * @param tempState 临时状态
     * @return 增广后的状态
     */
    private State addProcToState(State tempState, TreeSet<Character> addFirstBaSet) {
        // 首先一定要对现有的表达式进行初始化（有的不用，没有的加,注意在加入表达式的过程中也要动态加入)
        for(Lr1project tempProject : tempState.projectSet) {
            if(!tempState.firstBaMap.containsKey(tempProject)) {
                tempState.firstBaMap.put(tempProject, new TreeSet<Character>());
            }
        }
        int lastSum = 0;

        do{
            lastSum = tempState.projectSet.size();
            // 制作状态副本
            State copyState = (State) tempState.clone();
            // 查看所有项目，寻找可以增广的（是非终结符的情况）
            for(Lr1project tempProject : copyState.projectSet) {
                if(tempProject.pos < tempProject.getRight().length()) {
                    // TODO：注意等等操作这里的pos+1加入！！！！！！！！！
                    Character nvItem = tempProject.getRight().charAt(tempProject.pos);
                    //TODO: 加入firstBa集Here，只对非终结符拓展处理，这里开始曾广！！
                    if(nvSet.contains(nvItem)) {
                        for(String procItem : expressionMap.get(nvItem)) {
                            Lr1project addProject = new Lr1project(nvItem + "->" + procItem, 0);
                            tempState.projectSet.add(addProject);
                            // 不存在才创建，否则会被覆盖的！
                            if(!tempState.firstBaMap.containsKey(addProject)) {
                                tempState.firstBaMap.put(addProject, new TreeSet<Character>());
                            } else {
                                continue;
                            }
                            // 获取当前表达式对应的firstBa映射
                            TreeSet<Character> tempfirstBaSet = tempState.firstBaMap.get(addProject);
                            // 不管发生什么先加入之前状态的,了里面只有一个'#'
                            // TODO: 不是预先送入addall
                            if(!addFirstBaSet.contains('#')) {
                                tempfirstBaSet.addAll(addFirstBaSet);
                            }
                            if(tempProject.pos + 1 < tempProject.getRight().length()) {
                                // 讨论后面一个是否终结符
                                Character nextItem = tempProject.getRight().charAt(tempProject.pos + 1);
                                if(ntSet.contains(nextItem)) {
                                    tempfirstBaSet.add(nextItem);
                                } else {
                                    tempfirstBaSet.addAll(firstMap.get(nextItem));
                                }
                            } else if (tempfirstBaSet.isEmpty()) {
                                // 后面是末尾,而且目前为空,则加入'#'
                                tempfirstBaSet.add('#');
                            }
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
     * 在这里可以顺带把GOTO集维护了！如果有新产生的，该状态连边，若没有，则连接到已知状态！
     */
    private void initLr1ProjectsSet() {
        // 定义初始状态0
        String startProc = "S->E";
        Lr1project startProjects = new Lr1project(startProc, 0);
        State startState = new State();
        startState.projectSet.add(startProjects);
        TreeSet<Character> tempSet = new TreeSet<Character>();
        tempSet.add('#');
        startState = addProcToState(startState, tempSet);
        startState.firstBaMap.put(startProjects, tempSet);
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
                            //TODO: 循环增加firstBa！不用循环！子集加了自己有B数！一行搞定！
                            tempSet = new TreeSet<Character>();
                            if(stateID == 1 && nvntItem == 'a') {
                                System.out.println("在这停顿！");
                            }
                            TreeSet<Character> addFirstBaSet = state.firstBaMap.get(projectItem);
                            tempSet.addAll(addFirstBaSet);
                            tempState.firstBaMap.put(tempProject, tempSet);
                            // 处理增广
                            tempState.projectSet.add(tempProject);
                            tempState.projectSet.remove(projectItem);
                            //tempState = addProcToState(tempProject, tempState);
                            tempState = addProcToState(tempState,  addFirstBaSet);
                        }
                    }
                    //TODO: 在这里维护GOTO集！
                    if (!statesMap.values().contains(tempState) && !tempState.projectSet.isEmpty()) {
                        statesMap.put(totalState, tempState);
                        statesMap.get(stateID).gotoSet.add(totalState);
                        totalState++;
                    } else {
                        // 获取该存在状态的编号,连边
                        Set set = statesMap.entrySet();
                        Iterator iter = set.iterator();
                        while(iter.hasNext()) {
                            HashMap.Entry entry = (HashMap.Entry) iter.next();
                            if(entry.getValue().equals(tempState)) {
                                int toNode = (int) entry.getKey();
                                statesMap.get(stateID).gotoSet.add(toNode);
                                break;
                            }
                        }
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
        firstBaMap = new HashMap<Lr1project, TreeSet<Character>>();
        gotoSet = new TreeSet<Integer>();
    }

    public HashSet<Lr1project> projectSet;
    public HashMap<Lr1project, TreeSet<Character>> firstBaMap;
    public TreeSet<Integer> gotoSet;


    @Override
    public Object clone() {
        State state = new State();
        HashSet<Lr1project> tempSet = new HashSet<Lr1project>();
        tempSet.addAll(this.projectSet);
        state.projectSet = tempSet;
        //TODO: 复制firstBaMap
        HashMap<Lr1project, TreeSet<Character>> tempfirstBaMap = new HashMap<Lr1project, TreeSet<Character>>();
        for(Lr1project lr1project : projectSet) {
            tempfirstBaMap.put(lr1project, this.firstBaMap.get(lr1project));
        }
        state.firstBaMap = tempfirstBaMap;
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        // 判断相等的条件：项目集和firstBa集合群相等
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

        // 首先检测项目集
        Iterator ite = set2.iterator();

        boolean projectIsFullEqual = true;

        while (ite.hasNext()) {
            if (!set1.contains(ite.next())) {
                projectIsFullEqual = false;
            }
        }

        // 接下来检测firstBa集合群，键值同时存在且相等
        boolean firstBaIsFullEqual = true;

        //TODO：改回正常的
        if(this.firstBaMap.isEmpty() || state.firstBaMap.isEmpty()) {
            firstBaIsFullEqual = false;
        } else {
            for (Lr1project projectItem : this.projectSet) {
                if (state.projectSet.contains(projectItem)) {
                    ite = state.firstBaMap.get(projectItem).iterator();
                    while (ite.hasNext()) {
                        if (!this.firstBaMap.get(projectItem).contains(ite.next())) {
                            firstBaIsFullEqual = false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
        return projectIsFullEqual && firstBaIsFullEqual && (this.firstBaMap.size() == state.firstBaMap.size()) && (this.projectSet.size() == state.projectSet.size());
    }
}