
package com.sparkle.roam.Model.Destibutor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetdistributorProfileForAgent {

    @SerializedName("distributorID")
    @Expose
    private Integer distributorID;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("distributorProfileURL")
    @Expose
    private Object distributorProfileURL;
    @SerializedName("distributorAccountNo")
    @Expose
    private Object distributorAccountNo;
    @SerializedName("contactLastName")
    @Expose
    private String contactLastName;
    @SerializedName("contactFirstName")
    @Expose
    private String contactFirstName;
    @SerializedName("contactEmail")
    @Expose
    private String contactEmail;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("addressLine1")
    @Expose
    private String addressLine1;
    @SerializedName("addressLine2")
    @Expose
    private Object addressLine2;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private Object state;
    @SerializedName("postalCode")
    @Expose
    private Object postalCode;
    @SerializedName("country_countryID")
    @Expose
    private Integer countryCountryID;
    @SerializedName("countryDetail")
    @Expose
    private CountryDetail countryDetail;

    public Integer getDistributorID() {
        return distributorID;
    }

    public void setDistributorID(Integer distributorID) {
        this.distributorID = distributorID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Object getDistributorProfileURL() {
        return distributorProfileURL;
    }

    public void setDistributorProfileURL(Object distributorProfileURL) {
        this.distributorProfileURL = distributorProfileURL;
    }

    public Object getDistributorAccountNo() {
        return distributorAccountNo;
    }

    public void setDistributorAccountNo(Object distributorAccountNo) {
        this.distributorAccountNo = distributorAccountNo;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public Object getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(Object addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Object postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getCountryCountryID() {
        return countryCountryID;
    }

    public void setCountryCountryID(Integer countryCountryID) {
        this.countryCountryID = countryCountryID;
    }

    public CountryDetail getCountryDetail() {
        return countryDetail;
    }

    public void setCountryDetail(CountryDetail countryDetail) {
        this.countryDetail = countryDetail;
    }

}
