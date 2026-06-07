package bm.b0b0b0.SoulPact.chest.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MoneyFormat {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private MoneyFormat() {
    }

    public static String format(double amount) {
        return FORMAT.format(amount);
    }
}
