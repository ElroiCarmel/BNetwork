import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Factor {
    // DATA
    private List<Variable> variables;
    private double[] prob;

    // CONSTRUCTORS
    public Factor(List<Variable> variables, double[] prob) {
        this.variables = new LinkedList<>(variables);
        this.prob = Arrays.copyOf(prob, prob.length);
    }

    // METHODS

    public double[] getProb() {
        return prob;
    }
}
