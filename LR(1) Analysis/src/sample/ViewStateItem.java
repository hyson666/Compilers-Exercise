package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class ViewStateItem {
    public ViewStateItem(Integer stateId, HashMap<Integer, State> statesMap) {
        this.state = statesMap.get(stateId);
        this.stateId = new SimpleIntegerProperty(stateId);
        this.projects = new SimpleStringProperty(projectsToString(this.state.projectSet, this.state.firstBaMap));
        this.nodes = new SimpleStringProperty(nodesToString(this.state.gotoSet));
    }

    // 定义要初始化的数据
    private State state;
    private final SimpleIntegerProperty stateId;
    private final SimpleStringProperty projects;
    private final SimpleStringProperty nodes;


    private String projectsToString (HashSet<Lr1project> projects, HashMap<Lr1project, TreeSet<Character>> firstBaMap){
        String retStr = "";
        // 插入点位的时候采用+3来跳过"X->"
        boolean firstDo = true;
        for(Lr1project projectItem : projects) {
            if (!firstDo) {
                retStr += "、";
            }
            firstDo = false;
            StringBuilder stringbuilder = new StringBuilder(projectItem.proc);
            stringbuilder.insert(projectItem.pos + 3, "·");
            retStr += stringbuilder.toString();
            retStr += ",";
            TreeSet<Character> firstBaSet = firstBaMap.get(projectItem);
            boolean firstTime = true;
            for(Character firstBaItem : firstBaSet) {
                if(firstTime) {
                    retStr += firstBaItem.toString();
                    firstTime = false;
                } else {
                    retStr = retStr + "/" + firstBaItem;
                }
            }
        }
        return retStr;
    }


    private String nodesToString (TreeSet<Integer> nodeSet){
        String retStr = "";
        boolean firstDo = true;
        for(Integer nodeId : nodeSet) {
            if (!firstDo) {
                retStr += ",";
            }
            firstDo = false;
            retStr += nodeId.toString();
        }
        return retStr;
    }

    public SimpleIntegerProperty stateIdProperty() {
        return stateId;
    }

    public SimpleStringProperty nodesProperty() {
        return nodes;
    }

    public SimpleStringProperty projectsProperty() {
        return projects;
    }

}
