package com.sparkle.roam.Model;

public class PPID_CODE {

    String PPID,CODE;

    public PPID_CODE() {
    }

    public PPID_CODE(String PPID, String CODE) {
        this.PPID = PPID;
        this.CODE = CODE;
    }

    public String getPPID() {
        return PPID;
    }

    public void setPPID(String PPID) {
        this.PPID = PPID;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }
}
