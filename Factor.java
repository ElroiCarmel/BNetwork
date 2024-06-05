import java.util.*;
import java.util.stream.Collectors;

public class Factor implements Comparable<Factor> {
    // DATA
    private ArrayList<Variable> variables; // The ORDER of the vars is crucial
    private double[] probTable;

    // CONSTRUCTORS
    public Factor(List<Variable> variables, double[] prob) {
        this.variables = new ArrayList<>(variables);
        this.probTable = Arrays.copyOf(prob, prob.length);
    }

    public Factor(List<Variable> variables) {
        this.variables = new ArrayList<>(variables);
        int len = 1;
        for (Variable v : variables) {
            len = len * v.size();
        }
        this.probTable = new double[len];
    }

    // Copy constructor
    public Factor(Factor f){
        this.variables = new ArrayList<>(f.variables);
        this.probTable = Arrays.copyOf(f.probTable, f.probTable.length);
    }

    // METHODS

    private int getIndex(List<String> state) {
        int ans = 0, i = 0;
        Iterator<String> strIt = state.iterator();
        while (strIt.hasNext() && i < variables.size() - 1) {
            int ind = variables.get(i).getOutcomeIndex(strIt.next());
            ans = variables.get(i+1).size() * (ans + ind);
            i++;
        }
        ans += variables.get(i).getOutcomeIndex(strIt.next());
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
            for (String s : curr) {
                generated.add(s);
                original.add(s);
            }
            original.add(varIndex, state);
            double retrieved = this.getProb(original);
            ans.setProb(generated, retrieved);
            original.clear();
            generated.clear();
        }
        return ans;
    }

    public Factor sumOut(Variable var) {
        if (this.variables.size() == 1) return null;

        List<Variable> f2Vars = new LinkedList<>(this.variables);
        int varIndex = this.variables.indexOf(var);
        f2Vars.remove(varIndex);
        Factor ans = new Factor(f2Vars);

        List<String> helper = new LinkedList<>();
        List<String> varOutcomes = var.getOutcomes();
        Iterator<String> varOutcomeIT;

        OutcomeIterator it = OutcomeIterator.getInstance(f2Vars);
        while (it.hasNext()) {
            varOutcomeIT = varOutcomes.iterator();
            String[] curr = it.next();
            double pr = 0;
            helper.addAll(Arrays.asList(curr));
            while (varOutcomeIT.hasNext()) {
                String outcome = varOutcomeIT.next();
                helper.add(varIndex, outcome);
                pr += getProb(helper);
                helper.remove(varIndex);
            }
            ans.setProb(helper, pr);
            helper.clear();
        }
        return ans;
    }

    /**
     * A simple multiply a chain of factors
     * @param factors The factors we want to reduce to one factor
     * @return The total amount of multiplications needed in the process
     */
    public static int multiply(Queue<Factor> factors) {
        int ans = 0;
        while (factors.size() > 1) {
            Factor f1 = factors.remove(), f2 = factors.remove();
            Factor product = f1.multiply(f2);
            ans += product.size();
            factors.add(product);
        }
        return ans;
    }

    public Factor multiply(Factor f) {
        Set<Variable> union = new LinkedHashSet<>(this.variables); union.addAll(f.variables);
        List<Variable> unionL = new LinkedList<>(union);
        Factor ans = new Factor(unionL);

        List<Integer> leftIndices = new LinkedList<>(), rightIndices = new LinkedList<>();
        Iterator<Variable> leftIT = this.variables.iterator(), rightIT = f.variables.iterator();
        while (leftIT.hasNext()) {
            leftIndices.add(unionL.indexOf(leftIT.next()));
        }
        while (rightIT.hasNext()) {
            rightIndices.add(unionL.indexOf(rightIT.next()));
        }

        OutcomeIterator it = OutcomeIterator.getInstance(unionL);
        List<String> leftOutcome = new LinkedList<>(), rightOutcome = new LinkedList<>(), unionOutcome = new ArrayList<>();

        while (it.hasNext()) {
            String[] outcome = it.next();
            unionOutcome.addAll(Arrays.asList(outcome));
            for (Integer leftIndex : leftIndices) {
                leftOutcome.add(outcome[leftIndex]);
            }

            for (Integer rightIndex : rightIndices) {
                rightOutcome.add(outcome[rightIndex]);
            }
            double p1 = this.getProb(leftOutcome), p2 = f.getProb(rightOutcome);
            ans.setProb(unionOutcome, p1 * p2);
            leftOutcome.clear(); rightOutcome.clear(); unionOutcome.clear();
        }

        return ans;
    }

    public Factor normalize() {
        Factor ans = new Factor(this);
        double sum = Arrays.stream(ans.probTable).sum();
        ans.probTable = Arrays.stream(ans.probTable).map(x -> x / sum).toArray();
        return ans;
    }

    public int size() {
        return this.probTable.length;
    }

    public boolean contains(Variable v) {
        return variables.contains(v);
    }

    public double getProb(Map<Variable, String> q) {
        List<String> helper = new LinkedList<>();
        for (Variable v : this.variables) {
            String state = q.get(v);
            helper.add(state);
        }
        return this.getProb(helper);
    }

    @Override
    public String toString() {
        String temp = "Variables: ";
        temp += variables.stream().map(Variable::getName).collect(Collectors.joining(", "));
        StringJoiner sj = new StringJoiner("\n");
        sj.add(temp);
        OutcomeIterator it = OutcomeIterator.getInstance(this.variables);
        int i = 0;
        while (it.hasNext()) {
            String[] state = it.next();
            String s = Arrays.toString(state) + " -> " + String.format("%.5f", probTable[i++]);
            sj.add(s);
        }
        return sj.toString();
    }

    @Override
    public int compareTo(Factor other) {
        if (this.size() == other.size()) {
            int f1 = this.variables.stream().map(Variable::getName).flatMapToInt(String::chars).sum();
            int f2 = other.variables.stream().map(Variable::getName).flatMapToInt(String::chars).sum();
            return f1 - f2;
        }
        return this.size() - other.size();
    }
}
