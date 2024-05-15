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
        this.outcomes = new LinkedList<>();
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

    @Override
    public String toString() {
        HashMap<String, List<String>> ans = new HashMap<>();
        ans.put(name, this.outcomes);
        return ans.toString();
    }
}
