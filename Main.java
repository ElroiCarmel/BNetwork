import java.io.*;
import java.util.*;


public class Main {
    public static void main(String[] args) {
        String p = "input.txt";
        BayesianNetwork bn;
        try (BufferedReader br = new BufferedReader(new FileReader(p))) {
            String line = br.readLine();
            bn = new BayesianNetwork(line);
            line = br.readLine();
            while (line != null) {
                String ans = bn.answer(line);
                System.out.println("Question: \"" + line + "\" Answer: " + ans);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void printLinks(Variable v) {
        boolean isPrinted = false;
        List<Variable> parents = v.getParents();
        if (parents != null && !parents.isEmpty()) {
            isPrinted = true;
            System.out.print("Parents: ");
            for (Variable parent : parents) {
                System.out.print(parent.getName() + ", ");
            }

        }
        List<Variable> children = v.getChildren();
        if (children != null && !children.isEmpty()) {
            isPrinted = true;
            System.out.print("Children: ");
            for (Variable child : children) {
                System.out.print(child.getName() + ", ");
            }
        }
        if (isPrinted) System.out.println();
    }

    public static void printFactor(Factor f) {
        List<Variable> vars = f.getVariables();
        double[] table = f.getTable();
        System.out.println(vars);
        OutcomeIterator it = OutcomeIterator.getInstance(vars);
        int i = 0;
        while (it.hasNext()) {
            String[] state = it.next();
            String s = Arrays.toString(state) + " P: " + table[i++];
            System.out.println(s);
        }
    }

    public static void printFactor(Factor... f) {
        for (Factor fc: f) {
            printFactor(fc);
        }
    }

}

