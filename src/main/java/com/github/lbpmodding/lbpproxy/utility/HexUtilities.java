package com.github.lbpmodding.lbpproxy.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HexUtilities {

    public static String bytesToHexString(byte... bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte current : bytes) {
            builder.append(String.format("%02x", current));
        }
        return builder.toString();
    }

    public static boolean checkHeader(byte[] data, byte[] header) {
        if (data.length < header.length) {
            return false;
        }
        for (int i = 0; i < header.length; i++) {
            if (data[i] != header[i]) {
                return false;
            }
        }
        return true;
    }
}
