package com.sparkle.roam.Print.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/11/14.
 */

public class BluetoothInfoBean implements Serializable{
    private String name;
    private String address;
    private int result;
    private int read;
    private byte[] buffer;

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BluetoothInfoBean{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", result=" + result +
                ", read=" + read +
                ", buffer=" + Arrays.toString(buffer) +
                '}';
    }
}
