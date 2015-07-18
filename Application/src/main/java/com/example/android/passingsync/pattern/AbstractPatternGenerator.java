package com.example.android.passingsync.pattern;

import java.util.List;
import java.util.Map;

/**
 * this contains the core mechanisms for patterns
 * <p/>
 * subclasses for siteswaps, synchronous patterns and random patterns possible
 */
public abstract class AbstractPatternGenerator {

    public abstract StartPos getStart(Passer p);

    public abstract Display getDisplay(Passer p);

    /**
     * returns actions for zero to all both passers
     */
    public abstract Map<Passer, Character> step();

    enum Passer {
        A,//A has straight singles
        B//B has crossing singles
    }

    enum Side {LEFT, RIGHT}

    static class StartPos {
        final int leftHand;
        final int rightHand;
        final Side firstHand;
        final List<Character> initialSequence;

        StartPos(int rightHand, int leftHand, Side firstHand, List<Character> initialSequence) {
            this.leftHand = leftHand;
            this.rightHand = rightHand;
            this.firstHand = firstHand;
            this.initialSequence = initialSequence;
        }

        @Override
        public String toString() {
            return String.format("Start with %d right and %d left; start %s with %s", rightHand, leftHand, firstHand.toString(), initialSequence);
        }
    }

    static class Display {

        final List<Character> seqA;
        final List<Character> seqB;
        final int highlight;//index in first+second list (between zero and (seqA++seqB).length

        Display(List<Character> seqA,
                List<Character> seqB,
                int highlight) {
            this.seqA = seqA;
            this.seqB = seqB;
            assert (seqA.size() == seqB.size());
            this.highlight = highlight;
        }

        @Override
        public String toString() {
            StringBuffer out = new StringBuffer();
            StringBuffer highlightStr = new StringBuffer();
            int idx = -1;
            for (Character a : seqA) {
                out.append(a);
                out.append(" ");
                idx++;
                if (idx == highlight)
                    highlightStr.append("* ");
                else
                    highlightStr.append("  ");
            }
            out.append("\n");
            out.append(highlightStr);
            out.append("\n");
            for (Character a : seqB) {
                out.append(a);
                out.append(" ");
            }
            out.append("\n");
            return out.toString();
        }
    }


}
