package com.warrior.dynamic.connectivity;

import java.util.*;

/**
 * Created by warrior on 01.11.15.
 */
public class Main {

    public static void main(String[] args) {
        timeTest();
    }

    private static void timeTest() {
        warmup();

        for (int i = 16; i < 5000; i *= 2) {
            System.out.println(i);

            int actionNumber = 200 * i;
            List<Action> actions = generateActions(actionNumber, i);

            DynamicConnectivity simple = new SimpleDynamicConnectivity(i);
            DynamicConnectivity fast = new FastDynamicConnectivity(i);

            long simpleTime = timeTest(simple, actions);
            System.out.format("simple. all: %d ns, per action: %d ns\n", simpleTime, simpleTime / actionNumber);

            long fastTime = timeTest(fast, actions);
            System.out.format("fast. all: %d ns, per action: %d ns \n", fastTime, fastTime / actionNumber);
        }
    }

    private static void warmup() {
        int vertexNumber = 500;
        List<Action> actions = generateActions(100 * vertexNumber, vertexNumber);

        DynamicConnectivity simple = new SimpleDynamicConnectivity(vertexNumber);
        DynamicConnectivity fast = new FastDynamicConnectivity(vertexNumber);

        long time = timeTest(simple, actions);
        time = timeTest(fast, actions);
    }


    private static List<Action> generateActions(int number, int maxVertexNumber) {
        Random random = new Random();
        Set<Edge> edges = new HashSet<>();

        List<Action> actions = new ArrayList<>(number);
        for (int j = 0; j < number; j++) {
            Operation o;
            int u, v;
            Edge edge;
            do {
                o = Operation.values()[random.nextInt(3)];
                u = random.nextInt(maxVertexNumber);
                v = random.nextInt(maxVertexNumber);
                edge = Edge.create(u, v);
            } while (!isUsefulAction(edges, o, edge));

            actions.add(new Action(o, u, v));
        }
        return actions;
    }

    private static boolean isUsefulAction(Set<Edge> edges, Operation o, Edge edge) {
        if (edge.first == edge.second) {
            return false;
        }
        switch (o) {
            case REACH:
                return true;
            case LINK:
                return !edges.contains(edge);
            case CUT:
                return edges.contains(edge);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static long timeTest(DynamicConnectivity dynamicConnectivity, List<Action> actions) {
        long start = System.nanoTime();
        for (Action action : actions) {
            apply(dynamicConnectivity, action);
        }
        long end = System.nanoTime();
        return end - start;
    }

    private static void apply(DynamicConnectivity dynamicConnectivity, Action action) {
        switch (action.operation) {
            case REACH:
                boolean simpleReach = dynamicConnectivity.reach(action.u, action.v);
                break;
            case LINK:
                dynamicConnectivity.link(action.u, action.v);
                break;
            case CUT:
                dynamicConnectivity.cut(action.u, action.v);
                break;
        }
    }

    private static class Action {
        public final Operation operation;
        public final int u;
        public final int v;

        public Action(Operation operation, int u, int v) {
            this.operation = operation;
            this.u = u;
            this.v = v;
        }
    }
}
