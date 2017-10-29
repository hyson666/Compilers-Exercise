package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * 根据当前状态，遍历Nv和Nt赋值
 * @author HYSON
 */

public class ViewActionGoto {
    ViewActionGoto(Integer stateId, Grammer lr1) {
        this.stateid = new SimpleIntegerProperty(stateId);
        this.lr1 = lr1;
        this.gotoFuncMap = lr1.gotoFuncMap;
        this.actionFuncMap = lr1.actionFuncMap;
        this.getActionStr();
        this.getGotoStr();
        this.actionItem1 = new SimpleStringProperty(actionStr[0]);
        this.actionItem2 = new SimpleStringProperty(actionStr[1]);
        this.actionItem3 = new SimpleStringProperty(actionStr[2]);
        this.actionItem4 = new SimpleStringProperty(actionStr[3]);
        this.actionItem5 = new SimpleStringProperty(actionStr[4]);
        this.actionItem6 = new SimpleStringProperty(actionStr[5]);
        this.gotoItem1 = new SimpleStringProperty(gotoStr[0]);
        this.gotoItem2 = new SimpleStringProperty(gotoStr[1]);
        this.gotoItem3 = new SimpleStringProperty(gotoStr[2]);
        this.gotoItem4 = new SimpleStringProperty(gotoStr[3]);
    }

    private Grammer lr1;
    private SimpleIntegerProperty stateid;
    private SimpleStringProperty actionItem1;
    private SimpleStringProperty actionItem2;
    private SimpleStringProperty actionItem3;
    private SimpleStringProperty actionItem4;
    private SimpleStringProperty actionItem5;
    private SimpleStringProperty actionItem6;
    private SimpleStringProperty gotoItem1;
    private SimpleStringProperty gotoItem2;
    private SimpleStringProperty gotoItem3;
    private SimpleStringProperty gotoItem4;
    public HashMap<Integer, HashMap<Character,Integer>> gotoFuncMap;
    public HashMap<Integer, HashMap<Character, String>> actionFuncMap;
    private String[] actionStr = new String[10];
    private String[] gotoStr = new String[10];

    private void getActionStr() {
        // 先构造Action集
        TreeSet<Character> ntSet = new TreeSet<Character>();
        ntSet.addAll(lr1.ntSet);
        ntSet.add('#');
        HashMap<Character, String> action = actionFuncMap.get(stateid.intValue());
        int cnt = 0;
        for(Character ntItem : ntSet) {
            if(action.containsKey(ntItem)) {
                actionStr[cnt++] = action.get(ntItem);
            } else {
                actionStr[cnt++] = " ";
            }
        }

        // 再构造Goto集
    }

    private void getGotoStr() {
        TreeSet<Character> nvSet = new TreeSet<Character>();
        nvSet.addAll(lr1.nvSet);
        HashMap<Character, Integer> gotoMap = gotoFuncMap.get(stateid.intValue());
        int cnt = 0;
        if(gotoMap == null) {
            // 注：只有非规约状态才有goto集，其余情况都是null！
            return;
        }
        for(Character nvItem : nvSet) {
            if(gotoMap.containsKey(nvItem)) {
                gotoStr[cnt++] = gotoMap.get(nvItem).toString();
            } else {
                gotoStr[cnt++] = " ";
            }
        }
    }

    public SimpleIntegerProperty stateidProperty() {
        return stateid;
    }

    public SimpleStringProperty actionItem1Property() {
        return actionItem1;
    }

    public SimpleStringProperty actionItem2Property() {
        return actionItem2;
    }

    public SimpleStringProperty actionItem3Property() {
        return actionItem3;
    }

    public SimpleStringProperty actionItem4Property() {
        return actionItem4;
    }

    public SimpleStringProperty actionItem5Property() {
        return actionItem5;
    }

    public SimpleStringProperty actionItem6Property() {
        return actionItem6;
    }

    public SimpleStringProperty gotoItem1Property() {
        return gotoItem1;
    }

    public SimpleStringProperty gotoItem2Property() {
        return gotoItem2;
    }

    public SimpleStringProperty gotoItem3Property() {
        return gotoItem3;
    }

    public SimpleStringProperty gotoItem4Property() {
        return gotoItem4;
    }
}
