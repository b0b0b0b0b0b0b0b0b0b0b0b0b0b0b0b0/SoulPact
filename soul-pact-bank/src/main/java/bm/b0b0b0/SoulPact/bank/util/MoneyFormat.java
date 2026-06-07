package bm.b0b0b0.SoulPact.bank.util;

public final class MoneyFormat {

    private MoneyFormat() {
    }

    public static String format(double amount) {
        if (Math.rint(amount) == amount) {
            return String.valueOf((long) amount);
        }
        return String.format(java.util.Locale.US, "%.2f", amount);
    }
}
