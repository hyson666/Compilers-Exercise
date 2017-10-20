package sample;

import javafx.beans.property.*;

public class stepItem {
    private final SimpleIntegerProperty index;
    private final SimpleStringProperty analyzeStackStr;
    private final SimpleStringProperty str;
    private final SimpleStringProperty useExpStr;
    private final SimpleStringProperty action;

    public stepItem() {
        this(null, null, null, null, null);
    }

    stepItem(Integer now_index, String now_analyzeStackStr, String now_str, String now_useExpStr,
             String now_action){
        this.index = new SimpleIntegerProperty(now_index);
        this.analyzeStackStr = new SimpleStringProperty(now_analyzeStackStr);
        this.str = new SimpleStringProperty(now_str);
        this.useExpStr = new SimpleStringProperty(now_useExpStr);
        this.action = new SimpleStringProperty(now_action);
    }

    public StringProperty analyzeStackStrProperty() {
        return analyzeStackStr;
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public StringProperty actionProperty() {
        return action;
    }

    public StringProperty strProperty() {
        return str;
    }

    public StringProperty useExpStrProperty() {
        return useExpStr;
    }
}
