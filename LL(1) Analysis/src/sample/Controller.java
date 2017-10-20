package sample;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.collections.ObservableList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.Property;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class Controller {
    /**
     * 按钮一定要实例化！！按钮定义！！
     */
    public Button analyzeButton;
    public Button openButton;
    /**
     * 预测表数据定义
     */
    public TableView<stepItem> showTable;
    public TableColumn<stepItem, Integer> indexColumn;
    public TableColumn<stepItem, String> stackColumn;
    public TableColumn<stepItem, String> strColumn;
    public TableColumn<stepItem, String> procColumn;
    public TableColumn<stepItem, String> actionColumn;
    /**
     * First与Follow表数据定义
     */
    public TableView<FirstFollowItem> FirstFollowTable;
    public TableColumn<FirstFollowItem, String> nvColumn;
    public TableColumn<FirstFollowItem, String> firstColumn;
    public TableColumn<FirstFollowItem, String> followColumn;
    /**
     * 预测分析表数据定义
     */
    public TableView<AnalyzeShowItem> AnalyzeShowTable;
    public TableColumn<AnalyzeShowItem, String> keyItem;
    public TableColumn<AnalyzeShowItem, String> lpar;
    public TableColumn<AnalyzeShowItem, String> rpar;
    public TableColumn<AnalyzeShowItem, String> pow;
    public TableColumn<AnalyzeShowItem, String> plus;
    public TableColumn<AnalyzeShowItem, String> minus;
    public TableColumn<AnalyzeShowItem, String> iden;
    public TableColumn<AnalyzeShowItem, String> shu;
    public TableColumn<AnalyzeShowItem, String> sharp;
    /**
     * 数据容器定义
     * 1.分析表 2.FirstFollow表 3.预测分析表
     */
    private final ObservableList<stepItem> data = FXCollections.observableArrayList();
    private final ObservableList<FirstFollowItem> ffdata = FXCollections.observableArrayList();
    private final ObservableList<AnalyzeShowItem> excdata = FXCollections.observableArrayList();
    /**
     * 定义文法输入区、字符串输入区
     */
    public TextArea inputArea;
    public TextField inputStr;


    public void showT (){
        System.out.println("Hello");
    }

    /**
     * 进行分析操作,包含表的初始建立
     */

    public void analyze() {
        // 清空表格
        data.clear();
        ffdata.clear();
        excdata.clear();
        // 调试代码
        ArrayList<String> gsArray = new ArrayList<String>();
        Grammer gs = new Grammer();
        // 赋测试集，初始化预测表
        initGs(gsArray, inputArea);
        gs.setGsArray(gsArray);
        gs.getNvNt();
        gs.init_exp_maps();
        gs.get_first();
        gs.get_follow();
        showNvNt(gs, ffdata);
        gs.genAnalyzeTable();
        // 调用分析器进行分析
        Analyzer LL1 = new Analyzer();
        LL1.initLL1(gs);
        //LL1.setString("i+i*i#");
        LL1.setString(inputStr.getText());
        LL1.analyze();
        for(AnalyzeStep step : LL1.analyzeSteps) {
            System.out.println(step.index + "\t\t" + step.analyzeStackStr + "\t\t" + step.str + "\t\t"
                    + step.useExpStr + "\t\t" + step.action);
            // 初始化可视表
            data.add(new stepItem(step.index,step.analyzeStackStr, step.str, step.useExpStr, step.action));
        }
        /**
         * 设置分析表单（妙啊！）
         */
        indexColumn.setCellValueFactory(new PropertyValueFactory<stepItem, Integer>("index"));
        stackColumn.setCellValueFactory(new PropertyValueFactory<stepItem, String>("analyzeStackStr"));
        strColumn.setCellValueFactory(new PropertyValueFactory<stepItem, String>("str"));
        procColumn.setCellValueFactory(new PropertyValueFactory<stepItem, String>("useExpStr"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<stepItem, String>("action"));
        showTable.setItems(data);
        /**
         * 设置First，Follow集表单
         */
        nvColumn.setCellValueFactory(new PropertyValueFactory<FirstFollowItem, String>("nvItem"));
        firstColumn.setCellValueFactory(new PropertyValueFactory<FirstFollowItem, String>("firstStr"));
        followColumn.setCellValueFactory(new PropertyValueFactory<FirstFollowItem, String>("followStr"));
        FirstFollowTable.setItems(ffdata);
        /**
         * 设置预测分析表
         */
        // 首先确定各个算符的编号
        gs.analyzeTable[0][0] = " ";
        int lpar_index = 0, rpar_index = 0, pow_index = 0, plus_index = 0, minus_index = 0, iden_index= 0, shu_index = 0,
                sharp_index = 0;
//        if(gs.ntSet.contains("+")) {
//            plus_index = gs.numOfNt.get("+");
//        }
//        if(gs.ntSet.contains("-")) {
//            minus_index = gs.numOfNt.get("-");
//        }
//        if(gs.ntSet.contains("/")) {
//            shu_index = gs.numOfNt.get("/");
//        }
//        if(gs.ntSet.contains("*")) {
//            pow_index = gs.numOfNt.get("*");
//        }
//        if(gs.ntSet.contains("(")) {
//            lpar_index = gs.numOfNt.get("(");
//        }
//        if(gs.ntSet.contains(")")) {
//            rpar_index = gs.numOfNt.get(")");
//        }
//        if(gs.ntSet.contains("i")) {
//            iden_index = gs.numOfNt.get("i");
//        }
//        if(gs.ntSet.contains("#")) {
//            sharp_index = gs.numOfNt.get("#");
//        }

        for(int i = 0; i< gs.nvSet.size(); ++i) {
            String keyItem = gs.nvArray[i].toString();
            excdata.add(new AnalyzeShowItem(keyItem, gs.analyzeTable[i][1], gs.analyzeTable[i][2],
                    gs.analyzeTable[i][3], gs.analyzeTable[i][4],gs.analyzeTable[i][5], gs.analyzeTable[i][6],
                    gs.analyzeTable[i][7], gs.analyzeTable[i][8]));
        }
//        private final SimpleStringProperty keyItem;
//        private final SimpleStringProperty lpar;
//        private final SimpleStringProperty rpar;
//        private final SimpleStringProperty pow;
//        private final SimpleStringProperty plus;
//        private final SimpleStringProperty iden;
//        private final SimpleStringProperty shu;
//        private final SimpleStringProperty sharp;
        keyItem.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("keyItem"));
        lpar.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("lpar"));
        rpar.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("rpar"));
        pow.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("pow"));
        plus.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("plus"));
        minus.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("minus"));
        iden.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("iden"));
        shu.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("shu"));
        sharp.setCellValueFactory(new PropertyValueFactory<AnalyzeShowItem, String>("sharp"));
        AnalyzeShowTable.setItems(excdata);

    }

    private static void initGs(ArrayList<String> gsArray, TextArea inputArea) {
//        gsArray.add("E->TG");
//        gsArray.add("G->+TG|ε");
//        gsArray.add("T->FS");
//        gsArray.add("S->*FS|ε");
//        gsArray.add("F->(E)|i");
        String temp = inputArea.getText();
        String lines[] = temp.split("\\r?\\n");
        for(int i = 0; i < lines.length; ++i){
            //System.out.println(lines[i]);
            gsArray.add(lines[i]);
        }
    }

    private static void showNvNt(Grammer gs, ObservableList ffdata) {
        // 迭代器可以实现自动匹配;
        Iterator iter;
        // 先打印Follow集
        ArrayList<Character> keyList = new ArrayList<Character>();
        keyList.addAll(gs.nvSet);
        // 记录符串，为表格展示做准备
        String firstStr, followStr;
        for(Character keyItem : keyList) {
            firstStr = "";
            followStr = "";
            System.out.print(keyItem + ":   ");
            TreeSet<Character> firstList = new TreeSet<Character>();
            firstList = gs.firstMap.get(keyItem);
            TreeSet<Character> followList = new TreeSet<Character>();
            followList = gs.followMap.get(keyItem);
            iter = firstList.iterator();
            while(iter.hasNext()) {
                //System.out.print(iter.next() + " ");
                if(firstStr.equals("")) firstStr = firstStr + iter.next();
                else firstStr = firstStr + " , " + iter.next();
            }
            System.out.print(firstStr);
            System.out.print("      ||      ");
            iter = followList.iterator();
            while(iter.hasNext()) {
                //System.out.print(iter.next() + " ");
                if(followStr.equals("")) followStr = followStr + iter.next();
                followStr = followStr + " , " + iter.next();
            }
            System.out.print(followStr);
            System.out.print('\n');
            String keyStr = keyItem.toString();
            ffdata.add(new FirstFollowItem(keyStr, firstStr, followStr));
        }
    }
}
