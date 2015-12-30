package edu.cmu.mastersofflyingobjects.passingsync.pattern;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class RandomSyncGenerator extends AbstractPatternGenerator {

    private final int c6;
    private final int c9;
    private final int c7;
    private final int cSum;
    boolean everyOther = false;
    private int pos = -2 - 1;
    private boolean nextWait = false;
    private Random r = new Random();

    public RandomSyncGenerator(String config) {
        String[] c = config.split(";");
        c6 = Integer.parseInt(c[0]);
        c7 = Integer.parseInt(c[1]);
        c9 = Integer.parseInt(c[2]);
        cSum = c6 + c7 + c9;

        assert (cSum > 0);
    }


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
            else if (nextWait) {
                p = '4';
                nextWait = false;
            } else p = getRandomPass();


            Side side = pos % 2 == 0 ? Side.RIGHT : Side.LEFT;

            Map<Passer, Pair<Side, Character>> r = new HashMap<>();
            r.put(Passer.A, new Pair<>(side, p));
            r.put(Passer.B, new Pair<>(side, p));
            return r;
        } else return Collections.EMPTY_MAP;
    }

    private Character getRandomPass() {
        int rr = r.nextInt(cSum);
        if (rr < c6)
            return '6';
        if (rr < c6 + c7)
            return '7';
        nextWait = true;
        return '9';
    }


}
