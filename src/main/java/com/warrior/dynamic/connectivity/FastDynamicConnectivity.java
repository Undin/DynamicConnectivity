package com.warrior.dynamic.connectivity;

import java.util.*;

import static com.warrior.dynamic.connectivity.Node.merge;

/**
 * Created by warrior on 04.01.16.
 */
public class FastDynamicConnectivity implements DynamicConnectivity {

    private final int maxLevel;
    private final List<Level> levels;

    private final Map<Edge, Integer> edgeLevels = new HashMap<>();

    public FastDynamicConnectivity(int n) {
        maxLevel = (int) Math.ceil(Math.log(n) / Math.log(2));
        levels = new ArrayList<>(maxLevel + 1);
        for (int i = 0; i <= maxLevel; i++) {
            levels.add(new Level(n));
        }
    }

    @Override
    public void link(int u, int v) {
        if (u == v) {
            return;
        }
        Edge edge = Edge.create(u, v);

        // if edge had been added earlier do nothing
        if (edgeLevels.containsKey(edge)) {
            return;
        }

        addEdgeToLevel(edge, maxLevel);
    }

    @Override
    public void cut(int u, int v) {
        if (u == v) {
            return;
        }
        Edge edge = Edge.create(u, v);
        if (!edgeLevels.containsKey(edge)) {
            return;
        }

        int edgeLevel = edgeLevels.remove(edge);
        edgeLevels.remove(edge.reverseEdge());
        boolean edgeInMst = false;
        for (int i = edgeLevel; i <= maxLevel; i++) {
            Level level = levels.get(i);
            edgeInMst = level.cut(edge);
        }

        if (edgeInMst) {
            for (int i = edgeLevel; i <= maxLevel; i++) {
                Level level = levels.get(i);
                Node firstTree = level.getVertex(u).root();
                Node secondTree = level.getVertex(v).root();
                // if |firstTree| > |secondTree| swap
                if (firstTree.size() > secondTree.size()) {
                    firstTree = secondTree;
                }

                List<Edge> treeEdges = treeEdges(firstTree);
                List<Edge> decreaseLevel = new ArrayList<>();
                Edge replacementEdge = null;
                for (Edge e : firstTree) {
                    Vertex w = level.getVertex(e.second);
                    if (w.root() == firstTree) {
                        decreaseLevel.add(e);
                    } else {
                        replacementEdge = e;
                        break;
                    }
                }

                decreaseLevel(treeEdges, i);
                decreaseLevel(decreaseLevel, i);

                if (replacementEdge != null) {
                    for (int j = i; j <= maxLevel; j++) {
                        levels.get(j).link(replacementEdge);
                    }
                    break;
                }
            }
        }
    }

    private void decreaseLevel(List<Edge> edges, int level) {
        for (Edge e : edges) {
            int l = edgeLevels.get(e);
            // if l == level - 1 then we has already decrease level of this edge with its reverse edge
            if (l > level - 1) {
                removeEdgeFromLevel(e, level);
                addEdgeToLevel(e, level - 1);
            }
        }
    }

    private List<Edge> treeEdges(Node node) {
        List<Edge> treeEdges = new ArrayList<>();
        treeEdges(node, treeEdges);
        return treeEdges;
    }

    private void treeEdges(Node node, List<Edge> edges) {
        if (node instanceof EdgeNode) {
            EdgeNode edgeNode = (EdgeNode) node;
            edges.add(Edge.create(edgeNode.u.number, edgeNode.v.number));
        }
        if (node.getLeft() != null) {
            treeEdges(node.getLeft(), edges);
        }
        if (node.getRight() != null) {
            treeEdges(node.getRight(), edges);
        }
    }

    private void removeEdgeFromLevel(Edge e, int l) {
        Level level = levels.get(l);

        Vertex x = level.getVertex(e.first);
        Vertex y = level.getVertex(e.second);

        x.removeEdge(e);
        y.removeEdge(e.reverseEdge());
    }

    private void addEdgeToLevel(Edge edge, int l) {
        // level(edge) = level(edge.reverseEdge()) = l
        edgeLevels.put(edge, l);
        edgeLevels.put(edge.reverseEdge(), l);

        // add edge 'edge' to level l
        Level level = levels.get(l);
        boolean connectSpanningTree = level.link(edge);

        if (!connectSpanningTree) {
            // added adjacent edges to vertices
            Vertex first = level.getVertex(edge.first);
            Vertex second = level.getVertex(edge.second);
            first.addEdge(edge);
            second.addEdge(edge.reverseEdge());
        }
    }

    @Override
    public boolean reach(int u, int v) {
        Level level = levels.get(maxLevel);
        return level.reach(u, v);
    }

    public void print() {
        for (int i = maxLevel; i >= 0; i--) {
            System.out.println("level " + i);
            levels.get(i).printAllTrees();
            System.out.println();
        }
    }

    private static class Level {

        private final List<Vertex> vertices;
        private final Map<Edge, EdgeNode> edges = new HashMap<>();
        private final Set<EdgeNode> spanningTreeEdges = new HashSet<>();

        public Level(int n) {
            vertices = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                vertices.add(new Vertex(i));
            }
        }

        public boolean reach(int u, int v) {
            Vertex vu = vertices.get(u);
            Vertex vv = vertices.get(v);
            return vu.root() == vv.root();
        }

        /**
         * @param edge
         * @return true if edge link to separate spanning tree
         */
        public boolean link(Edge edge) {
            Vertex first = vertices.get(edge.first);
            Vertex second = vertices.get(edge.second);
            EdgeNode edgeNode = EdgeNode.create(first, second);
            edges.put(edge, edgeNode);
            edges.put(edge.reverseEdge(), edgeNode.reverseEdge());

            Node firstRoot = first.root();
            Node secondRoot = second.root();
            if (firstRoot == secondRoot) {
                return false;
            }
            spanningTreeEdges.add(edgeNode);
            spanningTreeEdges.add(edgeNode.reverseEdge());

            firstRoot = Node.normalization(firstRoot, first);
            secondRoot = Node.normalization(secondRoot, second);

            Node root = firstRoot;
            root = merge(root, edgeNode);
            root = merge(root, secondRoot);
            root = merge(root, edgeNode.reverseEdge());
            return true;
        }

        /**
         * @param edge removing edge
         * @return true if mst contains edge and false otherwise
         */
        public boolean cut(Edge edge) {
            EdgeNode edgeNode = edges.get(edge);

            edges.remove(edge);
            edges.remove(edge.reverseEdge());

            boolean edgeInSpanningTree;
            if (edgeInSpanningTree = spanningTreeEdges.contains(edgeNode)) {
                spanningTreeEdges.remove(edgeNode);
                spanningTreeEdges.remove(edgeNode.reverseEdge());
                removeEdge(edgeNode);
            }

            vertices.get(edge.first).removeEdge(edge);
            vertices.get(edge.second).removeEdge(edge.reverseEdge());

            return edgeInSpanningTree;
        }

        // ... left part ... (edge) ... second tree ... (reverse edge) ... right part ...
        private void removeEdge(EdgeNode edge) {
            int firstIndex = edge.index();
            int secondIndex = edge.reverseEdge().index();

            int l = Math.min(firstIndex, secondIndex);
            int r = Math.max(firstIndex, secondIndex);

            Node root = edge.root();

            // split right part
            Node.SplitResult result = Node.split(root, r + 1);
            Node right = result.right;
            root = result.left;

            // remove reverse edge
            root = Node.split(root, r).left;

            // split second tree
            root = Node.split(root, l + 1).left;

            // remove edge
            Node left = Node.split(root, l).left;

            // merge left and right parts of first tree
            Node.merge(left, right);
        }

        public Vertex getVertex(int v) {
            return vertices.get(v);
        }

        public void printAllTrees() {
            Node[] roots = new Node[vertices.size()];
            for (int i = 0; i < vertices.size(); i++) {
                Node root = vertices.get(i).root();
                roots[i] = root;
                boolean isPrinted = false;
                for (int j = 0; j < i; j++) {
                    if (roots[j] == root) {
                        isPrinted = true;
                        break;
                    }
                }
                if (!isPrinted) {
                    System.out.println(Node.toString(root));
                }
            }
        }
    }
}
