package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 这个类用于在表格里面展示所有的步骤
 */


public class AnalysisStep {

    AnalysisStep(Integer stepId, String stateStackStr, String inputStr, String symbolStr, String actionStr) {
        this.stepId = new SimpleIntegerProperty(stepId);
        this.stateStack = new SimpleStringProperty(stateStackStr);
        this.inputStack = new SimpleStringProperty(inputStr);
        this.symbolStr = new SimpleStringProperty(symbolStr);
        this.actionStr = new SimpleStringProperty(actionStr);

    }

    private final SimpleIntegerProperty stepId;
    private final SimpleStringProperty stateStack;
    private final SimpleStringProperty inputStack;
    private final SimpleStringProperty symbolStr;
    private final SimpleStringProperty actionStr;

    public SimpleIntegerProperty stepIdProperty() {
        return stepId;
    }

    public SimpleStringProperty actionStrProperty() {
        return actionStr;
    }

    public SimpleStringProperty inputStackProperty() {
        return inputStack;
    }

    public SimpleStringProperty stateStackProperty() {
        return stateStack;
    }

    public SimpleStringProperty symbolStrProperty() {
        return symbolStr;
    }
}
