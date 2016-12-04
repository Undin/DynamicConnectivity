package com.warrior.dynamic.connectivity;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by warrior on 08.01.16.
 */
public class DynamicConnectivityTest {

    @Test
    public void smallLinkTest() {
        for (int i = 2; i < 50; i++) {
            linkCutTest(i, Operation.REACH, Operation.LINK);
        }
    }

    @Test
    public void largeLinkTest() {
        for (int i = 50; i <= 5000; i *= 10) {
            linkCutTest(i, Operation.REACH, Operation.LINK);
        }
    }

    @Test
    public void smallLinkCutTest() {
        for (int i = 2; i < 50; i++) {
            linkCutTest(i, Operation.values());
        }
    }

    @Test
    public void largeLinkCutTest() {
        for (int i = 50; i <= 5000; i *= 10) {
            linkCutTest(i, Operation.values());
        }
    }

    private void linkCutTest(int vertexNumber, Operation... allowedOperation) {
        DynamicConnectivity simple = new SimpleDynamicConnectivity(vertexNumber);
        DynamicConnectivity fast = new FastDynamicConnectivity(vertexNumber);

        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            Operation o = allowedOperation[random.nextInt(allowedOperation.length)];
            int u = random.nextInt(vertexNumber);
            int v = random.nextInt(vertexNumber);
            switch (o) {
                case REACH:
                    boolean expected = simple.reach(u, v);
                    boolean actual = fast.reach(u, v);
                    Assert.assertEquals("reach", expected, actual);
                    break;
                case LINK:
                    simple.link(u, v);
                    fast.link(u, v);
                    break;
                case CUT:
                    simple.cut(u, v);
                    fast.cut(u, v);
                    break;
            }
        }
    }
}
