package edu.cmu.mastersofflyingobjects.passingsync.pattern;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RandomSyncGenerator extends AbstractPatternGenerator {

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
