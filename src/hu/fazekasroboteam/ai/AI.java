package hu.fazekasroboteam.ai;

        import java.util.ArrayList;

public class AI {

    /**
     * WEIGHTS
     * Dimensions:
     * 1. Layer-1
     * 2. To (row)
     * 3. From (row)
     */
    public ArrayList<ArrayList<ArrayList<Float>>> w = new ArrayList<>();
    public ArrayList<ArrayList<ArrayList<Float>>> wEffect = new ArrayList<>();
    public ArrayList<ArrayList<ArrayList<Float>>> wEffectSum = new ArrayList<>();

    /**
     * CONSTANTS
     * Dimensions:
     * 1. Layer
     * 2. Row
     */
    public ArrayList<ArrayList<Float>> b = new ArrayList<>();
    public ArrayList<ArrayList<Float>> bEffect = new ArrayList<>();
    public ArrayList<ArrayList<Float>> bEffectSum = new ArrayList<>();

    /**
     * WEIGHTED SUMS
     * Dimensions:
     * 1. Layer
     * 2. Row
     */
    public ArrayList<ArrayList<Float>> z = new ArrayList<>();

    /**
     * VALUES
     * Dimensions:
     * 1. Layer
     * 2. Row
     */
    public ArrayList<ArrayList<Float>> a = new ArrayList<>();
    public ArrayList<ArrayList<Float>> aEffect = new ArrayList<>();

    /**
     * GOALS
     * Dimensions:
     * 1. Row
     */
    public ArrayList<Float> y = new ArrayList<>();

    public int[] lh;


    AI(int[] lh) {
        this.lh = lh;
        setTo0();
    }

    public void addTest(ArrayList<Float> input, ArrayList<Float> output) {
        applyInputs(input);
        applyOutputs(output);
        count();
        countEffects();
        addEffects();
    }

    public ArrayList<Float> test(ArrayList<Float> input){
        applyInputs(input);
        count();
        return a.get(lh.length-1);
    }

    private void setTo0() {
        ArrayList<Float> a_0 = new ArrayList<>();
        ArrayList<Float> b_0 = new ArrayList<>();
        ArrayList<Float> bEffect_0 = new ArrayList<>();
        ArrayList<Float> bEffectSum_0 = new ArrayList<>();
        ArrayList<Float> z_0 = new ArrayList<>();
        ArrayList<Float> aEffect_0 = new ArrayList<>();
        for (int i = 0; i < lh[0]; i++) {
            a_0.add(0.0f);
            b_0.add(0.0f);
            bEffect_0.add(0.0f);
            bEffectSum_0.add(0.0f);
            z_0.add(0.0f);
            aEffect_0.add(0.0f);
            y.add(0.0f);
        }
        a.add(a_0);
        b.add(b_0);
        bEffect.add(bEffect_0);
        bEffectSum.add(bEffectSum_0);
        z.add(z_0);
        aEffect.add(aEffect_0);

        for (int i = 1; i < lh.length; i++) {
            ArrayList<ArrayList<Float>> w_temp_2d = new ArrayList<>();
            ArrayList<ArrayList<Float>> wEffect_temp_2d = new ArrayList<>();
            ArrayList<ArrayList<Float>> wEffectSum_temp_2d = new ArrayList<>();
            ArrayList<Float> b_temp_1d = new ArrayList<>();
            ArrayList<Float> bEffect_temp_1d = new ArrayList<>();
            ArrayList<Float> bEffectSum_temp_1d = new ArrayList<>();
            ArrayList<Float> z_temp_1d = new ArrayList<>();
            ArrayList<Float> a_temp_1d = new ArrayList<>();
            ArrayList<Float> aEffect_temp_1d = new ArrayList<>();
            for (int j = 0; j < lh[i]; j++) {
                ArrayList<Float> w_temp_1d = new ArrayList<>();
                ArrayList<Float> wEffect_temp_1d = new ArrayList<>();
                ArrayList<Float> wEffectSum_temp_1d = new ArrayList<>();
                for (int k = 0; k < lh[i - 1]; k++) {
                    w_temp_1d.add(1.0f);
                    wEffect_temp_1d.add(0.0f);
                    wEffectSum_temp_1d.add(0.0f);
                }
                w_temp_2d.add(w_temp_1d);
                wEffect_temp_2d.add(wEffect_temp_1d);
                wEffectSum_temp_2d.add(wEffectSum_temp_1d);
                b_temp_1d.add(0.0f);
                bEffect_temp_1d.add(0.0f);
                bEffectSum_temp_1d.add(0.0f);
                z_temp_1d.add(0.0f);
                a_temp_1d.add(0.0f);
                aEffect_temp_1d.add(0.0f);
            }
            w.add(w_temp_2d);
            wEffect.add(wEffect_temp_2d);
            wEffectSum.add(wEffectSum_temp_2d);
            b.add(b_temp_1d);
            bEffect.add(bEffect_temp_1d);
            bEffectSum.add(bEffectSum_temp_1d);
            z.add(z_temp_1d);
            a.add(a_temp_1d);
            aEffect.add(aEffect_temp_1d);
        }
    }

    private void applyInputs(ArrayList<Float> input) {
        a.set(0, input);
    }

    private void applyOutputs(ArrayList<Float> output) {
        y = output;
    }

    private void count() {
        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                float sum = 0.0f;
                for (int k = 0; k < lh[i - 1]; k++) {
                    sum += a.get(i - 1).get(k) * w.get(i - 1).get(j).get(k);
                }
                sum += b.get(i).get(j);
                z.get(i).set(j, sum);
                a.get(i).set(j, reduce(z.get(i).get(j)));
            }
        }
    }

    private void countEffects() {
        for (int j = 0; j < lh[lh.length - 1]; j++) {
            float set_aEffect;
            set_aEffect = 2 * (a.get(lh.length - 1).get(j) - y.get(j));
            aEffect.get(lh.length - 1).set(j, set_aEffect);
        }
        for (int i = lh.length - 2; i > 0; i--) {
            for (int k = 0; k < lh[i]; k++) {
                float set_aEffect = 0;
                for (int j = 0; j < lh[i + 1]; j++) {
                    set_aEffect += w.get(i).get(j).get(k) * reduce_diff(z.get(i + 1).get(j)) * aEffect.get(i + 1).get(j);
                }
                aEffect.get(i).set(k, set_aEffect);
            }
        }

        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                for (int k = 0; k < lh[i - 1]; k++) {
                    float set_wEffect;
                    set_wEffect = a.get(i - 1).get(k) * reduce_diff(z.get(i).get(j)) * aEffect.get(i).get(j);
                    wEffect.get(i - 1).get(j).set(k, set_wEffect);
                }
            }
        }

        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                float set_bEffect;
                set_bEffect = reduce_diff(z.get(i).get(j)) * aEffect.get(i).get(j);
                bEffect.get(i).set(j, set_bEffect);
            }
        }
    }

    private void addEffects() {
        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                for (int k = 0; k < lh[i - 1]; k++) {
                    float wEffectSumTo;
                    wEffectSumTo = wEffectSum.get(i - 1).get(j).get(k) + wEffect.get(i - 1).get(j).get(k);
                    wEffectSum.get(i - 1).get(j).set(k, wEffectSumTo);
                }
            }
        }

        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                float bEffectSumTo;
                bEffectSumTo = bEffectSum.get(i).get(j) + bEffect.get(i).get(j);
                bEffect.get(i).set(j, bEffectSumTo);
            }
        }
    }

    public void modify(float s) {
        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                for (int k = 0; k < lh[i - 1]; k++) {
                    w.get(i - 1).get(j).set(k, w.get(i - 1).get(j).get(k) - s * wEffectSum.get(i - 1).get(j).get(k));
                    wEffectSum.get(i - 1).get(j).set(k, 0.0f);
                }
            }
        }

        for (int i = 1; i < lh.length; i++) {
            for (int j = 0; j < lh[i]; j++) {
                bEffect.get(i).set(j, b.get(i).get(j) - s * bEffectSum.get(i).get(j));
                bEffectSum.get(i).set(j, 0.0f);
            }
        }
    }

    public void print() {
        System.out.println("a: " + a.toString());
        System.out.println("aEffect: " + aEffect.toString());
        System.out.println("w: " + w.toString());
        System.out.println("wEffect: " + wEffect.toString());
        System.out.println("wEffectSum: " + wEffectSum.toString());
        System.out.println("b: " + b.toString());
        System.out.println("bEffect: " + bEffect.toString());
        System.out.println("bEffectSum: " + bEffectSum.toString());
        System.out.println("z: " + z.toString());
        System.out.println("y: " + y.toString());
    }

    public void printError() {
        count();
        System.out.println("Error: " + error());
    }

    private float error() {
        float sum = 0.0f;
        for (int j = 0; j < lh[lh.length - 1]; j++) {
            sum += (a.get(lh.length - 1).get(j) - y.get(j)) * (a.get(lh.length - 1).get(j) - y.get(j));
        }
        return sum;
    }

    public float reduce(float x) {
        if (x > 0) {
            return x / (x + 1);
        }
        return 0.0f;
    }

    private float reduce_diff(float x) {
        if (x > 0) {
            return 1 / ((x + 1) * (x + 1));
        }
        return 0.0f;

    }

}