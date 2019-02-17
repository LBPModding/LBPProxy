package com.github.lbpmodding.lbpproxy.utility;

public final class HexUtilities {

    private HexUtilities() {
    }

    public static String bytesToHexString(byte... bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte current : bytes) {
            builder.append(String.format("%02x", current));
        }
        return builder.toString();
    }
}
