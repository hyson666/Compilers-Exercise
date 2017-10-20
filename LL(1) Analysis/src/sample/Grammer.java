package sample;

import sun.reflect.generics.tree.Tree;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  LL(1)文法类
 *  By Hyson
 */

public class Grammer implements Serializable{

    public Grammer() {
        super();
        gsArray = new ArrayList<String>();
        nvSet = new TreeSet<Character>();
        ntSet = new TreeSet<Character>();
        firstMap = new HashMap<Character, TreeSet<Character>>();
        followMap = new HashMap<Character, TreeSet<Character>>();
        followLength = new HashMap<Character, Integer>();
        numOfNv = new HashMap<Character, Integer>();
        numOfNt = new HashMap<Character, Integer>();
    }

    /**
     *  定义各个变量
     */
    //TODO: 修改回去私有
    public String[][] analyzeTable;
    // LL(1)文法产生集合
    private ArrayList<String> gsArray;
    // Vn非终结符集合
    public TreeSet<Character> nvSet;
    // Vt终结符集合
    public TreeSet<Character> ntSet;
    // First集合
    public HashMap<Character, TreeSet<Character>> firstMap;
    // Follow集合
    public HashMap<Character, TreeSet<Character>> followMap;
    // 表达式集合
    private HashMap<Character, ArrayList<String>> expressionMap;
    // Follow集长度集合
    private HashMap<Character, Integer> followLength;
    // 非终结符与编号映射
    private HashMap<Character, Integer> numOfNv;
    // 终结符与编号映射
    public HashMap<Character, Integer> numOfNt;
    public Object[] nvArray;

    // 返回分析表
    public String[][] getAnalyzeTable() {
        return analyzeTable;
    }

    // 设置分析表值
    public void setAnalyzeTable(String[][] analyzeTable){
        this.analyzeTable = analyzeTable;
    }

    // 初始化gsArray的值
    public void setGsArray(ArrayList<String> gsArray) {
        this.gsArray = gsArray;
    }

    /**
     * 获取非终结符集和终结符集
     * 1.获取非终结符：每一个生成式箭头左边单个符号的所有符号集
     * 2.获取非终结符：不是终结符当然就是非终结符
     * 利用方法：String.split()划分非终结符和生成式
     *           Set.contains()来查询集合中是否存在该元素
     */
    public void getNvNt() {
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
            String produce_str = str[1];
            for (int i = 0; i < produce_str.length(); ++i) {
                char charItem = produce_str.charAt(i);
                if(!nvSet.contains(charItem)) {
                    ntSet.add(charItem);
                }
            }
        }
    }

    /**
     *  初始化表达式映射集合表
     *  利用非终结符去映射所有生成式的表
     *  TODO:加入利用‘|’来区分生成式的功能，之后用来遍历
     */
    public void init_exp_maps() {
        expressionMap = new HashMap<Character, ArrayList<String>>();
        for (String gsItem : gsArray) {
            String[] str = gsItem.split("->");
            String nv_str = str[0];
            String right_str = str[1];
            char charItem = nv_str.charAt(0);
            if (!expressionMap.containsKey(charItem)) {
                ArrayList<String> exp_arr = new ArrayList<String>();
                String[] produce_str = right_str.split("\\|");
                // 数组转为list然后放入表中
                exp_arr.addAll(Arrays.asList(produce_str));
                expressionMap.put(charItem, exp_arr);
            }
        }
    }

    /**
     * 利用迭代器，遍历所有Nv，求出他们的First集
     * 对每一个表达式从前往后遍历，直至第一个不含空串的非终结符的产生终结符全部放入First集
     */
    public void get_first() {
        Iterator<Character> iter = nvSet.iterator();
        while(iter.hasNext()) {
            Character nvItem = iter.next();
            ArrayList<String> arrayList = expressionMap.get(nvItem);
            TreeSet<Character> firstSet = firstMap.get(nvItem);
            for(String now_proc : arrayList) {
                boolean shouldBreak = false;
                for(int i = 0; i < now_proc.length() && !shouldBreak; ++i) {
                    char nowChar =  now_proc.charAt(i);
                    // 调取此时非终结符对应的first集合映射
                    if(firstSet == null) {
                        firstSet = new TreeSet<Character>();
                    }
                    // 参数说明：1.当前处理的first集合，2.当前处理的非终结符，3.当前处理字符
                    shouldBreak = calc_first(firstSet, nvItem, nowChar);
                }
            }
        }
    }
    /**
     * 计算first集函数
     * 针对当前的一条产生式，找到第一个终结符，或者不含有空串的非终结符，把这个非终结符产生的终结符放入first
     */
    private boolean calc_first(TreeSet<Character> firstSet, Character nvItem, char nowChar) {
        boolean haveEmpty = false,haveNt = false;
        if (nowChar == 'ε' || ntSet.contains(nowChar)) {
            firstSet.add(nowChar);
            firstMap.put(nvItem, firstSet);
            // 首先找到了的终结符可以用以中止继续进行
            return true;
        } else if (nvSet.contains(nowChar)) {
            // 如果是非终结符，迭代继续，如果不含有空串，返回true，含有的话说明接下来还要继续的
            // 首先获取当前所有产生式，看看是否含有空集合，且带有终结符
            ArrayList<String> arrayList = expressionMap.get(nowChar);
            if(arrayList.contains("ε"))
                haveEmpty = true;
            // 搜寻产生式中是否含有终结符
            for (int i = 0; i < arrayList.size(); ++i) {
                String now_proc = arrayList.get(i);
                char tempChar = now_proc.charAt(0);
                calc_first(firstSet, nvItem, tempChar);
            }
        }
        return true;
    }
    /**
     * 获取Follow集合
     * 通过遍历全部产生式中所有Nv的情况来确定他们的集合（利用进阶在后面非终结符的First）
     */

    public  void getFollow() {
        // 初始化每个非终结符的Follow集
        for(Character tempKey : nvSet) {
            TreeSet<Character> tempSet = new TreeSet<Character>();
            followMap.put(tempKey, tempSet);
        }

        Iterator<Character> iterator = nvSet.iterator();

        while(iterator.hasNext()) {
            // 指定当前处理的非终结符
            Character nvItem = iterator.next();
            Set<Character> keySet = expressionMap.keySet();
            for(Character keyItem : keySet) {
                ArrayList<String>  procArray = expressionMap.get(keyItem);
                for(String nowProc : procArray){
                    TreeSet<Character> followSet = followMap.get(nvItem);
                    // 参数说明： 正在处理字符串，当前字符串，当前正在展开非终结符，当前公式，follow集
                    calc_follow(nvItem, nvItem, keyItem, nowProc, followSet);
                }
            }
        }
    }

    /**
     * 计算Follow集的函数，参数说明如下：
     * @param nowCharItem 当前正在查找的符号
     * @param nvItem 当前正在计算Follow集的非终结符
     * @param keyItem 当前已经展开的非终结符名
     * @param nowProc 当前已经展开的公式
     * @param followSet 当前处理非终结符的Follow集
     */
    private void calc_follow(Character nowCharItem, Character nvItem, Character keyItem,
                             String nowProc, TreeSet<Character> followSet)
    {
        // Case1：当前非终结符是开始符，则加入'#'到Follow(E)集中
        if (nvItem.equals('E')) {
            followSet.add('#');
            followMap.put(nowCharItem, followSet);
        }
        // Case2：当前产生式中存在..Ab..的情况,直接添加b进入Follow(A)集
        if (TextUtil.containsAb(ntSet, nowProc, nvItem)) {
            Character b = TextUtil.getNextToA(nowProc, nvItem);
            followSet.add(b);
            followMap.put(nowCharItem, followSet);
        }
        // Case3: 当前产生式中存在..AB..的情况，加入First(B)到Follow(A)中，如果B含有空串，下一个同样处理
        if (TextUtil.containsAB(nvSet, nowProc, nvItem)) {
            Character B = TextUtil.getNextToA(nowProc, nvItem);
            TreeSet<Character> firstSetOfB = firstMap.get(B);
            followSet.addAll(firstSetOfB);
            if(TextUtil.containsAB_BIsNUll(nvSet, nowProc, nvItem, expressionMap)) {
                String nowCP = nvItem + B.toString();
                int index = 0;
                while (!TextUtil.cpIsOver(nowCP, nowProc, ntSet)) {
                    index = TextUtil.nowIndex(nowCP, nowProc);
                    followSet.addAll(firstMap.get(nowProc.charAt(index)));
                }
            } else {
                followSet.add('#');
                followSet.remove('ε');
                followMap.put(nowCharItem, followSet);
            }
        }
        // Case4: B-> aA,把Follow(B)放入Follow(A)
        if(TextUtil.containbA(nvSet, nowProc, nvItem)) {
            Set<Character> keySet = expressionMap.keySet();
            for(Character key_item : keySet) {
                ArrayList<String> procOfKey = expressionMap.get(key_item);
                for(String proc_item : procOfKey) {
                    calc_follow(nowCharItem, nvItem, key_item,nowProc, followSet);
                }
            }
        }
    }
    /**
     * 上面逻辑有点乱，换一个实现，用书本上的方式实现
     * 一共三个函数，一个公共调用接口，一个计算，一个检测集合大小变化
     * 测试AC！
     */
    public void get_follow() {
        for(Character tempKey : nvSet) {
            TreeSet<Character> tempSet = new TreeSet<Character>();
            followMap.put(tempKey, tempSet);
            followLength.put(tempKey, 0);
        }
        do {
            Iterator<Character> iterator = nvSet.iterator();
            while (iterator.hasNext()) {
                Character nvItem = iterator.next();
                Set<Character> keySet = expressionMap.keySet();
                for (Character keyItem : keySet) {
                    ArrayList<String> procArray = expressionMap.get(keyItem);
                    for (String nowProc : procArray) {
                        TreeSet<Character> followSet = followMap.get(nvItem);
                        // 参数说明： 正在处理字符串，当前字符串，当前正在展开非终结符，当前公式，follow集
                        calc_follow_new(nvItem, keyItem, nowProc, followSet);
                    }
                }
            }
            //System.out.println("!!!");
        }while(!calc_follow_finish(nvSet));
    }

    private void calc_follow_new(Character nvItem, Character keyItem, String nowProc, TreeSet<Character> followSet) {
        // Case1：当前非终结符是开始符，则加入'#'到Follow(E)集中
        if (nvItem.equals('E')) {
            followSet.add('#');
            followMap.put(nvItem, followSet);
        }
        // Case2：当前产生式中存在..Ab..的情况,直接添加b进入Follow(A)集
        if (TextUtil.containsAb(ntSet, nowProc, nvItem)) {
            Character b = TextUtil.getNextToA(nowProc, nvItem);
            followSet.add(b);
            //System.out.println(b);
            followMap.put(nvItem, followSet);
        }
        // Case3: 当前产生式中存在..AB..的情况，加入First(B)到Follow(A)中，如果B含有空串，下一个同样处理
        if (TextUtil.containsAB(nvSet, nowProc, nvItem)) {
            Character B = TextUtil.getNextToA(nowProc, nvItem);
            TreeSet<Character> firstSetOfB = firstMap.get(B);
            followSet.addAll(firstSetOfB);
            //System.out.println(firstSetOfB);
            followMap.put(nvItem, followSet);
        }
        if(TextUtil.containsAB_BIsNUll(nvSet, nowProc, nvItem, expressionMap)) {
            followSet.add('#');
            followSet.remove('ε');
            TreeSet<Character> followSetOfKey = followMap.get(keyItem);
            followSet.addAll((followSetOfKey));
            followMap.put(nvItem, followSet);
        }
        if(TextUtil.containbA(nvSet, nowProc, nvItem)) {
            TreeSet<Character> followSetOfKey = followMap.get(keyItem);
            followSet.addAll((followSetOfKey));
            //System.out.println(followSetOfKey);
            followMap.put(nvItem, followSet);
        }
    }

    private boolean calc_follow_finish(TreeSet<Character> nvSet) {
        Iterator iter;
        iter = nvSet.iterator();
        boolean judge;
        while (iter.hasNext()) {
            Character nowNv = (Character) iter.next();
            int now_cnt = followMap.get(nowNv).size();
            if(now_cnt != followLength.get(nowNv)) {
                followLength.put(nowNv, now_cnt);
                return false;
            }
        }
        return true;
    }
    /**
     * 构造预测分析表
     */
    public void genAnalyzeTable() {
        init_nvntnum();
        Object[] ntArray = ntSet.toArray();
        nvArray = nvSet.toArray();
        // 初始化分析表，行为非终结符，列为终结符
        analyzeTable = new String[nvArray.length + 1][ntArray.length + 1];

        // 输出一个占位符
        System.out.print("Nv/Nt" + "\t\t");
        analyzeTable[0][0] = "Nv/Nt";
        // 初始化行标
        for(int i =0; i < ntArray.length ;++i){
            if(ntArray[i].equals('ε')) {
                ntArray[i] = '#';
            }
            System.out.print(ntArray[i] + "\t\t");
            analyzeTable[0][i+1] = ntArray[i] + "";
        }

        System.out.println("");
        for(int i = 0; i < nvArray.length; ++i) {
            //列标初始化
            System.out.print(nvArray[i] + "\t\t");
            for(int j = 1; j < ntArray.length + 1; ++j) {
                String findUseExp = TextUtil.findUseExp(expressionMap, numOfNv, numOfNt, nvSet, ntSet, firstMap, followMap, i+1, j);
                analyzeTable[i][j] = findUseExp;
                System.out.print(findUseExp + "\t\t");
            }
            System.out.println();
        }
    }
    /**
     * 初始化预测分析表中的终结符和非终结符对应编号
     */
    private void init_nvntnum(){
        int num = 1;
        for(Character nvItem : nvSet) {
            numOfNv.put(nvItem, num++);
        }
        num = 1;
        for(Character ntItem : ntSet) {
            numOfNt.put(ntItem,num++);
        }
    }
    /**
     * 用于返回分析表中的产生式给调用处
     */
    public String searchAnalyzeTable(Character A, Character b) {
        //System.out.println(A + "\t\t" + b);
        if(b == '#') b = 'ε';
        int nvNum = numOfNv.get(A);
        int ntNum = numOfNt.get(b);
        if(!analyzeTable[nvNum - 1][ntNum].equals(" ")) {
            return analyzeTable[nvNum - 1][ntNum];
        }
        return null;
    }
}



