package sample;

import javafx.beans.property.*;

public class FirstFollowItem {
    private final SimpleStringProperty nvItem;
    private final SimpleStringProperty firstStr;
    private final SimpleStringProperty followStr;

    public FirstFollowItem(String _nvItem, String _firstStr, String _followStr){
        this.nvItem = new SimpleStringProperty(_nvItem);
        this.firstStr = new SimpleStringProperty(_firstStr);
        this.followStr = new SimpleStringProperty(_followStr);
    }

    public FirstFollowItem() {
        this(null,null,null);
    }

    public StringProperty nvItemProperty () {
        return nvItem;
    }

    public StringProperty firstStrProperty() {
        return firstStr;
    }

    public StringProperty followStrProperty() {
        return followStr;
    }
}
