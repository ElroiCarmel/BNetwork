import java.util.*;

public abstract class QueryFactory {

    public static ProQuery parseToProQuery(String s, BayesianNetwork bn) throws NoSuchElementException{
        ProQuery ans = new ProQuery();
        int i = s.indexOf('('), j = s.indexOf('|'), k = s.indexOf(')');
        String targetStr = s.substring(i+1, j), evidenceStr = s.substring(j+1, k);
        String[] tarSplt = targetStr.split("=");
        Variable tarVar = bn.getVar(tarSplt[0]);
        if (tarVar == null) throw new NoSuchElementException();
        ans.setTarget(tarVar, tarSplt[1]);
        String[] evSplt = evidenceStr.split(",");
        for (String ev : evSplt) {
            String[] split = ev.split("=");
            Variable evidVar = bn.getVar(split[0]);
            if (evidVar == null) throw new NoSuchElementException();
            ans.addEvidence(evidVar, split[1]);
        }
        if (k < s.length() - 2) {
            String hidStr = s.substring(k+2);
            String[] hidSplt = hidStr.split("-");
            for (String hid : hidSplt) {
                Variable hidVar = bn.getVar(hid);
                if (hidVar == null) throw new NoSuchElementException();
                ans.addHidden(hidVar);
            }
        }
        return ans;
    }

    public static IndQuery parseToIndQuery(String s, BayesianNetwork bn) throws NoSuchElementException{
        int i = s.indexOf("|");
        String targetStr = s.substring(0, i);
        IndQuery ans = new IndQuery();
        String[] targetSplt = targetStr.split("-");
        Variable[] tarVars = new Variable[2];
        int j = 0;
        for (String tar : targetSplt) {
            Variable v = bn.getVar(tar);
            if (v == null) throw new NoSuchElementException();
            tarVars[j++] = v;
        }
        ans.setTwoVars(tarVars);
        if (i < s.length() - 1) {
            String obs = s.substring(i+1);
            String[] obsSplt = obs.split(",");
            for (String obsStr : obsSplt) {
                String varName = obsStr.split("=")[0];
                Variable v = bn.getVar(varName);
                if (v == null) throw new NoSuchElementException();
                ans.addObserved(v);
            }
        }
        return ans;
    }


}

class IndQuery {
    private Variable[] vars;
    private List<Variable> observed = null;

    public IndQuery() {
        this.vars = new Variable[2];
    }

    public void setTwoVars(Variable v1, Variable v2) {
        this.vars[0] = v1; this.vars[1] = v2;
    }

    public void setTwoVars(Variable[] vars) {
        setTwoVars(vars[0], vars[1]);
    }

    public void addObserved(Variable v) {
        if (observed == null) observed = new LinkedList<>();
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
    private Queue<Variable> hidden = null;

    public ProQuery() {
        this.target = new HashMap<>();
        this.evidence = new HashMap<>();
    }

    public void setTarget(Variable var, String outcome) {
        this.target.put(var, outcome);
    }

    public void addEvidence(Variable v, String outcome) {
        this.evidence.put(v, outcome);
    }

    public void addHidden(Variable v) {
        if (hidden == null) hidden = new LinkedList<>();
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

class ProResult {
    private double probability;
    private int mul, add;

    public ProResult(double probability, int multiplication, int additions) {
        this.probability = probability;
        this.mul = multiplication;
        this.add = additions;
    }


    @Override
    public String toString() {
        return String.format("%.5f,%d,%d", this.probability, this.add, this.mul);
    }
}