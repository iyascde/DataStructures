//package AVLTrees;

import java.util.Queue;

public class IntTree {
    private IntTreeNode overallRoot;
    
    public IntTree() {
        overallRoot = null;
    }
    
    /**
     * Adds a new data value to the binary search tree
     * @param data - value to be added to the tree.
     */
    public void addValue(int data) {
        if (overallRoot == null) {
            overallRoot = new IntTreeNode(data);
        } else {
            overallRoot.addValue(data);
        }
    }

    /**
     * Returns a multi-line "pretty-print" representation of the tree.
     */
    public String toPrettyPrint() {
        String output = "";
        if (overallRoot != null) {
            Queue<String> lines = overallRoot.toPrettyPrint();
            while(!lines.isEmpty()) {
                output += lines.remove() + "\n";
            }
        }
        return output;
    }

    public void addValueAVL(int data) {
        if(overallRoot == null) {
            overallRoot = new IntTreeNode(data);
        } else {
            overallRoot = overallRoot.addValueAVL(data);
        }
    }
}


