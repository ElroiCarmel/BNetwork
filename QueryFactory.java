import java.util.*;

public abstract class QueryFactory {
    public static void main(String[] args) {
        String test = "B-E|J=T,X=F";
        String[] split = test.split("\\|");
        String[] vars = split[0].split("-");
        System.out.println(Arrays.toString(vars));
        if (split.length > 1) {
            String[] obs = split[1].split("[=,]");
            System.out.println(Arrays.toString(obs));
        }
        List<Integer> l = new ArrayList<>();

    }


}

class IndQuery {
    private Variable[] vars;
    private List<Variable> observed;

    public IndQuery() {
        this.vars = new Variable[2];
        this.observed = new LinkedList<>();
    }

    public void setTwoVars(Variable v1, Variable v2) {
        this.vars[0] = v1; this.vars[1] = v2;
    }

    public void setTwoVars(Variable[] vars) {
        setTwoVars(vars[0], vars[1]);
    }

    public void addObserved(Variable v) {
        this.observed.add(v);
    }

    public List<Variable> getObserved() {
        return observed;
    }

    public Variable[] getVars() {
        return vars;
    }
}

class ProQuery {
    private HashMap<Variable, String> target, evidence;
    private Queue<Variable> hidden;

    public ProQuery() {
        this.target = new HashMap<>();
        this.evidence = new HashMap<>();
        this.hidden = new LinkedList<>();
    }

    public void setTarget(Variable var, String outcome) {
        this.target.put(var, outcome);
    }

    public void addEvidence(Variable v, String outcome) {
        this.evidence.put(v, outcome);
    }

    public void addHidden(Variable v) {
        this.hidden.add(v);
    }

    public HashMap<Variable, String> getTarget() {
        return target;
    }

    public HashMap<Variable, String> getEvidence() {
        return evidence;
    }

    public Queue<Variable> getHidden() {
        return hidden;
    }
}
