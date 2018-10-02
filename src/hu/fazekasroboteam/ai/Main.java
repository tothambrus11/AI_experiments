package hu.fazekasroboteam.ai;

import java.util.ArrayList;
import java.util.Random;

public class Main {

    static Random r;

    public static void main(String[] args) {

        r = new Random();
        AI ai = new AI(new int[]{2, 1});
        ArrayList<Float> input = new ArrayList<>();
        input.add(0.0f);
        input.add(0.0f);
        ArrayList<Float> output = new ArrayList<>();
        output.add(0.0f);
        long t = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            float a = r.nextFloat() / 4;
            float b = r.nextFloat() / 4;
            input.set(0, a);
            input.set(1, b);
            output.set(0, ai.reduce(2 * a + 2 * b));
            ai.addTest(input, output);
            ai.printError();
            ai.modify(0.1f);
        }
        input.set(0, 0.4f);
        input.set(1, 0.3f);

        System.out.println("\n" + input.get(0) + " + " + input.get(1) + " => " + (ai.test(input).get(0) / (1.0f - ai.test(input).get(0))) / 2);
        System.out.println("\n" + ((System.currentTimeMillis() - t) / 1000.0f) + "s");

    }
}