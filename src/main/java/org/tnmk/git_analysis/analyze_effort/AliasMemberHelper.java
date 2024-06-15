package org.tnmk.git_analysis.analyze_effort;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public class AliasMemberHelper {

  /**
   * @return this value is used to identify unique member across many repos
   * Note that authors with the same aliases are considered as single one unique member. It means such authors will have the same memberKey.
   */
  public static String getMemberKey(List<String> aliases) {
    String firstAlias = aliases.get(0);
    return uniqueNumberWithCrypto(firstAlias).toString();
  }

  private static BigInteger uniqueNumberWithCrypto(String firstAlias) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    byte[] hashBytes = md.digest(firstAlias.getBytes(StandardCharsets.UTF_8));
    BigInteger bigInteger = new BigInteger(1, hashBytes);
    return bigInteger;
  }

  /**
   * This method has better performance than {@link #uniqueNumberWithCrypto(String)}, but less uniqueness.
   */
  private static long uniqueNumberWithUuid(String firstAlias) {
    UUID uuid = UUID.nameUUIDFromBytes(firstAlias.getBytes());
    long mostSigBits = uuid.getMostSignificantBits();
    long leastSigBits = uuid.getLeastSignificantBits();
    long positiveNumber = (mostSigBits & Long.MAX_VALUE) ^ (leastSigBits & Long.MAX_VALUE);
    return positiveNumber;
  }
}
