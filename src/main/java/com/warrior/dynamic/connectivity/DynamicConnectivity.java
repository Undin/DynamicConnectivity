package com.warrior.dynamic.connectivity;

/**
 * Created by warrior on 04.01.16.
 */
public interface DynamicConnectivity {
    void link(int u, int v);
    void cut(int u, int v);
    boolean reach(int u, int v);
}
