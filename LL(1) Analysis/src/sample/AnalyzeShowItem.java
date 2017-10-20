package sample;

import javafx.beans.property.*;

/**
 * 这里都是按照列输出的,每一项记录一个产生式，汇集成表就可以了
 */
public class AnalyzeShowItem {
    private final SimpleStringProperty keyItem;
    private final SimpleStringProperty lpar;
    private final SimpleStringProperty rpar;
    private final SimpleStringProperty pow;
    private final SimpleStringProperty plus;
    private final SimpleStringProperty minus;
    private final SimpleStringProperty iden;
    private final SimpleStringProperty shu;
    private final SimpleStringProperty sharp;

    public AnalyzeShowItem (String keyStr,String lparStr,String rparStr, String powStr,String plusStr, String minusStr, String idenStr,
                            String shuStr, String sharpStr)
    {
        this.keyItem = new SimpleStringProperty(keyStr);
        this.lpar = new SimpleStringProperty(lparStr);
        this.rpar = new SimpleStringProperty(rparStr);
        this.pow = new SimpleStringProperty(powStr);
        this.plus = new SimpleStringProperty(plusStr);
        this.minus = new SimpleStringProperty(minusStr);
        this.iden = new SimpleStringProperty(idenStr);
        this.shu = new SimpleStringProperty(shuStr);
        this.sharp = new SimpleStringProperty(sharpStr);
    }

    public SimpleStringProperty idenProperty() {
        return iden;
    }

    public SimpleStringProperty keyItemProperty() {
        return keyItem;
    }

    public SimpleStringProperty lparProperty() {
        return lpar;
    }

    public SimpleStringProperty plusProperty() {
        return plus;
    }

    public SimpleStringProperty powProperty() {
        return pow;
    }

    public SimpleStringProperty rparProperty() {
        return rpar;
    }

    public SimpleStringProperty sharpProperty() {
        return sharp;
    }

    public SimpleStringProperty shuProperty() {
        return shu;
    }

    public SimpleStringProperty minusProperty() {
        return minus;
    }
}
