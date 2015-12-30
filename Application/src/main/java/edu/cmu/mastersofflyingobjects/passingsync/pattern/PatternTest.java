package edu.cmu.mastersofflyingobjects.passingsync.pattern;

import java.util.Map;
import java.util.Random;

/**
 * Created by ckaestne on 7/18/2015.
 */
public class PatternTest {

    public static void main(String[] args) {
//        AbstractPatternGenerator p = new SiteswapGenerator("972", 2);
//        AbstractPatternGenerator.Display d = p.getDisplay(AbstractPatternGenerator.Passer.A);
//        System.out.println(d.toString());
//
//        System.out.println("A: " + p.getStart(AbstractPatternGenerator.Passer.A));
//        System.out.println("B: " + p.getStart(AbstractPatternGenerator.Passer.B));
//
//        for (int i = 0; i < 20; i++)
//            System.out.println(p.step());

        RandomSiteswapGenerator.SiteswapState t =  RandomSiteswapGenerator.SiteswapState.create(63);

        Random r=new Random();
//        System.out.println(t.p(6));
        RandomSiteswapGenerator gen = new RandomSiteswapGenerator(100, "6;2456789");

        System.out.println(gen.graph);
        for (int i=0;i<80;i++) {
            Map<RandomSiteswapGenerator.SiteswapState, Integer> transitions = gen.graph.get(t);
//            System.out.println(transitions);
            Map.Entry<RandomSiteswapGenerator.SiteswapState, Integer> rand =
                    (Map.Entry<RandomSiteswapGenerator.SiteswapState, Integer>) transitions.entrySet().toArray()[r.nextInt(transitions.size())];
            System.out.print(rand.getValue());
            t=rand.getKey();
        }

//        System.out.println(t);
//        t=t.p(7);
//        System.out.println(t);
//        t=t.p(7);
//        System.out.println(t);
//        t=t.p(7);
//        System.out.println(t);
//        t=t.p(7);
//        System.out.println(t);
//        t=t.p(2);
//        System.out.println(t);
//
//        System.out.println(RandomSiteswapGenerator.graph);


//        for (int i = 0; i<2048;i++)
//            if (c(Integer.toBinaryString(i))==6)
//                System.out.println(i);
    }
   static int c(String x) {
        int r=0;
        for (char c:x.toCharArray())
            if (c=='1')
                r++;
        return r;
    }
}
