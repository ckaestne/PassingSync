package edu.cmu.mastersofflyingobjects.passingsync.pattern;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class RandomSiteswapGenerator extends AbstractPatternGenerator {

    public RandomSiteswapGenerator(int seed, String config) {
        r = new Random(seed);
        initRandomSiteswapSeq(seed);
    }


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

            for (SiteswapState s : new HashSet<>(result.keySet()))
                if (result.get(s).isEmpty()) {
                    result.remove(s);
                    stable = false;
                }
            for (SiteswapState s : result.keySet()) {
                Map<SiteswapState, Integer> transitions = result.get(s);
                for (SiteswapState target : new HashSet<>(transitions.keySet()))
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

                return SiteswapState.create((state >> 1) | (int) Math.pow(2, athrow - 1));
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

    private final Random r;
    //always stores the next 30 steps; new random steps added to the end, current passes removed from the beginning
    private final LinkedList<Integer> siteswapSeq = new LinkedList<>();
    private RandomSiteswapGenerator.SiteswapState lastState;

    private void initRandomSiteswapSeq(int seed) {
        lastState = RandomSiteswapGenerator.SiteswapState.create(31);
        for (int i = 0; i < 60; i++) {
            addRandomSiteswapStep();
        }
    }

    private void addRandomSiteswapStep() {
        Map<RandomSiteswapGenerator.SiteswapState, Integer> transitions = RandomSiteswapGenerator.graph.get(lastState);
        Map.Entry<RandomSiteswapGenerator.SiteswapState, Integer> rand =
                (Map.Entry<RandomSiteswapGenerator.SiteswapState, Integer>) transitions.entrySet().toArray()[r.nextInt(transitions.size())];
        siteswapSeq.add(rand.getValue());
        lastState = rand.getKey();
    }


    private int pos = -2 - 1;


    @Override
    public StartPos getStart(Passer passer) {
        return new StartPos(2, 1, Side.RIGHT, Collections.EMPTY_LIST);

    }

    @Override
    public Display getDisplay(Passer p) {

        final List<Character> seqA = new ArrayList<>();
        final List<Character> seqB = new ArrayList<>();
        for (int i = 0; i < siteswapSeq.size(); i++) {
            Integer c = siteswapSeq.get(i);
            boolean evenPass = (Math.max(pos,0) + i) % 2 == 0;
            if (evenPass) {
                seqA.add(c.toString().charAt(0));
                seqB.add(' ');
            } else {
                seqA.add(' ');
                seqB.add(c.toString().charAt(0));
            }
        }
        return new Display(seqA, seqB, 0);

    }

    @Override
    public Map<Passer, Pair<Side, Character>> step() {
        pos++;
        Character p;
        if (pos >= 0) {
            if (pos>0)
                siteswapSeq.pop();//discard old head
            addRandomSiteswapStep();
            p = siteswapSeq.peek().toString().charAt(0);
        } else
            p = '0';


        Side side = pos % 4 < 2 ? Side.RIGHT : Side.LEFT;
        Passer who = pos % 2 == 0 ? Passer.A : Passer.B;
        Map<Passer, Pair<Side, Character>> r = new HashMap<>();
        r.put(who, new Pair<>(side, p));
        return r;
    }
}
