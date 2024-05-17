import java.lang.reflect.Array;
import java.util.*;

public class Factor {
    // DATA
    private ArrayList<Variable> variables; // The ORDER of the vars is crucial
    private double[] probTable;
    private int[] indexScale;

    // CONSTRUCTORS
    public Factor(List<Variable> variables, double[] prob) {
        this.variables = new ArrayList<>(variables);
        this.probTable = Arrays.copyOf(prob, prob.length);
        setIndexScale();
    }

    public Factor(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
        int len = 1;
        for (Variable v : variables) {
            len = len * v.size();
        }
        this.probTable = new double[len];
        setIndexScale();
    }

    private void setIndexScale() {
        int len = variables.size();
        this.indexScale = new int[len];
        int product = 1;
        for (int i = len - 1; i >= 0 ; i--) {
            this.indexScale[i] = product;
            product *= this.variables.get(i).size();
        }
    }

    // METHODS

    private int getIndex(List<String> state) {
        int[] indices = new int[state.size()];
        for (int i = 0; i < state.size(); i++) {
            String s = state.get(i);
            Variable v = this.variables.get(i);
            indices[i] = v.getOutcomeIndex(s);
        }
        int ans = 0;
        for (int i = 0; i < state.size(); i++) {
            ans += indices[i] * this.indexScale[i];
        }
        return ans;
    }

    public double getProb(List<String> state) {
        int index = getIndex(state);
        return this.probTable[index];
    }

    public void setProb(List<String> state, double num) {
        int index = getIndex(state);
        this.probTable[index] = num;
    }

    public Factor restrict(Variable var, String state) {
        if (this.variables.size() == 1) return null;

        // Copy factor variables to a list and remove the observed var
        List<Variable> f2Vars = new LinkedList<>(this.variables);
        int varIndex = this.variables.indexOf(var);
        f2Vars.remove(varIndex);
        Factor ans = new Factor(f2Vars);

        List<String> original = new LinkedList<>(), generated = new LinkedList<>();

        OutcomeIterator it = OutcomeIterator.getInstance(f2Vars);
        while (it.hasNext()) {
            String[] curr = it.next();
            for (int i = 0; i < curr.length ; i++) {
                generated.add(curr[i]);
                original.add(curr[i]);
            }
            original.add(varIndex, state);
            double retrieved = this.getProb(original);
            ans.setProb(generated, retrieved);
            original.clear();
            generated.clear();
        }
        return ans;
    }

    private Factor sumOut(Variable var) {
        return null;
    }

    private Factor multiply(Factor f) {
        return null;
    }

    private Factor normalize() {
        return null;
    }

    public double[] getTable() {
        return this.probTable;
    }

    public List<Variable> getVariables() {
        return this.variables;
    }

    public int size() {
        return this.probTable.length;
    }
}
