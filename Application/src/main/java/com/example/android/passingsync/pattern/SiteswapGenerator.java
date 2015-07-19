package com.example.android.passingsync.pattern;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SiteswapGenerator extends AbstractPatternGenerator {

    private final String siteswap;
    private final int startPos;
    private int pos = -4 - 1;

    public SiteswapGenerator(String siteswap, int startPos) {
        this.siteswap = siteswap;
        this.startPos = startPos;
        assert siteswap.length() >= 1 : "Invalid siteswap " + siteswap;
        assert isValidSiteswap(siteswap) : "Invalid siteswap " + siteswap;

    }

    private static boolean isValidSiteswap(String siteswap) {
        Set<Integer> known = new HashSet<>();
        for (int i = 0; i < siteswap.length(); i++) {
            int p = Integer.valueOf(siteswap.substring(i, i + 1));
            int land = (i + p) % siteswap.length();
            if (known.contains(land))
                return false;
            known.add(land);
        }
        return true;
    }

    @Override
    public StartPos getStart(Passer passer) {
        String s = (siteswap + siteswap + siteswap + siteswap + siteswap + siteswap + siteswap + siteswap).substring(startPos);
        int myoffset = passer == Passer.B ? (startPos + 1) % 2 : startPos % 2;
        List<Character> initialSeq = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            initialSeq.add(s.charAt(i * 2 + myoffset));
        Set<Integer> known = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            int p = Integer.valueOf(s.substring(i, i + 1));
            int land = (i + p);
            known.add(land);
        }
        int secondHand = 0, firstHand = 0;
        for (int i = myoffset; i < s.length(); i = i + 4)
            if (!known.contains(i)) firstHand++;
        for (int i = 2 + myoffset; i < s.length(); i = i + 4)
            if (!known.contains(i)) secondHand++;
        Side startSide = passer == Passer.A ?
                ((startPos + 1) % 4 < 2 ? Side.RIGHT : Side.LEFT) :
                (startPos < 2 ? Side.RIGHT : Side.LEFT);

        return new StartPos(startSide == Side.RIGHT ? firstHand : secondHand, startSide == Side.RIGHT ? secondHand : firstHand, startSide, initialSeq);
    }

    @Override
    public Display getDisplay(Passer p) {
        final List<Character> seqA = new ArrayList<>();
        final List<Character> seqB = new ArrayList<>();
        for (int i = 0; i < siteswap.length() * 2; i++) {
            Character c = siteswap.charAt(i % siteswap.length());
            if (i % 2 == 0) {
                seqA.add(c);
                seqB.add(' ');
            } else {
                seqA.add(' ');
                seqB.add(c);
            }
        }

        return new Display(seqA, seqB, (startPos + Math.max(0,pos))%(siteswap.length()*2));
    }

    @Override
    public Map<Passer, Pair<Side,Character>> step() {
        pos++;
        Passer who = (pos + startPos) % 2 == 0 ? Passer.A : Passer.B;
        Character p;
        if (pos < 0) p = '0';
        else p = siteswap.charAt((pos + startPos) % siteswap.length());
        Side side=(pos+startPos)%4<2?Side.RIGHT:Side.LEFT;

        return Collections.singletonMap(who, new Pair<>(side,p));
    }
}
