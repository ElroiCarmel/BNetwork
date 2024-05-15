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
    private HashSet<Variable> bayesBall(Variable source, List<Variable> evidence) {
        Set<Variable> result = new HashSet<>();
        int size = this.varMap.size();
        boolean[] visited = new boolean[size], markedTop = new boolean[size], markedBottom = new boolean[size], observed = new boolean[size];
        Arrays.fill(visited, false); Arrays.fill(markedTop, false); Arrays.fill(markedBottom, false); Arrays.fill(observed, false);

        for (Variable var : evidence) {
            observed[var.getID()] = true;
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
                            scheduled.add(p); fromWhom.add(fromChild);
                        }
                    }
                    markedTop[i] = true;
                }
                if (!markedBottom[i]) {
                    List<Variable> children = var.getChildren();
                    if (children != null) {
                        for (Variable child : children) {
                            scheduled.add(child); fromWhom.add(fromParent);
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
                            scheduled.add(p); fromWhom.add(fromChild);
                        }
                    }
                    markedTop[i] = true;
                }
                if (!observed[i] && !markedBottom[i]) {
                    List<Variable> children = var.getChildren();
                    if (children != null) {
                        for (Variable child : children) {
                            scheduled.add(child); fromWhom.add(fromParent);
                        }
                    }
                    markedBottom[i] = true;
                }
            }

        }

        // Still need to decide what to return
    }

    public HashMap<String, Variable> getVarMap() {
        return this.varMap;
    }

}
