package root.iv.protection.zip.node;

public class InternalNode extends Node {
    private Node left;
    private Node right;

    public InternalNode(Node left, Node right) {
        super(left.sum + right.sum);
        this.left = left;
        this.right = right;
    }

    @Override
    public void buildCode(String code) {
        super.buildCode(code);
        left.buildCode(code + "0");
        right.buildCode(code + "1");
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }
}
