package ru.zubmike.service.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class MathUtils {

	private static final char DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().getDecimalSeparator();

	public static final int DEFAULT_SCALE_VALUE = 2;

	public static double divide(double value1, double value2) {
		return scale(value1 / value2);
	}

	public static double divide(double value1, double value2, int scale) {
		return scale(value1 / value2, scale);
	}

	public static double multiply(double value1, double value2) {
		return scale(value1 * value2);
	}

	public static double scale(double value) {
		return scale(value, DEFAULT_SCALE_VALUE);
	}

	public static double scale(double value, int newScale) {
		return BigDecimal.valueOf(value)
				.setScale(newScale, RoundingMode.HALF_UP)
				.doubleValue();
	}

	public static <T> double sum(Collection<T> items, ToDoubleFunction<T> function) {
		return scale(items.stream().mapToDouble(function).sum());
	}

	public static <T extends Number> double sum(Collection<T> items) {
		return scale(items.stream().mapToDouble(Number::doubleValue).sum());
	}

	public static <T extends Number> double sum(T... items) {
		return scale(Stream.of(items).mapToDouble(Number::doubleValue).sum());
	}

	public static int getDecimalPart(double value) {
		String formatValue = String.format("%." + DEFAULT_SCALE_VALUE + "f", value);
		int separatorIndex = formatValue.indexOf(DECIMAL_SEPARATOR);
		return separatorIndex > 0
				? Integer.parseInt(formatValue.substring(separatorIndex + 1))
				: 0;
	}

	public static double getPercent(double value, double totalValue) {
		return totalValue > 0
				? MathUtils.multiply(MathUtils.divide(value, totalValue), 100)
				: 0;
	}
}
