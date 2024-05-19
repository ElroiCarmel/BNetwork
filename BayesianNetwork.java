import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BayesianNetwork {
    // DATA
    private HashMap<String, Variable> varMap;


    // CONSTRUCTOR
    public BayesianNetwork(String path) {
        this.varMap = new HashMap<>();
        constructFromXML(path);
    }

    private void constructFromXML(String path) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            try {
                Document doc = builder.parse(new File(path));
                doc.getDocumentElement().normalize();
                int varCount = 0;
                NodeList vars = doc.getElementsByTagName("VARIABLE");
                for (int i = 0; i < vars.getLength(); i++) {
                    Variable tempVar = null;
                    Node varNode = vars.item(i);
                    if (varNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element varElement = (Element) varNode;
                        Node ndName = varElement.getElementsByTagName("NAME").item(0);
                        if (ndName.getNodeType() == Node.ELEMENT_NODE) {
                            Element nameElement = (Element) ndName;
                            String varName = nameElement.getTextContent();
                            tempVar = new Variable(varCount, varName);
                            varCount++;
                            this.varMap.put(varName, tempVar);
                        }
                        NodeList outcomes = varElement.getElementsByTagName("OUTCOME");
                        for (int j = 0; j < outcomes.getLength(); j++) {
                            Node outcome = outcomes.item(j);
                            if (outcome.getNodeType() == Node.ELEMENT_NODE) {
                                Element outcomeElement = (Element) outcome;
                                String outcomeName = outcomeElement.getTextContent();
                                if (tempVar != null) {
                                    tempVar.addOutcome(outcomeName);
                                }
                            }
                        }
                    }
                }
                NodeList cpts = doc.getElementsByTagName("DEFINITION");
                for (int i = 0; i < cpts.getLength(); i++) {
                    LinkedList<Variable> tempVarList = new LinkedList<>();
                    double[] tempProb = null;
                    Node cptNode = cpts.item(i);
                    if (cptNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element cptElement = (Element) cptNode;
                        NodeList given = cptElement.getElementsByTagName("GIVEN");
                        for (int j = 0; j < given.getLength(); j++) {
                            Node givenNode = given.item(j);
                            if (givenNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element givenElement = (Element) givenNode;
                                String givenName = givenElement.getTextContent();
                                tempVarList.add(this.varMap.get(givenName));
                            }
                        }
                        Node target = cptElement.getElementsByTagName("FOR").item(0);
                        if (target.getNodeType() == Node.ELEMENT_NODE) {
                            Element targetElement = (Element) target;
                            String targetName = targetElement.getTextContent();
                            tempVarList.add(this.varMap.get(targetName));
                        }
                        Node table = cptElement.getElementsByTagName("TABLE").item(0);
                        if (table.getNodeType() == Node.ELEMENT_NODE) {
                            Element tableElement = (Element) table;
                            String tableText = tableElement.getTextContent();
                            String[] tableSplit = tableText.split(" ");
                            tempProb = new double[tableSplit.length];
                            for (int j = 0; j < tableSplit.length; j++) {
                                tempProb[j] = Double.parseDouble(tableSplit[j]);
                            }
                        }
                        if (tempVarList.size() > 1) {
                            Variable child = tempVarList.getLast();
                            Variable parent;
                            Iterator<Variable> it = tempVarList.iterator();
                            while (it.hasNext()) {
                                parent = it.next();
                                if (!it.hasNext()) break;
                                parent.addChild(child);
                                child.addParent(parent);
                            }

                        }
                        tempVarList.getLast().setCpt(new Factor(tempVarList, tempProb));
                        tempVarList.clear();
                    }
                }
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    // METHODS

    /**
     * Implementation of Bayes-Ball algorithm to detect dependency relations
     * between variables in the bayesian network. Will be used for Queries.
     * I made the version for a single source variable
     *
     * @param source   The ball starts at the source variable
     * @param evidence List of the observed variables
     * @return Boolean array that states "true" if the ball passed through that variable and "false" otherwise.
     * Index in the array stand for the correspondent variable ID
     */
    private boolean[] bayesBall(Variable source, List<Variable> evidence) {
        Set<Variable> result = new HashSet<>();
        int size = this.varMap.size();
        boolean[] visited = new boolean[size], markedTop = new boolean[size], markedBottom = new boolean[size], observed = new boolean[size];
        Arrays.fill(visited, false);
        Arrays.fill(markedTop, false);
        Arrays.fill(markedBottom, false);
        Arrays.fill(observed, false);

        if (evidence != null) {
            for (Variable var : evidence) {
                observed[var.getID()] = true;
            }
        }

        final boolean fromChild = true, fromParent = false;

        Queue<Boolean> fromWhom = new LinkedList<>();
        fromWhom.add(fromChild);

        Queue<Variable> scheduled = new LinkedList<>();
        scheduled.add(source);

        while (!scheduled.isEmpty()) {
            Variable var = scheduled.poll();
            int i = var.getID();
            boolean from = fromWhom.poll();

            visited[i] = true;

            if (!observed[i] && from == fromChild) {
                if (!markedTop[i]) {
                    List<Variable> parents = var.getParents();
                    if (parents != null) {
                        for (Variable p : parents) {
                            scheduled.add(p);
                            fromWhom.add(fromChild);
                        }
                    }
                    markedTop[i] = true;
                }
                if (!markedBottom[i]) {
                    List<Variable> children = var.getChildren();
                    if (children != null) {
                        for (Variable child : children) {
                            scheduled.add(child);
                            fromWhom.add(fromParent);
                        }
                    }
                    markedBottom[i] = true;
                }
            }
            if (from == fromParent) {
                if (observed[i] && !markedTop[i]) {
                    List<Variable> parents = var.getParents();
                    if (parents != null) {
                        for (Variable p : parents) {
                            scheduled.add(p);
                            fromWhom.add(fromChild);
                        }
                    }
                    markedTop[i] = true;
                }
                if (!observed[i] && !markedBottom[i]) {
                    List<Variable> children = var.getChildren();
                    if (children != null) {
                        for (Variable child : children) {
                            scheduled.add(child);
                            fromWhom.add(fromParent);
                        }
                    }
                    markedBottom[i] = true;
                }
            }

        }

        return visited;
    }

    private boolean[] reversedBFS(List<Variable> sources) {
        boolean[] visited = new boolean[this.varMap.size()];
        Arrays.fill(visited, false);
        for (Variable source : sources) {
            visited[source.getID()] = true;
        }
        Queue<Variable> q = new LinkedList<>(sources);
        while (!q.isEmpty()) {
            List<Variable> parents = q.poll().getParents();
            if (parents != null) {
                for (Variable p : parents) {
                    if (!visited[p.getID()]) {
                        q.add(p);
                        visited[p.getID()] = true;
                    }
                }
            }
        }
        return visited;
    }

    public ProResult answer(ProQuery query) {
        // TODO Test the cpt check works fine
        Variable varTarget = query.getTarget().keySet().toArray(new Variable[0])[0];
        Factor cpt = varTarget.getCpt();
        List<Variable> parents = varTarget.getParents();
        HashMap<Variable, String> ev = query.getEvidence();
        if (parents.size() == ev.size() && parents.containsAll(ev.keySet())) {
            List<String> state = new LinkedList<>();
            for (Variable v : cpt.getVariables()) {
                state.add(ev.get(v));
            }
            state.add(query.getTarget().values().toArray(new String[0])[0]);
            double pr = cpt.getProb(state);
            return new ProResult(pr, 0, 0);
        } else {
            return answerByVE(query.getTarget(), query.getEvidence(), query.getHidden());
        }
    }

    /* Returns true if they are independent*/
    public boolean answer(IndQuery query) {
        Variable source = query.getVars()[0];
        boolean[] visited = bayesBall(query.getVars()[0], query.getObserved());
        return !visited[source.getID()];
    }

    private ProResult answerByVE(HashMap<Variable, String> target, HashMap<Variable, String> evidence, Queue<Variable> hidden) {

        Variable vtemp = target.keySet().toArray(new Variable[0])[0];
        List<Variable> evidencetemp = new LinkedList<>(evidence.keySet());

        List<Variable> filtered = getRelevantNodes(vtemp, evidencetemp);

        System.out.println(filtered);


        int mulCount = 0, addCount = 0;
        // 1. Construct a factor for each relevant node in the network
        List<Factor> factors = new LinkedList<>();
        for (Variable v : filtered) {
            factors.add(v.getCpt());
        }

        /*
           2. For each evidence variable and for each factor that contains the evidence variable, restrict the
         factor by assigning the observed value to the evidence variable.
         */
        if (evidence != null) {
            for (Map.Entry<Variable, String> entry : evidence.entrySet()) {
                Variable v = entry.getKey();
                String state = entry.getValue();
                for (ListIterator<Factor> it = factors.listIterator(); it.hasNext(); ) {
                    Factor f = it.next();
                    if (f.contains(v)) {
                        Factor restricted = f.restrict(v, state);
                        if (restricted != null) {
                            it.set(restricted);
                        } else {
                            it.remove();
                        }
                    }
                }
            }
        }


        /*
           3. Eliminate each hidden variable X:
            - Multiply all factors that contain X
            - Sum out the variable X from the product factor
         */
        PriorityQueue<Factor> pq = new PriorityQueue<>();
        if (hidden != null) {
            while (!hidden.isEmpty()) {
                Variable toEliminate = hidden.poll();
                for (ListIterator<Factor> it = factors.listIterator(); it.hasNext(); ) {
                    Factor f = it.next();
                    if (f.contains(toEliminate)) {
                        pq.add(f);
                        it.remove();
                    }
                }
                if (!pq.isEmpty()) {
                    mulCount += Factor.multiply(pq);
                    // Priority queue should contain only 1 element
                    Factor f = pq.poll();
                    Factor afterSummation = f.sumOut(toEliminate);
                    if (afterSummation != null) {
                        addCount += afterSummation.size() * (toEliminate.size() - 1);
                        factors.add(afterSummation);
                    } else {
                        addCount += toEliminate.size() - 1;
                    }
                }
            }
        }

        // 4. Multiply the remaining factors
        pq.addAll(factors);
        mulCount += Factor.multiply(pq);
        // 5. Normalize the factor
        Factor last = pq.poll().normalize();
        addCount += vtemp.size() - 1;
        double ans = last.getProb(Arrays.asList(target.values().iterator().next()));

        return new ProResult(ans, mulCount, addCount);
    }

    private List<Variable> getRelevantNodes(Variable query, List<Variable> evidence) {
        boolean[] bb = bayesBall(query, evidence);
        List<Variable> src = new LinkedList<>(evidence);
        src.add(query);
        boolean[] bfs = reversedBFS(src);
        System.out.println(Arrays.toString(bfs));
        List<Variable> ans = new LinkedList<>();
        for (Variable v : this.varMap.values()) {
            if (bb[v.getID()] && bfs[v.getID()]) ans.add(v);
        }
        return ans;
    }


    public HashMap<String, Variable> getVarMap() {
        return this.varMap;
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

    public double getProbability() {
        return probability;
    }

    public int getMul() {
        return mul;
    }

    public int getAdd() {
        return add;
    }

    @Override
    public String toString() {
        return String.format("%.5f,%d,%d", this.probability, this.add, this.mul);
    }
}
