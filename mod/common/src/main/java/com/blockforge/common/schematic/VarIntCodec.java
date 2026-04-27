package com.blockforge.common.schematic;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class VarIntCodec {
    private VarIntCodec() {
    }

    public static byte[] encode(List<Integer> values) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (int value : values) {
            int current = value;
            do {
                int b = current & 0x7F;
                current >>>= 7;
                if (current != 0) {
                    b |= 0x80;
                }
                output.write(b);
            } while (current != 0);
        }
        return output.toByteArray();
    }

    public static List<Integer> decode(byte[] bytes) {
        List<Integer> values = new ArrayList<>();
        int value = 0;
        int shift = 0;
        for (byte raw : bytes) {
            int b = raw & 0xFF;
            value |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                values.add(value);
                value = 0;
                shift = 0;
            } else {
                shift += 7;
                if (shift > 28) {
                    throw new IllegalArgumentException("varint too long");
                }
            }
        }
        if (shift != 0) {
            throw new IllegalArgumentException("truncated varint data");
        }
        return values;
    }
}
