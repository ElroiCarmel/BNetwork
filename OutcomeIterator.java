import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class OutcomeIterator implements Iterator<int[]> {

    private static final OutcomeIterator instance = new OutcomeIterator();
    private int[] currentState, max;
    private int maxIterations, counter;
    private boolean hasNext;
    public static OutcomeIterator getInstance(List<Variable> vars) {
        instance.configuration(vars);
        return instance;
    }

    private void configuration(List<Variable> vars) {
        currentState = new int[vars.size()];
        currentState[currentState.length-1] = -1;
        hasNext = true;
        max = new int[vars.size()];
        maxIterations = 1;
        counter = 0;
        int i = 0;
        for (Variable var : vars) {
            max[i] = var.size();
            maxIterations *= max[i];
            i++;
        }
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
        return this.hasNext;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public int[] next() throws NoSuchElementException {
        if (!hasNext) throw new NoSuchElementException();
        for (int i = currentState.length - 1; i >= 0; i--) {
            currentState[i]++;
            if (currentState[i] >= max[i]) {
                currentState[i] = 0;
            } else break;
        }
        this.counter++;
        this.hasNext = this.counter < maxIterations;
        return this.currentState;
    }
}
