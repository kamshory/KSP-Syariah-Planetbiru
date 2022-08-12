package com.example.planetbiru.utility;


public class TLV {

    /** Subdomain Tag Label*/
    private String tag;

    /** Length of subdomain value*/
    private int length;

    /** Subdomain Value*/
    private String value;

    public TLV(String tag, int length, String value) {
        this.length = length;
        this.tag = tag;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "tag=[" + this.tag + "]," + "length=[" + this.length + "]," + "value=[" + this.value + "]";
    }
}

