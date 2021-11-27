package iot.qiot.piros.edge.core.util;

import java.util.PrimitiveIterator.OfDouble;
import java.util.PrimitiveIterator.OfInt;
import java.util.PrimitiveIterator.OfLong;
import java.util.Random;

public class NumberUtil {

  public static OfInt intRandomNumberGenerator(int min, int max) {
    return (new Random()).ints(min, max).iterator();
  }

  public static OfLong longRandomNumberGenerator(long min, long max) {
    return (new Random()).longs(min, max).iterator();
  }

  public static OfDouble doubleRandomNumberGenerator(double min, double max) {
    return (new Random()).doubles(min, max).iterator();
  }
}
