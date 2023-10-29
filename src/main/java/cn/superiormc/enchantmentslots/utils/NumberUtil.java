package cn.superiormc.enchantmentslots.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class NumberUtil {
    private final static Map<Integer, String> romanNumerals = new LinkedHashMap<>();

    static {
        romanNumerals.put(1000, "M");
        romanNumerals.put(900, "CM");
        romanNumerals.put(500, "D");
        romanNumerals.put(400, "CD");
        romanNumerals.put(100, "C");
        romanNumerals.put(90, "XC");
        romanNumerals.put(50, "L");
        romanNumerals.put(40, "XL");
        romanNumerals.put(10, "X");
        romanNumerals.put(9, "IX");
        romanNumerals.put(5, "V");
        romanNumerals.put(4, "IV");
        romanNumerals.put(1, "I");
    }

    public static String convertToRoman(int number) {
        StringBuilder romanNumber = new StringBuilder();

        for (Map.Entry<Integer, String> entry : romanNumerals.entrySet()) {
            int value = entry.getKey();
            String symbol = entry.getValue();

            while (number >= value) {
                romanNumber.append(symbol);
                number -= value;
            }
        }

        return romanNumber.toString();
    }
}
