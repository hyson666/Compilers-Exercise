package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.INACTIVE;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class Controller {

    private Stage stage;

    public Button openButton;
    public Button startButton;
    public TextArea inputArea;
    public TextField inputStr;

    /**
     * 定义状态表的显示的变量
     */
    public TableView<ViewStateItem> stateTableView;
    public TableColumn<ViewStateItem, String> stateIdColumn;
    public TableColumn<ViewStateItem, String> projectItemsColumn;
    public TableColumn<ViewStateItem, String> goNodesColumn;

    /**
     * 定义Action、GOTO表变量
     */
    public TableView<ViewActionGoto> actionGotoTableView;

    /**
     * 定义主控程序分析表
     */
    public TableView<AnalysisStep> mainControlTable;
    public TableColumn<AnalysisStep, String> stepIdColumn;
    public TableColumn<AnalysisStep, String> stateStackColumn;
    public TableColumn<AnalysisStep, String> symbolStackColumn;
    public TableColumn<AnalysisStep, String> inputStrColumn;
    public TableColumn<AnalysisStep, String> actionColumn;


    private ArrayList<String> gsArrary = new ArrayList<String>();
    private Grammer lr1;
    private MainControl mainControl;


    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT","*.txt")
        );
        File filePath = fileChooser.showOpenDialog(stage);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath));
            BufferedReader br = new BufferedReader(reader);
            String temp = null;
            while((temp = br.readLine()) != null) {
                inputArea.appendText(temp);
                inputArea.appendText("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static void initGs(ArrayList<String> gsArray, TextArea inputArea) {
        String temp = inputArea.getText();
        String lines[] = temp.split("\\r?\\n");
        Collections.addAll(gsArray, lines);
    }


    public void startAnalyze() {
        initGs(gsArrary, inputArea);
        lr1 = new Grammer(gsArrary);
        showStateTable();
        showActionGotoTable();
        mainControl = new MainControl(inputStr.getText(), lr1);
        showMainControlTable();
    }


    /**
     * 新建状态表，先建立表类，传入再一一绑定数据
     */
    private void showStateTable() {
        final ObservableList<ViewStateItem> data = FXCollections.observableArrayList();
        for(Integer stateId : lr1.statesMap.keySet()) {
            data.add(new ViewStateItem(stateId, lr1.statesMap));
        }
        stateIdColumn.setCellValueFactory(new PropertyValueFactory<ViewStateItem, String>("stateId"));
        projectItemsColumn.setCellValueFactory(new PropertyValueFactory<ViewStateItem, String>("projects"));
        goNodesColumn.setCellValueFactory(new PropertyValueFactory<ViewStateItem, String>("nodes"));
        stateTableView.setItems(data);
    }

    /**
     * 打印ACTION、GOTO表
     * 由于是动态生成，不再全局定义表单项
     */
    private void showActionGotoTable() {
        // 一定要记得初始化
        actionGotoTableView.getColumns().clear();
        // 表单项
        TableColumn<ViewActionGoto, String> stateIdColumn2 = new TableColumn<ViewActionGoto, String>("状态");
        TableColumn<ViewActionGoto, String> actionColumn = new TableColumn<ViewActionGoto, String>("ACTION");
        TableColumn<ViewActionGoto, String> gotoColumn = new TableColumn<ViewActionGoto, String>("GOTO");

        // 首先对ACTION区进行定义
        final ObservableList<ViewActionGoto> data = FXCollections.observableArrayList();
        TreeSet<Character> ntSet = new TreeSet<Character>();
        ntSet.addAll(lr1.ntSet);
        ntSet.add('#');
        ArrayList<TableColumn<ViewActionGoto, String>> columnsArray = new ArrayList<TableColumn<ViewActionGoto, String>>();
        int cnt = 0;
        for(Character ntItem : ntSet) {
            columnsArray.add(new TableColumn<ViewActionGoto, String>(ntItem.toString()));
            actionColumn.getColumns().add(columnsArray.get(cnt));
            cnt++;
        }
        for(Integer i = 0; i < ntSet.size(); ++i) {
            Integer num = i + 1;
            columnsArray.get(i).setCellValueFactory(new PropertyValueFactory<ViewActionGoto, String>("actionItem" + num.toString()));
        }

        // 再对GOTO区进行定义
        cnt = 0;
        ArrayList<TableColumn<ViewActionGoto, String>> columnsArray2 = new ArrayList<TableColumn<ViewActionGoto, String>>();
        for(Character nvItem : lr1.nvSet) {
            columnsArray2.add(new TableColumn<ViewActionGoto, String>(nvItem.toString()));
            gotoColumn.getColumns().add(columnsArray2.get(cnt));
            cnt++;
        }
        for(Integer i = 0; i < lr1.nvSet.size(); ++i) {
            Integer num = i + 1;
            columnsArray2.get(i).setCellValueFactory(new PropertyValueFactory<ViewActionGoto, String>("gotoItem" + num.toString()));
        }

        //计算数据
        for(Integer stateId : lr1.statesMap.keySet()) {
            data.add(new ViewActionGoto(stateId, lr1));
        }

        //绑定ID
        stateIdColumn2.setCellValueFactory(new PropertyValueFactory<ViewActionGoto, String>("stateid"));
        actionGotoTableView.getColumns().addAll(stateIdColumn2, actionColumn, gotoColumn);
        actionGotoTableView.setItems(data);
    }

    /**
     * 打印主控分析表
     */
    void showMainControlTable() {
        final ObservableList<AnalysisStep> data = FXCollections.observableArrayList();
        data.addAll(mainControl.analysisSteps);
        // 绑定数据
        stepIdColumn.setCellValueFactory(new PropertyValueFactory<AnalysisStep, String>("stepId"));
        stateStackColumn.setCellValueFactory(new PropertyValueFactory<AnalysisStep, String>("stateStack"));
        inputStrColumn.setCellValueFactory(new PropertyValueFactory<AnalysisStep, String>("inputStack"));
        symbolStackColumn.setCellValueFactory(new PropertyValueFactory<AnalysisStep, String>("symbolStr"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<AnalysisStep, String>("actionStr"));
        mainControlTable.setItems(data);
    }
}
