package com.warrior.dynamic.connectivity;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by warrior on 31.10.15.
 */
class Node implements Iterable<Edge> {

    private final static Random RANDOM = new Random();

    private final int y;

    private Node parent;
    private Node left;
    private Node right;

    private int size;

    protected boolean hasEdgesInTree;

    public Node() {
        y = RANDOM.nextInt();
        size = 1;
    }

    public int size() {
        return size;
    }

    public Node get(int index) {
        return get(this, index);
    }

    public Node getParent() {
        return parent;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public int index() {
        return index(this, left != null ? left.size : 0);
    }

    public Node root() {
        Node root = this;
        while (root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    protected void update() {
        size = 1;
        hasEdgesInTree = hasEdgesInNode();
        if (left != null) {
            left.parent = this;
            size += left.size;
            hasEdgesInTree |= left.hasEdgesInTree;
        }
        if (right != null) {
            right.parent = this;
            size += right.size;
            hasEdgesInTree |= right.hasEdgesInTree;
        }
    }

    public boolean hasEdgesInNode() {
        return false;
    }

    public boolean hasEdgesInTree() {
        return hasEdgesInTree;
    }

    @Override
    public Iterator<Edge> iterator() {
        Node node = root();
        if (!node.hasEdgesInTree()) {
            return Collections.emptyIterator();
        } else {
            Vertex vertex = min(node);
            return new EdgeIterator(vertex);
        }
    }

    private static Vertex succ(Vertex v) {
        if (v.getRight() != null && v.getRight().hasEdgesInTree()) {
            return min(v.getRight());
        } else {
            Node node = v;
            Node parent = v.getParent();
            while (parent != null &&
                    (parent.getRight() == node || !isVertexAndHasEdges(parent) && !hasEdgesInTree(parent.getRight()))) {
                node = parent;
                parent = node.getParent();
            }

            if (parent == null) {
                return null;
            }
            if (isVertexAndHasEdges(parent)) {
                return (Vertex) parent;
            }
            if (hasEdgesInTree(parent.getRight())) {
                return min(parent.getRight());
            }
            throw new IllegalStateException("Invariant is incorrect");
        }
    }

    private static boolean hasEdgesInTree(Node node) {
        return node != null && node.hasEdgesInTree();
    }

    // node.hasEdgesInTree() == true
    private static Vertex min(Node node) {
        if (node.getLeft() != null && node.getLeft().hasEdgesInTree()) {
            return min(node.getLeft());
        } else if (isVertexAndHasEdges(node)) {
            return (Vertex) node;
        } else if (node.getRight() != null && node.getRight().hasEdgesInTree()) {
            return min(node.getRight());
        } else {
            throw new IllegalStateException("invariant is incorrect");
        }
    }

    private static boolean isVertexAndHasEdges(Node node) {
        return node instanceof Vertex && node.hasEdgesInNode();
    }

    public static Node insert(Node treap, int index, Node node) {
        rangeCheckForInsert(treap, index);
        SplitResult splitResult = split(treap, index);
        Node result = merge(splitResult.left, node);
        return merge(result, splitResult.right);
    }

    private static void rangeCheckForInsert(Node treap, int index) {
        if (index < 0 || index > treap.size()) {
            throw new IndexOutOfBoundsException();
        }
    }

    private static Node get(Node node, int index) {
        int leftSize = node.left != null ? node.left.size : 0;
        if (leftSize == index) {
            return node;
        } else if (leftSize > index) {
            return get(node.left, index);
        } else {
            return get(node.right, index - leftSize - 1);
        }
    }

    public static SplitResult split(Node node, int k) {
        SplitResult result = splitInternal(node, k);
        if (result.left != null) {
            result.left.parent = null;
        }
        if (result.right != null) {
            result.right.parent = null;
        }
        return result;
    }

    public static SplitResult splitInternal(Node node, int k) {
        if (node == null) {
            if (k == 0) {
                return new SplitResult(null, null);
            }
            throw new IllegalArgumentException("node == null && k > 0");
        }
        if (k > node.size) {
            throw new IllegalArgumentException("k > node.size");
        }
        int leftSize = node.left != null ? node.left.size : 0;
        if (leftSize >= k) {
            SplitResult result = split(node.left, k);
            node.left = result.right;
            node.update();
            return new SplitResult(result.left, node);
        } else {
            SplitResult result = split(node.right, k - leftSize - 1);
            node.right = result.left;
            node.update();
            return new SplitResult(node, result.right);
        }
    }

    public static Node merge(Node left, Node right) {
        if (left == null || right == null) {
            return left == null ? right : left;
        }
        if (left.y > right.y) {
            left.right = merge(left.right, right);
            left.update();
            return left;
        } else {
            right.left = merge(left, right.left);
            right.update();
            return right;
        }
    }

    private static int index(Node treap, int sum) {
        if (treap.parent == null) {
            return sum;
        }
        Node parent = treap.parent;
        if (parent.right == treap) {
            if (parent.left != null) {
                sum += parent.left.size;
            }
            sum++;
        }
        return index(parent, sum);
    }

    public static Node normalization(Node root, Node node) {
        int index = node.index();
        SplitResult result = split(root, index);
        return merge(result.right, result.left);
    }

    public static String toString(Node node) {
        return IntStream.range(0, node.size())
                .mapToObj(node::get)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    public static class SplitResult {

        public final Node left;
        public final Node right;

        public SplitResult(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
    }

    private static class EdgeIterator implements Iterator<Edge> {

        private Vertex vertex;
        private Iterator<Edge> it;

        // vertex == min(vertex.root())
        public EdgeIterator(Vertex vertex) {
            this.vertex = vertex;
            it = vertex.getAdjacentEdges().iterator();
        }

        @Override
        public boolean hasNext() {
            if (vertex == null) {
                return false;
            }
            if (it.hasNext()) {
                return true;
            }
            vertex = succ(vertex);
            if (vertex != null) {
                it = vertex.getAdjacentEdges().iterator();
            }
            return vertex != null;
        }

        @Override
        public Edge next() {
            return it.next();
        }
    }
}
