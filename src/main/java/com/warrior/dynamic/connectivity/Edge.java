package com.warrior.dynamic.connectivity;

/**
 * Created by warrior on 06.01.16.
 */
class Edge {
    public final int first;
    public final int second;

    private Edge reverseEdge;

    private Edge(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public Edge reverseEdge() {
        return reverseEdge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        return first == edge.first && second == edge.second;
    }

    @Override
    public int hashCode() {
        int result = first;
        result = 31 * result + second;
        return result;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public static Edge create(int u, int v) {
        Edge edge = new Edge(u, v);
        Edge reverse = new Edge(v, u);
        edge.reverseEdge = reverse;
        reverse.reverseEdge = edge;
        return edge;
    }
}
