package com.example.planetbiru.utility;

import com.example.planetbiru.config.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Utility {
    public static boolean isInternalLink(String hostname)
    {
        String[] hosts = Config.getInternalHosts().replace(" ", "").split(",");
        for(int i = 0; i < hosts.length; i++) {
            if(hostname.equalsIgnoreCase(hosts[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get first value of specified header
     * @param headers Array string contains header
     * @param key Header key
     * @return Value of the header
     */
    public static String getFirst(String[] headers, String key)
    {
        int i;
        String value = "";
        String line = "";
        String[] arr;
        String key2 = "";
        for(i = 0; i<headers.length; i++)
        {
            line = headers[i].trim();
            if(line.contains(":"))
            {
                arr = line.split("\\:", 2);
                key2 = arr[0].trim();
                if(key2.compareToIgnoreCase(key) == 0)
                {
                    value = arr[1].trim();
                }
            }
        }
        return value;
    }
    /**
     * Parse query string
     * @param query String contains query
     * @return Map contains the query
     * @throws UnsupportedEncodingException
     */
    public static Map<String, String> parseQuery(String query) throws UnsupportedEncodingException
    {
        Map<String, String> result = new HashMap<>();
        if(query != null)
        {
            if(query.length() > 0)
            {
                String[] args;
                int i;
                String arg = "";
                String[] arr;
                String key = "";
                String value = "";
                if(query.contains("&"))
                {
                    args = query.split("&");
                }
                else
                {
                    args = new String[1];
                    args[0] = query;
                }
                for(i = 0; i<args.length; i++)
                {
                    arg = args[i];
                    if(arg.contains("="))
                    {
                        arr = arg.split("=", 2);
                        key = arr[0];
                        value = Utility.urlDecode(arr[1]);
                        result.put(key, value);
                    }
                }
            }
        }
        return result;
    }
    /**
     * Encode URI
     * @param input Raw URI
     * @return Encoded URI
     * @throws UnsupportedEncodingException
     */
    public static String urlEncode(String input) throws UnsupportedEncodingException
    {
        return java.net.URLEncoder.encode(input, "UTF-8");
    }
    /**
     * Decode URI
     * @param input Encoded URI
     * @return Raw URI
     * @throws UnsupportedEncodingException
     */
    public static String urlDecode(String input) throws UnsupportedEncodingException
    {
        return java.net.URLDecoder.decode(input, "UTF-8");
    }
    /**
     * Escape SQL
     * @param input Raw data to be escaped
     * @return Escaped data
     */
    public static String escapeSQL(String input)
    {
        return Utility.escapeSQL(input, "mysql");
    }
    /**
     * Escape SQL
     * @param input Raw data to be escaped
     * @param databaseType Database type
     * @return Escaped data
     */
    public static String escapeSQL(String input, String databaseType)
    {
        String output = input;
        if(databaseType.equals("mysql"))
        {
            output = output.replace("\\u005c", "\\\\");
            output = output.replace("\\n", "\\\\n");
            output = output.replace("\\r", "\\\\r");
            output = output.replace("\\00", "\\\\0");
            output = output.replace("'", "\\\\'");
            output = output.replace("\"", "\\\\\"");
        }
        if(databaseType.equals("postgresql"))
        {
            output = output.replace("\\u005c", "\\\\");
            output = output.replace("\\00", "\\\\0");
            output = output.replace("'", "''");
            output = output.replace("\"", "\\\"");
        }
        return output;
    }
    /**
     * Create header length
     * @param messageLength Message length (in byte)
     * @param headerLength Header length (in byte)
     * @param direction Header direction (true = little endian, false = big endian)
     * @return Byte represent length of message
     */
    @SuppressWarnings("unused")
    public static byte[] createHeaderLength(long messageLength, int headerLength, boolean direction)
    {
        String b;
        String s = "";
        int i;
        long j = 0;
        long k = 0;
        byte[] header = new byte[headerLength];
        if(direction)
        {
            // Little Endian
            j = messageLength;
            for(i = 0; i<headerLength; i++)
            {
                k = j % 256;
                header[i] = (byte) k;
                j = j / 256;
            }
        }
        else
        {
            // Big Endian
            j = messageLength;
            for(i = 0; i<headerLength; i++)
            {
                k = j % 256;
                header[headerLength-i-1] = (byte) k;
                j = j / 256;
            }
        }
        return header;
    }
    /**
     * Convert integer to byte array
     * @param i Integer value
     * @return Byte array
     */
    public static byte[] integerToByteArray(int i)
    {
        byte b[] = new byte[4];
        ByteBuffer buf = ByteBuffer.wrap(b);
        buf.putInt(i);
        return b;
    }
    /**
     * Convert byte array to integer
     * @param b Byte array
     * @return Integer value
     */
    public static int byteArrayToInteger(byte[] b)
    {
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getInt();
    }
    /**
     * Convert array byte to hexadecimal number
     * @param bytes Byte data
     * @return Hexadecimal number
     */
    public static String byteArrayToHexadecimal(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString().trim();
    }
    /**
     * Get actual message length received from member
     * @param message Raw message
     * @param headerLength Header length
     * @param headerDirectionRequest Header direction. true = LSB left MSB right, false = MSB left LSB right
     * @return Actual message length
     */
    public static long getLength(byte[] message, int headerLength, boolean headerDirectionRequest)
    {
        int i;
        long result = 0;
        int x = 0;
        if(headerDirectionRequest)
        {
            // Little endian
            for(i = headerLength-1; i >= 0; i--)
            {
                result *= 256;
                x = (byte) message[i];
                if(x < 0)
                {
                    x = x+256;
                }
                if(x > 256)
                {
                    x = x-256;
                }
                result += x;
            }
        }
        else
        {
            // Big endian
            for(i = 0; i < headerLength; i++)
            {
                result *= 256;
                x = (byte) message[i];
                if(x < 0)
                {
                    x = x+256;
                }
                if(x > 256)
                {
                    x = x-256;
                }
                result += x;
            }
        }
        return result;
    }
    /**
     * Concate 2 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @return Concatenated bytes
     */
    public static byte[] byteConcate(byte[] firstByte, byte[] secondByte) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(firstByte);
        outputStream.write(secondByte);
        return outputStream.toByteArray();
    }
    /**
     * Concate 3 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @param thirdByte Third bytes
     * @return Concatenated bytes
     */
    public static byte[] byteConcate(byte[] firstByte, byte[] secondByte, byte[] thirdByte) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(firstByte);
        outputStream.write(secondByte);
        outputStream.write(thirdByte);
        return outputStream.toByteArray();
    }
    /**
     * Concate 3 byte array
     * @param firstByte First bytes
     * @param secondByte Second bytes
     * @param thirdByte Third bytes
     * @param fourthByte Fourth byte
     * @return Concatenated bytes
     */
    public static byte[] byteConcate(byte[] firstByte, byte[] secondByte, byte[] thirdByte, byte[] fourthByte) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(firstByte);
        outputStream.write(secondByte);
        outputStream.write(thirdByte);
        outputStream.write(fourthByte);
        return outputStream.toByteArray();
    }
    /**
     * Generate SHA-256 hash code from a string
     * @param input Input string
     * @return SHA-256 hash code
     * @throws NoSuchAlgorithmException
     */
    public static String sha256(String input) throws IOException, NoSuchAlgorithmException
    {
        if(input == null)
        {
            input = "";
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes());
        return Utility.bytesToHex(encodedHash);
    }
    /**
     * Generate SHA-1 hash code from a string
     * @param input Input string
     * @return SHA-1 hash code
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String input) throws NoSuchAlgorithmException
    {
        if(input == null)
        {
            input = "";
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] encodedHash = digest.digest(input.getBytes());
        return Utility.bytesToHex(encodedHash);
    }
    /**
     * Generate MD5 hash code from a string
     * @param input Input string
     * @return MD5 hash code
     * @throws NoSuchAlgorithmException
     */
    public static String md5(String input) throws IOException, NoSuchAlgorithmException
    {
        if(input.length() == 0)
        {
            return "";
        }
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] encodedHash = digest.digest(input.getBytes());
        return Utility.bytesToHex(encodedHash);
    }
    /**
     * Convert byte to hexadecimal number
     * @param hash Byte to be converted
     * @return String containing hexadecimal number
     */
    public static String bytesToHex(byte[] hash)
    {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++)
        {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            {
                hexString.append(hex);
            }
        }
        return hexString.toString();
    }

    public static long unixTime()
    {
        return System.currentTimeMillis() / 1000L;
    }
}
