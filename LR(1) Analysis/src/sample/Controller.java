package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Controller {

    private Stage stage;

    public Button openButton;
    public Button startButton;
    public TextArea inputArea;

    /**
     * 定义状态表的显示的变量
     */
    public TableView<ViewStateItem> stateTableView;
    public TableColumn<ViewStateItem, String> stateIdColumn;
    public TableColumn<ViewStateItem, String> projectItemsColumn;
    public TableColumn<ViewStateItem, String> goNodesColumn;


    private ArrayList<String> gsArrary = new ArrayList<String>();
    private Grammer lr1;


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
}
