package com.warrior.dynamic.connectivity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by warrior on 04.01.16.
 */
public class SimpleDynamicConnectivity implements DynamicConnectivity {

    private final List<Set<Integer>> edges;
    private final boolean[] visited;

    public SimpleDynamicConnectivity(int vertexNumber) {
        edges = IntStream.range(0, vertexNumber)
                .<Set<Integer>>mapToObj(i -> new HashSet<>())
                .collect(Collectors.toList());
        visited = new boolean[vertexNumber];
    }

    @Override
    public void link(int u, int v) {
        edges.get(u).add(v);
        edges.get(v).add(u);
    }

    @Override
    public void cut(int u, int v) {
        edges.get(u).remove(v);
        edges.get(v).remove(u);
    }

    @Override
    public boolean reach(int u, int v) {
        Arrays.fill(visited, false);
        visited[u] = true;
        return dfs(u, v, visited);
    }

    private boolean dfs(int u, int v, boolean[] visited) {
        if (u == v) {
            return true;
        }
        for (int w : edges.get(u)) {
            if (!visited[w]) {
                visited[w] = true;
                boolean reached = dfs(w, v, visited);
                if (reached) {
                    return true;
                }
            }
        }
        return false;
    }
}
