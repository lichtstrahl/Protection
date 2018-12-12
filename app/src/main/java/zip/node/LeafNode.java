package zip.node;

public class LeafNode extends Node {
    private int value;

    public LeafNode(int value, int count) {
        super(count);
        this.value = value;
    }

    @Override
    public void buildCode(String code) {
        super.buildCode(code);
    }

    public int getValue() {
        return value;
    }
}
