package com.zysl.cloud.aws.utils;

import java.security.MessageDigest;

public class MD5Utils {
  private static final String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5",
      "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

  private static String byteArrayToHexString(byte b[]) {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      resultSb.append(byteToHexString(b[i]));
    }

    return resultSb.toString();
  }

  private static String byteToHexString(byte b) {
    int n = b;
    if (n < 0) {
      n += 256;
    }
    int d1 = n / 16;
    int d2 = n % 16;
    return HEX_DIGITS[d1] + HEX_DIGITS[d2];
  }

  /**
   * 字符串MD5加密
   * @param str
   * @return
   */
  public static String encode(String str)  {
    return encode(str, "utf8");
  }

  public static String encode(String origin, String charsetname) {
    String resultString = null;
    try {
      resultString = new String(origin);
      MessageDigest md = MessageDigest.getInstance("MD5");
      if (charsetname == null || "".equals(charsetname)) {
        resultString = byteArrayToHexString(md.digest(resultString
            .getBytes()));
      } else {
        resultString = byteArrayToHexString(md.digest(resultString
            .getBytes(charsetname)));
      }
    } catch (Exception exception) {
    }
    return resultString;
  }

  public static void main(String[] args){
    System.out.println(MD5Utils.encode("1111111"));
  }
}
