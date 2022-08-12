package com.example.planetbiru.utility;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TLVUtils {

    private static final String TAG = "TLVUtils";

    /**
     * Convert a hexadecimal string to a list of TLV objects
     * @param hexString
     * @return
     */
    public static List<TLV> builderTlvList(String hexString) {
        List<TLV> tlvs = new ArrayList<TLV>();

        int position = 0;
        String sub = hexString;
        while (position < hexString.length() - 3) {

            try {
                sub = hexString.substring(position, position+4);
                String tag = sub.substring(0, 2);
                String len = sub.substring(2, 4);
                int length = Integer.parseInt(len.toLowerCase());
                String value = hexString.substring(position + 4, position + 4 + length);
                Log.d(TAG, sub);
                Log.d(TAG, tag);
                Log.d(TAG, len);
                Log.d(TAG, value);
                tlvs.add(new TLV(tag, length, value));
                position += (length + 4);
            }
            catch (NumberFormatException e)
            {
                break;
            }
        }
        return tlvs;
    }

    /**
     * Convert hexadecimal string to TLV object MAP
     * @param hexString
     * @return
     */
    public static Map<String, TLV> builderTlvMap(String hexString) {

        Map<String, TLV> tlvs = new HashMap<String, TLV>();

        int position = 0;
        while (position != hexString.length()) {

        }
        return tlvs;
    }

}