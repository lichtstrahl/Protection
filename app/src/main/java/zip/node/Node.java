package zip.node;

import java.io.Serializable;

public class Node implements Comparable<Node>, Serializable {
    protected final int sum;
    protected String code;

    public void buildCode(String code) {
        this.code = code;
    }

    public Node(int sum) {
        this.sum = sum;
    }

    // Минимальный тот, у которого минимальная сумма
    @Override
    public int compareTo(Node o) {
        return Integer.compare(sum, o.sum);
    }

    public String getCode() {
        return code;
    }
}
