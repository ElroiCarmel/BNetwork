import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An Iterator implementation to create a straight-forward approach to iterate over
 * all possible permutations of a given group of variables. Useful mainly for operations
 * between 2 factors.
 * For example: Let {A,B,C} be Random variables so that the possible outcomes for each one is {T, F}.
 * The class will iterate from the [T,T,T], [T,T,F], .... till [F,F,F].
 */
public class OutcomeIterator implements Iterator<ArrayList<String>> {
    /**
     * The class uses the Singleton design pattern
     */
    private static final OutcomeIterator instance = new OutcomeIterator();
    private final ArrayList<Integer> currentState = new ArrayList<>();
    private final ArrayList<String> currentStateString = new ArrayList<>();
    private final ArrayList<Variable> currentVars = new ArrayList<>();



    public static OutcomeIterator getInstance(List<Variable> vars) {
        instance.configuration(vars);
        return instance;
    }

    /**
     * In order to reduce space-complexity the class will be using ArrayLists so before
     * the iterations begin the data-structures needs to be cleared and set at the right sizes
     * @param vars List of the variable to iterate over their outcomes
     */
    private void configuration(List<Variable> vars) {
        currentVars.clear(); currentVars.addAll(vars);
        currentStateString.clear();
        currentState.clear();
        for (int i = 0; i < vars.size(); i++) {
            currentState.add(0);
            currentStateString.add("-");
        }
        currentState.set(currentState.size() - 1, -1);
    }

    private OutcomeIterator() {}

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        for (int i = 0; i < currentState.size(); i++) {
            if (currentState.get(i) < currentVars.get(i).size() - 1) return true;
        }
        return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public ArrayList<String> next() throws NoSuchElementException {
        if (!hasNext()) throw new NoSuchElementException();
        increment();
        for (int i = 0; i < currentState.size(); i++) {
            String s = currentVars.get(i).getOutcomes().get(currentState.get(i));
            currentStateString.set(i, s);
        }
        return this.currentStateString;
    }

    /**
     * An iterative (not recursive) approach for iterating over the cartesian product of
     * the variables outcomes
     */
    private void increment() {
        for (int i = currentState.size() - 1; i >= 0; i--) {
            currentState.set(i, currentState.get(i) + 1);
            if (currentState.get(i) >= currentVars.get(i).size()) {
                currentState.set(i, 0);
            } else break;
        }
    }
}
