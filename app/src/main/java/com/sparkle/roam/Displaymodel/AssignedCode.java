
package com.sparkle.roam.Displaymodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssignedCode {

    @SerializedName("hashTop")
    @Expose
    private String hashTop;
    @SerializedName("otpHashFormatted")
    @Expose
    private String otpHashFormatted;

    public String getHashTop() {
        return hashTop;
    }

    public void setHashTop(String hashTop) {
        this.hashTop = hashTop;
    }

    public String getOtpHashFormatted() {
        return otpHashFormatted;
    }

    public void setOtpHashFormatted(String otpHashFormatted) {
        this.otpHashFormatted = otpHashFormatted;
    }

}
