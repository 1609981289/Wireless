package com.xie.Util;
import java.util.Locale;

/**
 * Byte转换工具
 *
 * @author yangle
 */
@SuppressWarnings("all")
public class ByteUtil {

    /**
     * 十六进制字符串转byte[]
     *
     * @param hex
     *            十六进制字符串
     * @return byte[]
     */
    public static byte[] hex2byte(String hex) {
        String digital = "0123456789ABCDEF";
        String hex1 = hex.replace(" ", "");
        char[] hex2char = hex1.toCharArray();
        byte[] bytes = new byte[hex1.length() / 2];
        byte temp;
        for (int p = 0; p < bytes.length; p++) {
            temp = (byte) (digital.indexOf(hex2char[2 * p]) * 16);
            temp += digital.indexOf(hex2char[2 * p + 1]);
            bytes[p] = (byte) (temp & 0xff);
        }
        return bytes;
    }

    /**
     * byte[]转十六进制字符串
     *
     * @param
     *
     * @return 十六进制字符串
     */
    private static final char[] BYTE2HEX=(
            "000102030405060708090A0B0C0D0E0F"+

                    "101112131415161718191A1B1C1D1E1F"+

                    "202122232425262728292A2B2C2D2E2F"+

                    "303132333435363738393A3B3C3D3E3F"+

                    "404142434445464748494A4B4C4D4E4F"+

                    "505152535455565758595A5B5C5D5E5F"+

                    "606162636465666768696A6B6C6D6E6F"+

                    "707172737475767778797A7B7C7D7E7F"+

                    "808182838485868788898A8B8C8D8E8F"+

                    "909192939495969798999A9B9C9D9E9F"+

                    "A0A1A2A3A4A5A6A7A8A9AAABACADAEAF"+

                    "B0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF"+

                    "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF"+

                    "D0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF"+

                    "E0E1E2E3E4E5E6E7E8E9EAEBECEDEEEF"+

                    "F0F1F2F3F4F5F6F7F8F9FAFBFCFDFEFF").toCharArray();

    public static String byteArrayToHexString(byte[] bytes) {
        final int len = bytes.length;
        final char[] chars = new char[len<<1];
        int hexIndex;
        int idx = 0;
        int ofs = 0;
        while(ofs<len)
        {
            hexIndex = (bytes[ofs++]&0xFF)<<1;
            chars[idx++] = BYTE2HEX[hexIndex++];
            chars[idx++] = BYTE2HEX[hexIndex];
        }
        return new String(chars);
    }

    /**
     * byte转十六进制字符
     *
     * @param b
     *            byte
     * @return 十六进制字符
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase(Locale.getDefault());
    }
}

