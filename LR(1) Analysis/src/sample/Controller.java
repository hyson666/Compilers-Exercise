package sample;


import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Controller {

    private Stage stage;

    public Button openButton;
    public Button startButton;
    public TextArea inputArea;

    private ArrayList<String> gsArrary = new ArrayList<String>();

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
        Grammer lr1 = new Grammer(gsArrary);
    }

}
