package sample;

import sun.reflect.generics.tree.Tree;

import java.util.*;

/**
 * 为创建Follow集提供的字符串处理方法集，针对了下列几种情况：
 * 1.A是E（开始符）
 * 2.存在一产生式...AB...,则follow(A)中加入first(B)
 * 3.存在一产生式...Ab...,则follow(A)中加入b
 * 4.存在一产生式B->...aAB..,而且B可以为空,则follow(A)中加入follow(B)
 * 5.存在一产生式C->...AB,则直接把follow(C)加入follow(B)中
 */

/**
 * 参数说明
 * nowProc: 当前产生式
 * A: 当前正在处理的非终结符
 * b: 在非终结符后面紧接的终结符
 */

public class TextUtil {
    /**
     *  判断是否存在情况2
     */
    public static boolean containsAB(TreeSet<Character> nvSet, String nowProc, Character A) {
        String A_str = A.toString();
        if (nowProc.contains(A_str)) {
            int A_index = nowProc.indexOf(A_str);
            char findChar = 0;
            if (A_index + 1 < nowProc.length()) {
                findChar = nowProc.charAt(A_index + 1);
            } else {
                return false;
            }
            if (nvSet.contains(findChar)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否存在情况3
     * 应用函数：Str.contains()直接寻找是否存在子串
     *           Str.substring()直接提取候选..Ab..,再判断是否符合
     */
    public static boolean containsAb(TreeSet<Character> ntSet, String nowProc, Character A) {
        String A_Str = A.toString();
        if (nowProc.contains(A_Str)) {
            int A_Index = nowProc.indexOf(A_Str);
//            System.out.println(A);
//            System.out.println(nowProc);
//            System.out.println(A_Index);
            char findChar = 0;
            if(A_Index + 1 < nowProc.length()) {
                findChar = nowProc.charAt(A_Index + 1);
//                System.out.println(findChar);
            }
            if(ntSet.contains(findChar)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否存在情况4
     */
    public  static boolean containsAB_BIsNUll(TreeSet<Character> nvSet, String nowProc, Character A,
                                              HashMap<Character, ArrayList<String>> expressionMap)
    {
        String A_Str = A.toString();
        if(containsAB(nvSet, nowProc, A)) {
            Character B = getNextToA(nowProc, A);
            if(B == null) return false;
            ArrayList<String> arrayListOfB = expressionMap.get(B);
            if(arrayListOfB.contains("ε")) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否存在情况5
     * 即最后一个字符是非终结符，而且与产生它的非终结符本身不等
     */
    public static boolean containbA(TreeSet<Character> nvSet, String nowProc, Character A) {
        String A_str = A.toString();
        Character lastChar = nowProc.charAt(nowProc.length() - 1);
        if (lastChar.equals(A)) {
            return true;
        }
        return false;
    }
    /**
     * 判断AB_BIsNULL已处理串后面是否是终结符
     */
    public static boolean cpIsOver(String nowCP, String nowProc, TreeSet<Character> ntSet) {
        int nowCP_index = nowProc.indexOf(nowCP);
        if (ntSet.contains(nowProc.charAt(nowCP_index + 1)) || nowCP_index + 1 >= nowProc.length() ) {
            return true;
        }
        return false;
    }
    /**
     * 获取下一个处理符号的位置
     */
    public static int nowIndex (String nowCP, String nowProc) {
        int length = nowCP.length();
        int index = nowProc.indexOf(nowCP);
        return index + length - 1;
    }
    /**
     * 获取A后面的字符
     */
    public static Character getNextToA(String nowProc, Character A) {
        String A_str = A.toString();
        if(nowProc.contains(A_str)) {
            int A_index = nowProc.indexOf(A_str);
            if (A_index + 1 < nowProc.length()) {
                return nowProc.charAt(A_index + 1);
            } else {
                return null;
            }
        }
        return null;
    }
    /**
     * 获取该行列，所选用的字符串，如果没有则返回一个空串，有则返回对应表达式
     * //TODO:把FOLLOW集补上去！
     */
    public static String findUseExp(HashMap<Character, ArrayList<String>> expressionMap, HashMap<Character, Integer>
                                    numOfNv, HashMap<Character, Integer> numOfNt, TreeSet<Character> nvSet,
                                    TreeSet<Character> ntSet,HashMap<Character, TreeSet<Character>> firstMap ,
                                    HashMap<Character, TreeSet<Character>> followMap, int col , int row)
    {
        // 查找对应的非终结符
        char nvChar = 0,ntChar = 0;
        Iterator iter = numOfNv.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if((int)val == col) {
                nvChar = (char)key;
                break;
            }
        }
        // 查找对应终结符
        iter = numOfNt.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if((int)val == row) {
                ntChar = (char)key;
                break;
            }
        }
        // 由First集处理
        ArrayList<String> proc_array = expressionMap.get(nvChar);
        for(String proc_item : proc_array) {
            Character first = proc_item.charAt(0);
            if(first == ntChar) {
                return nvChar + "->" + proc_item;
            } else if (nvSet.contains(first)) {
                TreeSet<Character> firstSet = firstMap.get(first);
                if(firstSet.contains(ntChar)) {
                    return nvChar + "->" + proc_item;
                }
            }
        }
        // 由Follow集处理
        TreeSet<Character> followSet = followMap.get(nvChar);
        if(followSet.contains(ntChar)) return nvChar + "->ε";
        return " ";
    }
}
