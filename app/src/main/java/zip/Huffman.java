package zip;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import root.iv.protection.App;

public class Huffman {
    private static final int SIZE = 256;    // Количество значений

    public int[] zip(int[] content) {
        // Количество повторений каждого символа
        Map<Integer, Integer> map = new HashMap<>();

        for (int block : content) {
            if (map.containsKey(block)) {
                map.put(block, map.get(block) + 1);
            } else {
                map.put(block, 1);
            }

            PriorityQueue<Node> queue = new PriorityQueue<>();
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                queue.add(new LeafNode(entry.getKey(), entry.getValue()));
            }

            int sum = 0;
            while (queue.size() > 1) {
                Node first = queue.poll();
                Node second = queue.poll();
                Node newNode = new InternalNode(first, second);
                sum += newNode.sum;
                queue.add(newNode);
            }

            App.logI("Sum: " + sum);
        }


        return new int[0];
    }

    public int[] unzip(int []zip) {
        return new int[0];
    }


    class Node implements Comparable<Node> {
        final int sum;

        public Node(int sum) {
            this.sum = sum;
        }

        // Минимальный тот, у которого минимальная сумма
        @Override
        public int compareTo(Node o) {
            return Integer.compare(sum, o.sum);
        }
    }

    class InternalNode extends Node {
        Node left;
        Node right;

        public InternalNode(Node left, Node right) {
            super(left.sum + right.sum);
            this.left = left;
            this.right = right;
        }
    }

    class LeafNode extends Node {
        int value;

        public LeafNode(int value, int count) {
            super(count);
            this.value = value;
        }
    }
}
