package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;


/**
 * 主类用于测试以及生成GUI
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // 对窗口进行初始化
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("LL(1)文法分析器 By 黄一晟");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1450, 600));
        primaryStage.show();

        // LL(1)文法产生集合
        ArrayList<String> gsArray = new ArrayList<String>();
    }


    public static void main(String[] args) {
//        // 调试代码
//        ArrayList<String> gsArray = new ArrayList<String>();
//        Grammer gs = new Grammer();
//        // 赋测试集，初始化预测表
//        initGs(gsArray);
//        gs.setGsArray(gsArray);
//        gs.getNvNt();
//        gs.init_exp_maps();
//        gs.get_first();
//        gs.get_follow();
//        showNvNt(gs);
//        gs.genAnalyzeTable();
//        //调用分析器进行分析
//        Analyzer LL1 = new Analyzer();
//        LL1.initLL1(gs);
//        LL1.setString("i+i*i#");
//        LL1.analyze();
//        for(AnalyzeStep step : LL1.analyzeSteps) {
//            System.out.println(step.index + "\t\t" + step.analyzeStackStr + "\t\t" + step.str + "\t\t"
//                                + step.useExpStr + "\t\t" + step.action);
//        }
        launch(args);
    }

    private static void initGs(ArrayList<String> gsArray) {
        gsArray.add("E->TG");
        gsArray.add("G->+TG|ε");
        gsArray.add("T->FS");
        gsArray.add("S->*FS|ε");
        gsArray.add("F->(E)|i");
    }

    private static void showNvNt(Grammer gs) {
        // 迭代器可以实现自动匹配;
        Iterator iter;
        // 先打印Follow集
        ArrayList<Character> keyList = new ArrayList<Character>();
        keyList.addAll(gs.nvSet);
        for(Character keyItem : keyList) {
            System.out.print(keyItem + ":   ");
            TreeSet<Character> firstList = new TreeSet<Character>();
            firstList = gs.firstMap.get(keyItem);
            TreeSet<Character> followList = new TreeSet<Character>();
            followList = gs.followMap.get(keyItem);
            iter = firstList.iterator();
            while(iter.hasNext()) {
                System.out.print(iter.next() + " ");
            }
            System.out.print("      ||      ");
            iter = followList.iterator();
            while(iter.hasNext()) {
                System.out.print(iter.next() + " ");
            }
            System.out.print('\n');
        }
    }

}
