import java.util.*;

public abstract class QueryFactory {

    public static void main(String[] args) {
        String test = "P(B=T|J=T,M=T)";
        System.out.println(Arrays.toString(test.split("\\) ")));


//        if (test.startsWith("P(")) {
//            test = test.substring(2);
//            String[] s2 = test.split("\\|");
//            String[] target = s2[0].split("=");
//            String[] s3 = s2[1].split("\\) ");
//            String[] ev = s3[0].split(",");
//            System.out.println(Arrays.toString(ev));
//        } else {
//
//        }

    }

    public static ProQuery parseToProQuery(String s, BayesianNetwork bn) {
        ProQuery ans = new ProQuery();
        s = s.substring(2);
        String[] s2 = s.split("\\|");
        String[] target = s2[0].split("=");
        String[] s3 = s2[1].split("\\) ");
        String[] ev = s3[0].split(",");
        String[] hid = s3[1].split("-");
        HashMap<Variable, String> targetHM = new HashMap<>();
        Variable targetVar = bn.getVar(target[0]);
        if (targetVar != null) ans.setTarget(targetVar, target[1]);
        for (String evstr : ev) {
            String[] splitted = evstr.split("=");
            Variable evVar = bn.getVar(splitted[0]);
            if (evVar != null) ans.addEvidence(evVar, splitted[1]);
        }
        for (String hidstr : hid) {
            Variable hidVar = bn.getVar(hidstr);
            if (hidVar != null) ans.addHidden(hidVar);
        }
        return ans;
    }

    public static IndQuery parseToIndQuery(String s, BayesianNetwork bn) {
        return null;
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

    @Override
    public String toString() {
        return "ProQuery{" +
                "target=" + target +
                ", evidence=" + evidence +
                ", hidden=" + hidden +
                '}';
    }
}
