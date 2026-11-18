package algs;

import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.Queue;
import java.util.HashSet;
import java.util.HashMap;


//[a,b[
class Interval {
    int a, b;
    Interval(int m, int M) {
        a = m;
        b = M;
    }

    boolean Contains(int v) {
        return (v >= a && v <b);
    }

    @Override
    public String toString() {
        return "[" + a + "," + b + "[";
    }
}

class Node {
    LinkedList<Node> childs;
    Boolean val;
    String attribute;
    String attributeVi;
    int id;

    Node() {
        attribute = "";
        attributeVi = "";
        val = null;
        childs = new LinkedList<Node>();
    }
    Node(String attr, String vi) {
        attribute = attr;
        attributeVi = vi;
        val = null;
        childs = new LinkedList<Node>();
    }

    void AddChild(Node child) {
        childs.add(child);
    }

    int ChildNumber() {
        return childs.size();
    }

    void Print() {
        System.out.print("(");
        System.out.print(" Number of children: " + ChildNumber());
        if(attribute != "") System.out.print("| Atribute-vi: " + attribute + "-" + attributeVi+ " ");
        if(val != null) System.out.print("| Val: " + (val?"Yes":"No") + " ");
        System.out.print(" )");
    }
}

public class Algs {


    static HashMap<String,Integer> indexAttributes;
    static Node tree;

    public static double log2(double N) {
        double result = (Math.log(N) / Math.log(2));
        return result;
    }


    private static int Entropy(String attribute) {
        //todo
        return 0;
    }

    private static double Entropy(int x, int y) {
        if(x == 0 || y == 0) return 0;
        double total = (double)(x+y);
        return - (x/total) * log2(x/total) - (y/total) * log2(y/total);
    }

    private static double EntropyLast(LinkedList<LinkedList<String>> examples) {
        //We assume decision is in last collumn
        int nYes=0, nNo=0;
        int lastCol = examples.get(1).size() - 1;
        for(int i=1; i<examples.size(); i++) {
            if(examples.get(i).get(lastCol).toLowerCase().equals("yes")) {
                nYes++;
            }
            else {
                nNo++;
            }
        }
        double nTotal = nYes+nNo;
        if(nYes == 0 || nNo == 0) return 0;
        return - (nYes/nTotal) * log2(nYes/nTotal) - (nNo/nTotal) * log2(nNo/nTotal);
    }

    private static Map<String,Double> CalculateEntropy(LinkedList<LinkedList<String>> examples, LinkedList<String> attributes) {
        Map<String,Double> m = new HashMap<String,Double>();
        double decisionEntropy = EntropyLast(examples);
        int lastCol = examples.get(1).size() - 1;
        for(int a=0; a<attributes.size();a++) {
            String at = attributes.get(a);
            int col = a+1;
            Set<String> vs = GetAttributeSet(examples, col);
            double totalEntr = 0;
            for(String v : vs) {
                int nYes = 0;
                int nNo = 0;
                for(int i=1; i<examples.size(); i++) {
                    String entry = examples.get(i).get(col);
                    if(entry.equals(v)) {
                        if(examples.get(i).get(lastCol).toLowerCase().equals("yes")) {
                            nYes++;
                        }
                        else {
                            nNo++;
                        }
                    }
                }
                totalEntr += ((nYes+nNo)/(double)(examples.size()-1)) * Entropy(nYes,nNo);
            }

            m.put(at, decisionEntropy - totalEntr);
        }
        return m;
    }

    private static void SortByEntropy(LinkedList<LinkedList<String>> examples, LinkedList<String> attributes) {
        Map<String,Double> entr = CalculateEntropy(examples,attributes);
        for(Map.Entry<String,Double> e : entr.entrySet()) {
            // System.out.println(e.getKey() + ", " + e.getValue().toString());
        }
        Collections.sort(attributes, new Comparator<String>() {
            public int compare(String s1, String s2) {
                if(entr.get(s1) > entr.get(s2)) {
                    return -1;
                } else if (entr.get(s1) < entr.get(s2)) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public static void PositivityCount(LinkedList<LinkedList<String>> examples, Integer nYes, Integer nNo) {
        int lastCol = examples.get(1).size() - 1;
        for(int i=1; i<examples.size(); i++) {
            if(examples.get(i).get(lastCol).toLowerCase().equals("yes")) {
                nYes++;
            }
            else {
                nNo++;
            }
        }

    }

    public static Set<String> GetAttributeSet(LinkedList<LinkedList<String>> examples, int col) {
        Set<String> vs = new HashSet<>();
        for(int i=1; i<examples.size(); i++) {
            String entry = examples.get(i).get(col);
            vs.add(entry);
        }
        return vs;
    }


    public static void PopulateMap(LinkedList<LinkedList<String>> examples) {
        for(int i = 0; i < examples.get(0).size(); i++) {
            indexAttributes.put(examples.get(0).get(i), i + 1);
            // System.out.println("Pair added: ("+examples.get(0).get(i) +"," + (i + 1));
        }
    }

    public static LinkedList<String> GetIntAttributes(LinkedList<LinkedList<String>> examples) {
        LinkedList<String> intAttributes = new LinkedList<String>();
        LinkedList<String> attributes = examples.get(0);
        for(String att : attributes) {
            int col = indexAttributes.get(att);
            boolean isInt = true;
            for(int r=1; r<examples.size(); r++) {
                String entry = examples.get(r).get(col);
                for(int i=0; i<entry.length(); i++) {
                    char l = entry.charAt(i);
                    if(!Character.isDigit(l)) {
                        isInt = false;
                        break;
                    }
                }
                if(!isInt) break;
            }
            if(isInt) intAttributes.add(att);
        }
        return intAttributes;
    }

    public static LinkedList<Integer> GetMinAndMax(LinkedList<LinkedList<String>> examples, int col) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for(int r=1; r<examples.size(); r++) {
            String entry = examples.get(r).get(col);
            int num = Integer.decode(entry);
            min = Math.min(min, num);
            max = Math.max(max, num);
        }

        return new LinkedList<Integer>(Arrays.asList(min,max));
    }

    public static LinkedList<Interval> GetIntervals(LinkedList<LinkedList<String>> examples, int col) {
        LinkedList<Integer> min_max = GetMinAndMax(examples, col);

        int min = min_max.get(0), max = min_max.get(1);
        int len = max-min+1;
        int q1 = min + len/4;
        int q2 = min + len/2;
        int q3 = q2 + len/4;

        Interval i1 = new Interval(min, q1);
        Interval i2 = new Interval(q1, q2);
        Interval i3 = new Interval(q2, q3);
        Interval i4 = new Interval(q3, max + 1);
        return new LinkedList<Interval>(Arrays.asList(i1,i2,i3,i4));
    }

    public static void Normalize(LinkedList<LinkedList<String>> examples) {
        LinkedList<String> intAttributes = GetIntAttributes(examples);
        for(String att : intAttributes) {
            int col = indexAttributes.get(att);

            LinkedList<Interval> intervalList = GetIntervals(examples, col);

            for(int r=1; r<examples.size(); r++) {
                String entry = examples.get(r).get(col);
                int num = Integer.decode(entry);
                for(Interval i : intervalList) {
                    if(i.Contains(num)) {
                        examples.get(r).set(col, i.toString());
                    }
                }
            }

        }

    }


    public static void ID3(LinkedList<LinkedList<String>> examples, int target_attribute_index, LinkedList<String> attributes) {
        tree = new Node();
        indexAttributes = new HashMap<String,Integer>();
        PopulateMap(examples);
        Normalize(examples);
        ID3_aux(examples,target_attribute_index, attributes, tree);
    }


    public static LinkedList<LinkedList<String>> SubSet(LinkedList<LinkedList<String>> examples, String vi, int col) {
        LinkedList<LinkedList<String>> subsetvi = new LinkedList<LinkedList<String>>();
        LinkedList<String> firstRow = new LinkedList<String>();
        for(int i=0; i<examples.get(0).size(); i++) {
            firstRow.add(examples.get(0).get(i));
        }
        subsetvi.add(firstRow);
        for(int i=1; i<examples.size(); i++) {
            if(!examples.get(i).get(col).equals(vi)) {
                continue;
            }
            LinkedList<String> row = new LinkedList<String>();
            for(int j=0; j<examples.get(i).size(); j++) {
                row.add(examples.get(i).get(j));
            }
            subsetvi.add(row);
        }

        return subsetvi;
    }

    public static void ID3_aux(LinkedList<LinkedList<String>> examples, int target_attribute_index, LinkedList<String> attributes, Node curr) {
        SortByEntropy(examples, attributes);

        Integer nYes=0, nNo=0;
        int lastCol = examples.get(1).size() - 1;
        for(int i=1; i<examples.size(); i++) {
            if(examples.get(i).get(lastCol).toLowerCase().equals("yes")) {
                nYes++;
            }
            else {
                nNo++;
            }
        }


        if(attributes.size() == 0) {
            if(nYes > nNo) {
                curr.val = true;
            }
            else {
                curr.val = false;
            }
            return;
        }

        if(nNo == 0) {
            curr.val = true;
            return;
        }

        if(nYes == 0) {
            curr.val = false;
            return;
        }

        String bestAttribute = attributes.poll();


        int col = indexAttributes.get(bestAttribute);
        //linha pa dps nao esqeuecer
        Set<String> possibleVals = GetAttributeSet(examples, col);

        for(String vi : possibleVals) {
            Node n = new Node(bestAttribute, vi);

            LinkedList<LinkedList<String>> examplesVi = SubSet(examples, vi, col);



            if(examplesVi.size() <= 1) {
                //empty subset
                if(nYes > nNo) {
                    n.val = true;
                }
                else {
                    n.val = false;
                }
                curr.AddChild(n);
            }
            else {
                curr.AddChild(n);
                ID3_aux(examplesVi, target_attribute_index, attributes, n);
            }

        }
        return;
    }

    public static boolean isInInterval(String inter, String value) {
        int min,max;
        String[] values = inter.split(",");
        min = Integer.parseInt(values[0].substring(1));
        int len = values[1].length();
        max = Integer.parseInt(values[1].substring(0, len-1));
        int v = Integer.parseInt(value);

        return (min <= v && v < max);
    }


    public static String GoWildOnTree(LinkedList<String> entry, LinkedList<String> intAttributes) {
        LinkedList<Node> nexts = new LinkedList<Node>();
        for(Node child : tree.childs) nexts.add(child);
        Node result = tree;
        while(!nexts.isEmpty()) {
            String attribute = nexts.get(0).attribute;
            int col = indexAttributes.get(attribute);
            String vi = entry.get(col);
            boolean found = false;

            for(Node n : nexts) {
                if(intAttributes.contains(attribute)) {
                    if(isInInterval(n.attributeVi, vi)) {
                        nexts = new LinkedList<Node>();
                        for(Node child : n.childs) nexts.add(child);
                        result = n;
                        found = true;
                        break;
                    }
                }
                else {
                    if(n.attributeVi.equals(vi)) {
                        nexts = new LinkedList<Node>();
                        for(Node child : n.childs) nexts.add(child);
                        result = n;
                        found = true;
                        break;
                    }
                }
            }

            if(!found) {
                System.out.println("Wrong attribute match.");
                System.exit(1);
            }
        }
        return (result.val)?"Yes":"No";
    }

    public static LinkedList<String> Decide(LinkedList<LinkedList<String>> testdata) {
        LinkedList<String> results = new LinkedList<String>();
        for(int r = 1; r< testdata.size(); r++) {
            LinkedList<String> entry = testdata.get(r);
            LinkedList<String> intAttributes = GetIntAttributes(testdata);
            String yesorno = GoWildOnTree(entry, intAttributes);
            results.add(entry.get(0) + ": " + yesorno);
        }

        return results;
    }


    public static void PrintTree() {

        System.out.println("Tree --------------\n");
        Queue<Node> q = new LinkedList<Node>();
        Queue<Node> nextq = new LinkedList<Node>();
        q.add(tree);
        int d = 0;
        while(!q.isEmpty()) {
            System.out.println("Depth " + d);
            d++;
            while(!q.isEmpty()) {
                Node curr = q.poll();

                curr.Print();
                for(Node child : curr.childs) nextq.add(child);
                System.out.println();

            }
            System.out.println();
            q = new LinkedList(nextq);
            nextq = new LinkedList<Node>();
        }
        System.out.println("-------------------\n");

    }

}
