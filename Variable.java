import java.util.LinkedList;
import java.util.List;

public class Variable {
    // data
    private int ID;
    private String name;
    private List<Variable> parents, children;
    private List<String> outcomes;

    // constructor
    public Variable(int id, String name) {
        this.ID = id;
        this.name = name;
        this.parents = new LinkedList<>();
        this.children = new LinkedList<>();
        this.outcomes = new LinkedList<>();
    }
    //methods
    public void addParent(Variable var) {
        this.parents.add(var);
    }

    public void addChild(Variable var) {
        this.children.add(var);
    }

    public void addOutcome(String outcome) {
        this.outcomes.add(outcome);
    }
}
