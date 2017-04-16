package ethanjones.cubes.core.util;

import com.badlogic.gdx.math.RandomXS128;

import java.io.Serializable;
import java.util.Random;

public final class UUID implements Serializable, Comparable<UUID> {
  private final long mostSigBits;
  private final long leastSigBits;

  public UUID(long mostSigBits, long leastSigBits) {
    this.mostSigBits = mostSigBits;
    this.leastSigBits = leastSigBits;
  }

  public static UUID randomUUID() {
    Random random = new RandomXS128();
    long a = random.nextLong();
    long b = random.nextLong();

    final byte[] bytes = new byte[16];
    bytes[0] = (byte) ((a >> 56) & 0xFFL);
    bytes[1] = (byte) ((a >> 48) & 0xFFL);
    bytes[2] = (byte) ((a >> 40) & 0xFFL);
    bytes[3] = (byte) ((a >> 32) & 0xFFL);
    bytes[4] = (byte) ((a >> 24) & 0xFFL);
    bytes[5] = (byte) ((a >> 16) & 0xFFL);
    bytes[6] = (byte) ((a >> 8) & 0xFFL);
    bytes[7] = (byte) ((a >> 0) & 0xFFL);
    bytes[8] = (byte) ((b >> 56) & 0xFFL);
    bytes[9] = (byte) ((b >> 48) & 0xFFL);
    bytes[10] = (byte) ((b >> 40) & 0xFFL);
    bytes[11] = (byte) ((b >> 32) & 0xFFL);
    bytes[12] = (byte) ((b >> 24) & 0xFFL);
    bytes[13] = (byte) ((b >> 16) & 0xFFL);
    bytes[14] = (byte) ((b >> 8) & 0xFFL);
    bytes[15] = (byte) ((b >> 0) & 0xFFL);

    bytes[6] &= 0x0f;
    bytes[6] |= 0x40;
    bytes[8] &= 0x3f;
    bytes[8] |= 0x80;

    long msb = ((bytes[0] & 0xFFL) << 56) |
            ((bytes[1] & 0xFFL) << 48) |
            ((bytes[2] & 0xFFL) << 40) |
            ((bytes[3] & 0xFFL) << 32) |
            ((bytes[4] & 0xFFL) << 24) |
            ((bytes[5] & 0xFFL) << 16) |
            ((bytes[6] & 0xFFL) << 8) |
            ((bytes[7] & 0xFFL) << 0);
    long lsb = ((bytes[8] & 0xFFL) << 56) |
            ((bytes[9] & 0xFFL) << 48) |
            ((bytes[10] & 0xFFL) << 40) |
            ((bytes[11] & 0xFFL) << 32) |
            ((bytes[12] & 0xFFL) << 24) |
            ((bytes[13] & 0xFFL) << 16) |
            ((bytes[14] & 0xFFL) << 8) |
            ((bytes[15] & 0xFFL) << 0);
    return new UUID(msb, lsb);
  }

  public static UUID fromString(String str) {
    String[] split = str.split("-");
    if (split.length != 5 || split[0].length() != 8 || split[1].length() != 4 || split[2].length() != 4 || split[3].length() != 4 || split[4].length() != 12)
      throw new IllegalArgumentException("Invalid UUID string: "+str);

    long mostSigBits = Long.decode("0x" + split[0]);
    mostSigBits <<= 16;
    mostSigBits |= Long.decode("0x" + split[1]);
    mostSigBits <<= 16;
    mostSigBits |= Long.decode("0x" + split[2]);

    long leastSigBits = Long.decode("0x" + split[3]);
    leastSigBits <<= 48;
    leastSigBits |= Long.decode("0x" + split[4]);

    return new UUID(mostSigBits, leastSigBits);  }

  public long getLeastSignificantBits() {
    return leastSigBits;
  }

  public long getMostSignificantBits() {
    return mostSigBits;
  }

  public int compareTo(UUID uuid) {
    return (this.mostSigBits < uuid.mostSigBits ? -1 : (this.mostSigBits > uuid.mostSigBits ? 1 : (this.leastSigBits < uuid.leastSigBits ? -1 : 0)));
  }

  public boolean equals(Object object) {
    return object instanceof UUID && this.mostSigBits == ((UUID) object).mostSigBits && this.leastSigBits == ((UUID) object).leastSigBits;
  }

  public int hashCode() {
    return (int) leastSigBits;
  }

  public String toString() {
    return hexString(mostSigBits >> 32, 8) + "-" + hexString(mostSigBits >> 16, 4) + "-" + hexString(mostSigBits, 4) + "-" + hexString(leastSigBits >> 48, 4) + "-" + hexString(leastSigBits, 12);
  }

  private static String hexString(long val, int digits) {
    long l = 1L << (digits * 4);
    return Long.toHexString(l | (val & (l - 1))).substring(1);
  }
}

