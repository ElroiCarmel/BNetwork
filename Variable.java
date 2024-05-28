import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Variable {
    // data
    private final int ID;
    private String name;
    private List<Variable> parents = null, children = null;
    private List<String> outcomes;
    private Factor cpt;

    // constructor
    public Variable(int id, String name) {
        this.ID = id;
        this.name = name;
        this.outcomes = new ArrayList<>();
    }
    //methods
    public void addParent(Variable var) {
        if (this.parents == null) this.parents = new LinkedList<>();
        this.parents.add(var);
    }

    public void addChild(Variable var) {
        if (this.children == null) this.children = new LinkedList<>();
        this.children.add(var);
    }

    public void addOutcome(String outcome) {
        this.outcomes.add(outcome);
    }

    public void setCpt(Factor cpt) {
        this.cpt = cpt;
    }

    public Factor getCpt() {
        return cpt;
    }

    public List<Variable> getParents() {
        return this.parents;
    }

    public List<Variable> getChildren() {
        return this.children;
    }

    public String getName() {
        return this.name;
    }

    public int getID() {
        return this.ID;
    }

    public int size() {
        return this.outcomes.size();
    }

    public List<String> getOutcomes() {
        return this.outcomes;
    }

    @Override
    public String toString() {
        return "Name: " + name + ". Outcomes: " + outcomes;
    }

    public int getOutcomeIndex(String s) {
        return this.outcomes.indexOf(s);
    }

    public boolean containState(String s) {
        return this.outcomes.contains(s);
    }
}
