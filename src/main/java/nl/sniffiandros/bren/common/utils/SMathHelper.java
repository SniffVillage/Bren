package nl.sniffiandros.bren.common.utils;

// Simple Math Helper
public class SMathHelper {
    public static String roundNumberStr(double v) {
        double n = Double.parseDouble(String.valueOf(v));
        return n % 1 == 0 ? Integer.toString((int) n) : String.valueOf(v);
    }
}
