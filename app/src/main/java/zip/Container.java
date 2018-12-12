package zip;

import java.util.List;

import zip.node.Node;

public class Container {
    Node root;
    int[] zip;

    public Container(List<Integer> list, Node root) {

        this.root = root;
        this.zip = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            zip[i] = list.get(i);
    }

    public Node getRoot() {
        return root;
    }

    public int[] getZip() {
        return zip;
    }
}
