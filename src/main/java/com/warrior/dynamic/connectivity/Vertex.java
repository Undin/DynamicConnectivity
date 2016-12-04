package com.warrior.dynamic.connectivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by warrior on 04.01.16.
 */
class Vertex extends Node {

    public final int number;

    private Set<Edge> adjacentEdges = new HashSet<>();

    public Vertex(int number) {
        super();
        this.number = number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public Set<Edge> getAdjacentEdges() {
        return adjacentEdges;
    }

    public void addEdge(Edge edge) {
        boolean change = adjacentEdges.add(edge);
        if (change) {
            updateTree();
        }
    }

    public void removeEdge(Edge edge) {
        boolean change = adjacentEdges.remove(edge);
        if (change) {
            updateTree();
        }
    }

    private void updateTree() {
        Node node = this;
        while (node != null) {
            boolean hasEdgesInLeftTree = node.getLeft() != null && node.getLeft().hasEdgesInTree;
            boolean hasEdgesInRightTree = node.getRight() != null && node.getRight().hasEdgesInTree;
            boolean hasEdges = node.hasEdgesInNode() || hasEdgesInLeftTree || hasEdgesInRightTree;
            if (node.hasEdgesInTree != hasEdges) {
                node.hasEdgesInTree = hasEdges;
                node = node.getParent();
            } else {
                break;
            }
        }
    }

    @Override
    public boolean hasEdgesInNode() {
        return !adjacentEdges.isEmpty();
    }
}
