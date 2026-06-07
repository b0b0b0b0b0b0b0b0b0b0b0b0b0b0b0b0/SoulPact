package bm.b0b0b0.SoulPact.land.util;

import java.util.Locale;

public final class MoneyFormat {

    private MoneyFormat() {
    }

    public static String format(double amount) {
        if (Math.abs(amount - Math.rint(amount)) < 0.001D) {
            return String.format(Locale.US, "%.0f", amount);
        }
        return String.format(Locale.US, "%.2f", amount);
    }
}
