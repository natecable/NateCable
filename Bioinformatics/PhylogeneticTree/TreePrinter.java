public class TreePrinter {
    public void print(Node node) {
        print(node, "", true);
    }

    private void print(Node node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "+------ " : "|------ ") + (node.label != null ? node.label : "*"));
        if (node.leftChild != null || node.rightChild != null) {
            if (node.leftChild != null) {
                print(node.leftChild, prefix + (isTail ? "        " : "|       "), false);
            }
            if (node.rightChild != null) {
                print(node.rightChild, prefix + (isTail ? "        " : "|       "), true);
            }
        }
    }
}
