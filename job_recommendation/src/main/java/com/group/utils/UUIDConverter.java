package com.group.utils;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @Author: mfz
 * @Date: 2024/04/07/0:26
 * @Description:
 */
@Component
public class UUIDConverter {
    public int convertToInt(UUID u) {
        long mostsigBits  = u.getMostSignificantBits();
        long leastSigBits = u.getLeastSignificantBits();

        ByteBuffer b = ByteBuffer.allocate(16);
        b.putLong(mostsigBits);
        b.putLong(leastSigBits);

        byte[] byteArray = b.array();
        int result = 0;
        for(int i = 0;i<byteArray.length;i++) {
            result = (result << 8) | (byteArray[i] & 0xff);
        }
        return result<0?(-result):result;
    }
}
