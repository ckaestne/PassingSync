package com.example.android.passingsync.pattern;

/**
 * Created by ckaestne on 7/18/2015.
 */
public class PatternTest {

    public static void main(String[] args) {
        AbstractPatternGenerator p = new SiteswapGenerator("972", 2);
        AbstractPatternGenerator.Display d = p.getDisplay(AbstractPatternGenerator.Passer.A);
        System.out.println(d.toString());

        System.out.println("A: " + p.getStart(AbstractPatternGenerator.Passer.A));
        System.out.println("B: " + p.getStart(AbstractPatternGenerator.Passer.B));

        for (int i = 0; i < 20; i++)
            System.out.println(p.step());
    }
}
