package edu.cmu.mastersofflyingobjects.passingsync.pattern;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class RandomSiteswapGenerator extends AbstractPatternGenerator {

    //    static byte[2047,2047]
    static final Map<Integer, SiteswapState> stateCache = new HashMap<>();
    static Map<SiteswapState, Map<SiteswapState, Integer>> graph = genSiteswapGraph(5, new int[]{2, 4, 6, 7, 8});

    private static int c(String x) {
        int r = 0;
        for (char c : x.toCharArray())
            if (c == '1')
                r++;
        return r;
    }

    private static Map<SiteswapState, Map<SiteswapState, Integer>> genSiteswapGraph(int objectCount, int[] allowedThrows) {

        Map<SiteswapState, Map<SiteswapState, Integer>> result = new HashMap<>();
        for (int i = 0; i < 2048; i++)
            if (c(Integer.toBinaryString(i)) == objectCount) {

                SiteswapState s = SiteswapState.create(i);
                Map<SiteswapState, Integer> transitions = new HashMap<>();
                for (int t : allowedThrows) {
                    if (s.mayTransition(t))
                        transitions.put(s.p(t), t);
                }
                result.put(s, transitions);

            }

        boolean stable = false;
        while (!stable) {
            stable = true;

            for (SiteswapState s : new HashSet<>( result.keySet()))
                if (result.get(s).isEmpty()) {
                    result.remove(s);
                    stable = false;
                }
            for (SiteswapState s : result.keySet()) {
                Map<SiteswapState, Integer> transitions = result.get(s);
                for (SiteswapState target: new HashSet<>(transitions.keySet()))
                    if (!result.containsKey(target)) {
                        transitions.remove(target);
                        stable = false;
                    }
            }


        }


        return result;
    }


    static class SiteswapState {
        private final int state;

        public static SiteswapState create(int state) {
            if (!stateCache.containsKey(state))
                stateCache.put(state, new SiteswapState(state));
            return stateCache.get(state);
        }

        private SiteswapState(int state) {
            this.state = state;
        }

        public String toString() {
            return Integer.toBinaryString(state);
        }

        public boolean mayTransition(int athrow) {
            //have a ball now and have an empty spot in x beats
            return ((state % 2 == 1) && ((state >> athrow) % 2) == 0);
        }

        public SiteswapState p(int athrow) {
            if (athrow == 0) {
                assert state % 2 == 0 : "need to throw now";

                return SiteswapState.create((state >> 1));
            } else {
                assert state % 2 == 1 : "cannot throw now";

                return SiteswapState.create((state >> 1) | (int) Math.pow(2, athrow-1));
            }

        }


        public SiteswapState pa() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 1024);
        }

        public SiteswapState p7() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 128);
        }

        public SiteswapState p9() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 512);
        }

        public SiteswapState p8() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 256);
        }

        public SiteswapState p6() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 64);
        }

        public SiteswapState p5() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 32);
        }

        public SiteswapState p4() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 16);
        }

        public SiteswapState p2() {
            assert state % 2 == 1 : "cannot throw now";

            return SiteswapState.create((state >> 1) | 4);
        }

        public SiteswapState p0() {
            assert state % 2 == 0 : "need to throw now";

            return SiteswapState.create((state >> 1));
        }
    }


    boolean everyOther = false;
    private int pos = -2 - 1;


    @Override
    public StartPos getStart(Passer passer) {
        return new StartPos(2, 1, Side.RIGHT, Collections.EMPTY_LIST);

    }

    @Override
    public Display getDisplay(Passer p) {
        final List<Character> seqA = new ArrayList<>();
        seqA.add('r');
        seqA.add('a');
        seqA.add('n');
        seqA.add('d');
        seqA.add('o');
        seqA.add('m');

        return new Display(seqA, seqA, (Math.max(0, pos)) % 6);
    }

    @Override
    public Map<Passer, Pair<Side, Character>> step() {
        everyOther = !everyOther;
        if (everyOther) {
            pos++;
            Character p;
            if (pos < 0)
                p = '0';
            else if (pos == 0)
                p = '7';
            else if (Math.random() > 0.66)
                p = '7';
            else p = '6';

            Side side = pos % 2 == 0 ? Side.RIGHT : Side.LEFT;

            Map<Passer, Pair<Side, Character>> r = new HashMap<>();
            r.put(Passer.A, new Pair<>(side, p));
            r.put(Passer.B, new Pair<>(side, p));
            return r;
        } else return Collections.EMPTY_MAP;
    }
}
