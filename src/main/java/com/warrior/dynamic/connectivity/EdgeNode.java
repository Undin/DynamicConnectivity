package com.warrior.dynamic.connectivity;

/**
 * Created by warrior on 04.01.16.
 */
class EdgeNode extends Node {

    public final Vertex u;
    public final Vertex v;

    private EdgeNode reverseEdge;

    private EdgeNode(Vertex u, Vertex v) {
        super();
        this.u = u;
        this.v = v;
    }

    public EdgeNode reverseEdge() {
        return reverseEdge;
    }

    @Override
    public String toString() {
        return "(" + u +", " + v + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EdgeNode edge = (EdgeNode) o;

        return u.number == edge.u.number && v.number == edge.v.number;
    }

    @Override
    public int hashCode() {
        int result = u.number;
        result = 31 * result + v.number;
        return result;
    }

    public static EdgeNode create(Vertex u, Vertex v) {
        EdgeNode edge = new EdgeNode(u, v);
        EdgeNode reverseEdge = new EdgeNode(v, u);
        edge.reverseEdge = reverseEdge;
        reverseEdge.reverseEdge = edge;
        return edge;
    }
}
