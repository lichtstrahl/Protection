package zip.node;

import root.iv.protection.App;

public class LeafNode extends Node {
    private int value;

    public LeafNode(int value, int count) {
        super(count);
        this.value = value;
    }

    @Override
    public void buildCode(String code) {
        super.buildCode(code);
        App.logI("buildCode: " + value + " : " + code);
    }

    public int getValue() {
        return value;
    }
}
