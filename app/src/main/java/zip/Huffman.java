package zip;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import zip.node.InternalNode;
import zip.node.LeafNode;
import zip.node.Node;

public class Huffman {
    // Не учитывается случай, когда файл состоит из одного символа
    public Container zip(int[] content) {
        // Количество повторений каждого символа. Вариационный ряд
        Map<Integer, Integer> map = new HashMap<>();

        for (int block : content) {
            if (map.containsKey(block)) {
                map.put(block, map.get(block) + 1);
            } else {
                map.put(block, 1);
            }
        }

        // Запоминаем какому блоку соответствует какой узел в дереве, ведь строим мы это дерево начиная с листов, т.е. с элементов
        SparseArray<Node> blockNodes = new SparseArray<>();
        // Очередь с приоритетами
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            // Каждый узел - символ и число его повторений
            Node n = new LeafNode(entry.getKey(), entry.getValue());
            queue.add(n);
            blockNodes.put(entry.getKey(), n);
        }

        // Строим из очереди, "типа дерево".
        // Сортировать не нужно, так как очередь с приоритетами
        while (queue.size() > 1) {
            Node first = queue.poll();
            Node second = queue.poll();

            if (first == null || second == null)
                break;

            // При "схлопывании" просто складываются повторения в общем узле
            Node newNode = new InternalNode(first, second);
            queue.add(newNode);
        }

        Node root = queue.poll();
        root.buildCode("");

        String res = "1";
        List<Integer> result = new LinkedList<>();
        for (int i = 0; i < content.length; i++) {
            res += blockNodes.get(content[i]).getCode();

            if (res.length() >= 8) {
                char []buf = new char[8];
                res.getChars(0, 8, buf, 0);
                result.add(Integer.valueOf(String.valueOf(buf), 2));

                int n = res.length() - 8;
                char []swap = new char[n];
                res.getChars(8, res.length(), swap, 0);
                res = "";
                for (char c : swap)
                    res += c;
            }
        }

        if (!res.isEmpty())
            result.add(Integer.valueOf(res, 2));

        return new Container(result, root);
    }

    public int[] unzip(int []zip, Node root) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < zip.length-1; i++) {
            char []number = new char[] {'0','0','0','0','0','0','0','0'};
            String str = Integer.toBinaryString(zip[i]);
            int n = str.length();
            str.getChars(0, n, number, 8-n);
            builder.append(number);
        }
        builder.append(Integer.toBinaryString(zip[zip.length-1]));

        String input = builder.toString().substring(1, builder.toString().length());

        Node cur = root;

        List<Integer> unzip = new LinkedList<>();
        for (char c : input.toCharArray()) {
            if (cur instanceof LeafNode) {
                unzip.add(((LeafNode) cur).getValue());
                cur = root;
            }

            cur = (c == '0')
                    ? ((InternalNode)cur).getLeft()
                    : ((InternalNode)cur).getRight();

        }
        // Последний байт обязательно нужно посмотреть
        if (cur instanceof LeafNode) unzip.add(((LeafNode) cur).getValue());

        int[] r = new int[unzip.size()];
        for (int i = 0; i < unzip.size(); i++)
            r[i] = unzip.get(i);
        return r;
    }
}
