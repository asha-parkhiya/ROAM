
package com.sparkle.roam.Model.PayEventSync;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayEventData {

    public PayEventData(Integer payEventID, String payEventDate, Integer payDays, Integer payRecordAmt, String payRecordNotes, Integer payAccountID, String eventType, Object codeDays, Object codeIssued, Object codehashTop) {
        this.payEventID = payEventID;
        this.payEventDate = payEventDate;
        this.payDays = payDays;
        this.payRecordAmt = payRecordAmt;
        this.payRecordNotes = payRecordNotes;
        this.payAccountID = payAccountID;
        this.eventType = eventType;
        this.codeDays = codeDays;
        this.codeIssued = codeIssued;
        this.codehashTop = codehashTop;
    }

    @SerializedName("payEventID")
    @Expose
    private Integer payEventID;
    @SerializedName("payEventDate")
    @Expose
    private String payEventDate;
    @SerializedName("payDays")
    @Expose
    private Integer payDays;
    @SerializedName("payRecordAmt")
    @Expose
    private Integer payRecordAmt;
    @SerializedName("payRecordNotes")
    @Expose
    private String payRecordNotes;
    @SerializedName("payAccountID")
    @Expose
    private Integer payAccountID;
    @SerializedName("eventType")
    @Expose
    private String eventType;
    @SerializedName("codeDays")
    @Expose
    private Object codeDays;
    @SerializedName("codeIssued")
    @Expose
    private Object codeIssued;
    @SerializedName("codehashTop")
    @Expose
    private Object codehashTop;

    public Integer getPayEventID() {
        return payEventID;
    }

    public void setPayEventID(Integer payEventID) {
        this.payEventID = payEventID;
    }

    public String getPayEventDate() {
        return payEventDate;
    }

    public void setPayEventDate(String payEventDate) {
        this.payEventDate = payEventDate;
    }

    public Integer getPayDays() {
        return payDays;
    }

    public void setPayDays(Integer payDays) {
        this.payDays = payDays;
    }

    public Integer getPayRecordAmt() {
        return payRecordAmt;
    }

    public void setPayRecordAmt(Integer payRecordAmt) {
        this.payRecordAmt = payRecordAmt;
    }

    public String getPayRecordNotes() {
        return payRecordNotes;
    }

    public void setPayRecordNotes(String payRecordNotes) {
        this.payRecordNotes = payRecordNotes;
    }

    public Integer getPayAccountID() {
        return payAccountID;
    }

    public void setPayAccountID(Integer payAccountID) {
        this.payAccountID = payAccountID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getCodeDays() {
        return codeDays;
    }

    public void setCodeDays(Object codeDays) {
        this.codeDays = codeDays;
    }

    public Object getCodeIssued() {
        return codeIssued;
    }

    public void setCodeIssued(Object codeIssued) {
        this.codeIssued = codeIssued;
    }

    public Object getCodehashTop() {
        return codehashTop;
    }

    public void setCodehashTop(Object codehashTop) {
        this.codehashTop = codehashTop;
    }

}
