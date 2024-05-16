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
            product *= variables.get(i).size();
        }
    }

    // METHODS

    private int getIndex(List<String> state) {
        int[] indices = new int[state.size()];
        for (int i = 0; i < state.size(); i++) {
            String s = state.get(i);
            Variable v = variables.get(i);
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


    public double[] getTable() {
        return this.probTable;
    }

    public List<Variable> getVariables() {
        return this.variables;
    }
}
