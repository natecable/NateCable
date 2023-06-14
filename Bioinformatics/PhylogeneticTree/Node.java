public class Node {
    String label;
    double length;
    Node leftChild;
    Node rightChild;
    double height;

    public Node() { }

    public Node(String label, double length, Node leftChild, Node rightChild) {
        this.label = label;
        this.length = length;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.height = 0.0;
    }

    public Node(String label, double length, double height, Node leftChild, Node rightChild) {
        this.label = label;
        this.height = height;
        this.length = length;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public Node(String label, double length) {
        this.label = label;
        this.length = length;
    }

    public Node(String label, double length, double height) {
        this.label = label;
        this.length = length;
        this.height = height;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }
}
