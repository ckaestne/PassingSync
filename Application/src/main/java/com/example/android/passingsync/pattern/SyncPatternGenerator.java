package com.example.android.passingsync.pattern;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SyncPatternGenerator extends AbstractPatternGenerator {

    private final String sequence;
    boolean everyOther = false;
    private int pos = -4 - 1;


    /**
     * sequence consists of p and d and t for single and double and triple passes
     * plus solo siteswap numbers, e.g p33
     *
     * @param sequence
     */
    public SyncPatternGenerator(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public StartPos getStart(Passer passer) {
        return new StartPos(0, 0, Side.RIGHT, Collections.EMPTY_LIST);

    }

    @Override
    public Display getDisplay(Passer p) {
        final List<Character> seqA = new ArrayList<>();
        final List<Character> seqB = new ArrayList<>();
        for (int i = 0; i < sequence.length(); i++) {
            Character c = sequence.charAt(i);
            seqA.add(c);
            seqB.add(c);
        }

        return new Display(seqA, seqB, (Math.max(0, pos)) % (sequence.length()));
    }

    @Override
    public Map<Passer, Pair<Side, Character>> step() {
        everyOther = !everyOther;
        if (everyOther) {
            pos++;
            Character p;
            if (pos < 0) p = '0';
            else p = sequence.charAt(pos % sequence.length());
            Character o = '0';
            if (p == '1') o = '2';
            if (p == '2') o = '4';
            if (p == '3') o = '6';
            if (p == '4') o = '8';
            if (p == 'p') o = '7';
            if (p == 'd') o = '9';
            if (p == 't') o = 'a';

            Side side = pos % 2 == 0 ? Side.RIGHT : Side.LEFT;

            Map<Passer, Pair<Side, Character>> r = new HashMap<>();
            r.put(Passer.A, new Pair<>(side, o));
            r.put(Passer.B, new Pair<>(side, o));
            return r;
        } else return Collections.EMPTY_MAP;
    }
}
