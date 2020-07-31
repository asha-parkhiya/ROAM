
package com.sparkle.roam.Model.Destibutor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryDetail {

    @SerializedName("countryID")
    @Expose
    private Integer countryID;
    @SerializedName("nameEnglish")
    @Expose
    private String nameEnglish;
    @SerializedName("nameNative")
    @Expose
    private String nameNative;
    @SerializedName("countrySymbol")
    @Expose
    private String countrySymbol;
    @SerializedName("flagImage")
    @Expose
    private String flagImage;
    @SerializedName("currencyNameEnglish")
    @Expose
    private String currencyNameEnglish;
    @SerializedName("currencySymbol")
    @Expose
    private String currencySymbol;
    @SerializedName("currencyFXCode")
    @Expose
    private String currencyFXCode;
    @SerializedName("phoneAreaCode")
    @Expose
    private String phoneAreaCode;

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getNameNative() {
        return nameNative;
    }

    public void setNameNative(String nameNative) {
        this.nameNative = nameNative;
    }

    public String getCountrySymbol() {
        return countrySymbol;
    }

    public void setCountrySymbol(String countrySymbol) {
        this.countrySymbol = countrySymbol;
    }

    public String getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(String flagImage) {
        this.flagImage = flagImage;
    }

    public String getCurrencyNameEnglish() {
        return currencyNameEnglish;
    }

    public void setCurrencyNameEnglish(String currencyNameEnglish) {
        this.currencyNameEnglish = currencyNameEnglish;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencyFXCode() {
        return currencyFXCode;
    }

    public void setCurrencyFXCode(String currencyFXCode) {
        this.currencyFXCode = currencyFXCode;
    }

    public String getPhoneAreaCode() {
        return phoneAreaCode;
    }

    public void setPhoneAreaCode(String phoneAreaCode) {
        this.phoneAreaCode = phoneAreaCode;
    }

}
