package root.iv.protection;

import org.junit.Assert;
import org.junit.Test;

import zip.Container;
import zip.Huffman;

public class ZipUnitTest {
    @Test
    public void test() {
        Huffman huffman = new Huffman();
        String content = "aabbbbbbbxdc";
        int[] baseContent = new int[content.length()];
        for (int i = 0; i < content.length(); i++) {
            baseContent[i] = content.charAt(i);
        }

        Container container = huffman.zip(baseContent);
        int[] zipContent = container.getZip();

        int[] unzipContent = huffman.unzip(zipContent, container.getRoot());
    }

}
