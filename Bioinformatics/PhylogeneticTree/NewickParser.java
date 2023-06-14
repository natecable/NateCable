public class NewickParser {
    private String newick;
    private int position;

    public Node parse(String newick) {
        this.newick = newick;
        this.position = 0;
        Node root = parseSubtree();
        assert(position == newick.length());
        return root;
    }
    public int cnt = 1;
    private Node parseSubtree() {
        Node node = new Node();
        if (newick.charAt(position) == '(') {
            // It's an internal node
            position++; // Skip '('
            node.leftChild = parseSubtree();
            assert(newick.charAt(position) == ',');
            position++; // Skip ','
            node.rightChild = parseSubtree();
            assert(newick.charAt(position) == ')');
            node.label = Integer.toString(cnt++);
            position++; // Skip ')'
        } else {
            // It's a leaf node
            int start = position;
            while (position < newick.length() && newick.charAt(position) != ':' && newick.charAt(position) != ','
                && newick.charAt(position) != ')') {
                position++;
            }
            node.label = newick.substring(start, position);
        }
        if (position < newick.length() && newick.charAt(position) == ':') {
            // There's a branch length
            position++; // Skip ':'
            int start = position;
            while (position < newick.length() && newick.charAt(position) != ',' && newick.charAt(position) != ')') {
                position++;
            }
            node.length = Double.parseDouble(newick.substring(start, position));
        }

        computeHeight(node, 0);
        return node;
    }

    private void computeHeight(Node node, double parentHeight) {
        if (node == null) {
            return;
        }

        // Set the height of the current node
        node.height = parentHeight + node.length;

        // Recursively compute the height for the children
        computeHeight(node.leftChild, node.height);
        computeHeight(node.rightChild, node.height);
    }
    

}
