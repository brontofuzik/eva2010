package eva2010.cv7;

public class RastriginFunction implements RealFunction {

    // <editor-fold defaultstate="collapsed" desc="Methods">

    @Override
    public double value(double[] x) {
        int n = x.length;
        double sum = 0;
        for (int i = 0; i < n; i++) {
                sum += x[i] * x[i] - 10 * Math.cos(2 * Math.PI * x[i]);
        }
        return (35 * n - sum) / (35 * n + 10 * n);
    }

    // </editor-fold>
}
