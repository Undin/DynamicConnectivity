package com.warrior.dynamic.connectivity;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by warrior on 01.11.15.
 */
public class TreapTest {

    private static final Random RANDOM = new Random();
    private static final int ITERATION = 10000;

    @Test
    public void insertTest() {
        Node treap = null;
        List<Integer> list = new ArrayList<>(ITERATION);
        for (int i = 0; i < ITERATION * 10; i++) {
            int index = RANDOM.nextInt(list.size() + 1);
            int value = RANDOM.nextInt();
            IntTreap node = new IntTreap(value);
            if (treap != null) {
                treap = Node.insert(treap, index, node);
            } else {
                treap = node;
            }
            list.add(index, value);
        }
        equals(list, treap);
    }

    @Test
    public void splitInvariantTest() {
        for (int j = 0; j < ITERATION; j++) {
            Node treap = generateRandomTreap();
            Node.SplitResult result = Node.split(treap, RANDOM.nextInt(treap.size() - 1) + 1);
            checkInvariants(result.left);
            checkInvariants(result.right);
        }
    }

    @Test
    public void insertInvariantTest() {
        for (int j = 0; j < ITERATION; j++) {
            Node treap = generateRandomTreap();
            checkInvariants(treap);
        }
    }

    @Test
    public void indexTest() {
        List<Node> list = new ArrayList<>(ITERATION);
        Node treap = null;
        for (int i = 0; i < ITERATION; i++) {
            Node node = new Node();
            int index = RANDOM.nextInt(list.size() + 1);
            list.add(index, node);
            if (treap == null) {
                treap = node;
            } else {
                treap = Node.insert(treap, index, node);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            Node expected = list.get(i);
            Node real = treap.get(i);
            Assert.assertEquals("get", expected, real);
            int index = real.index();
            Assert.assertEquals("index", i, index);
        }
    }

    private static Node generateRandomTreap() {
        Node treap = new Node();
        for (int i = 1; i < 1000; i++) {
            treap = Node.insert(treap, RANDOM.nextInt(treap.size() + 1), new Node());
        }
        return treap;
    }

    private void checkInvariants(Node treap) {
        Assert.assertNull("root parent", treap.getParent());
        checkInvariantsRecursive(treap);
    }

    private void checkInvariantsRecursive(Node treap) {
        Node left = treap.getLeft();
        Node right = treap.getRight();
        int leftSize = left != null ? left.size() : 0;
        int rightSize = right != null ? right.size() : 0;
        Assert.assertEquals("size", leftSize + rightSize + 1, treap.size());

        if (left != null) {
            Assert.assertEquals("left parent", left.getParent(), treap);
            checkInvariantsRecursive(left);
        }
        if (right != null) {
            checkInvariantsRecursive(right);
            Assert.assertEquals("right parent", right.getParent(), treap);
        }
    }

    private static void equals(List<Integer> list, Node treap) {
        Assert.assertEquals(list.size(), treap.size());
        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals((int)list.get(i), ((IntTreap) treap.get(i)).value);
        }
    }

    private static class IntTreap extends Node {
        public final int value;

        public IntTreap(int value) {
            super();
            this.value = value;
        }
    }
}
