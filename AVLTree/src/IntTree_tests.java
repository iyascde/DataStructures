//package AVLTrees;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntTree_tests {

    /**
     * Creates a new IntTree and adds the given values to it.
     * @param values - the values to add to the tree.
     * @return the newly created IntTree with the given values.
     */
    private IntTree newTree(int... values) {
        IntTree tree = new IntTree();
        for(int data : values) {
            tree.addValue(data);
        }
        return tree;
    }

    private IntTree newTreeAVL(int... values) {
        IntTree tree = new IntTree();
        for(int data : values) {
            tree.addValueAVL(data);
        }
        return tree;
    }


    @Test
    public void test_avlTree() {
        IntTree tree = newTreeAVL(1,2,3,4,5,6);
        String expected = 
        "      ___[4]      \n" +
        "     /      \\     \n" +
        "   [2]      [5]   \n" +
        "  /   \\        \\  \n" +
        "[1]   [3]      [6]\n";
        String output = tree.toPrettyPrint();
        System.out.println(output);
        assertEquals(expected, output);
    }

    @Test
    public void test_bstTree() {
        IntTree tree = newTree(1,2,3,4,5,6);
        String expected = 
        "[1]               \n" +
        "   \\              \n" +
        "   [2]            \n" +
        "      \\           \n" +
        "      [3]         \n" +
        "         \\        \n" +
        "         [4]      \n" +
        "            \\     \n" +
        "            [5]   \n" +
        "               \\  \n" +
        "               [6]\n" +
        "                  \n";
        String output = tree.toPrettyPrint();
        System.out.println(output);
        assertEquals(expected, output);
    }
}
