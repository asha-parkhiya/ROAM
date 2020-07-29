
package com.sparkle.roam.Model.SyncData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayAccountData {

    @SerializedName("payAccountID")
    @Expose
    private Integer payAccountID;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("depositDays")
    @Expose
    private Integer depositDays;
    @SerializedName("payoffAmt")
    @Expose
    private Integer payoffAmt;
    @SerializedName("minPayDays")
    @Expose
    private Integer minPayDays;
    @SerializedName("maxPayDays")
    @Expose
    private Integer maxPayDays;
    @SerializedName("schPayDays")
    @Expose
    private Integer schPayDays;
    @SerializedName("userID")
    @Expose
    private Integer userID;
    @SerializedName("distributorID")
    @Expose
    private Integer distributorID;
    @SerializedName("assignedItemsID")
    @Expose
    private Integer assignedItemsID;
    @SerializedName("agentID")
    @Expose
    private Integer agentID;
    @SerializedName("agentAssignmentStatus")
    @Expose
    private String agentAssignmentStatus;
    @SerializedName("agentAssignment")
    @Expose
    private String agentAssignment;
    @SerializedName("initialCreditDays")
    @Expose
    private Integer initialCreditDays;
    @SerializedName("receivedPayAmt")
    @Expose
    private Integer receivedPayAmt;
    @SerializedName("durationDays")
    @Expose
    private Integer durationDays;
    @SerializedName("creditDaysIssued")
    @Expose
    private Integer creditDaysIssued;
    @SerializedName("payDaysReceived")
    @Expose
    private Integer payDaysReceived;


    public Integer getPayAccountID() {
        return payAccountID;
    }

    public void setPayAccountID(Integer payAccountID) {
        this.payAccountID = payAccountID;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Integer getDepositDays() {
        return depositDays;
    }

    public void setDepositDays(Integer depositDays) {
        this.depositDays = depositDays;
    }

    public Integer getPayoffAmt() {
        return payoffAmt;
    }

    public void setPayoffAmt(Integer payoffAmt) {
        this.payoffAmt = payoffAmt;
    }

    public Integer getMinPayDays() {
        return minPayDays;
    }

    public void setMinPayDays(Integer minPayDays) {
        this.minPayDays = minPayDays;
    }

    public Integer getMaxPayDays() {
        return maxPayDays;
    }

    public void setMaxPayDays(Integer maxPayDays) {
        this.maxPayDays = maxPayDays;
    }

    public Integer getSchPayDays() {
        return schPayDays;
    }

    public void setSchPayDays(Integer schPayDays) {
        this.schPayDays = schPayDays;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getDistributorID() {
        return distributorID;
    }

    public void setDistributorID(Integer distributorID) {
        this.distributorID = distributorID;
    }

    public Integer getAssignedItemsID() {
        return assignedItemsID;
    }

    public void setAssignedItemsID(Integer assignedItemsID) {
        this.assignedItemsID = assignedItemsID;
    }

    public Integer getAgentID() {
        return agentID;
    }

    public void setAgentID(Integer agentID) {
        this.agentID = agentID;
    }

    public String getAgentAssignmentStatus() {
        return agentAssignmentStatus;
    }

    public void setAgentAssignmentStatus(String agentAssignmentStatus) {
        this.agentAssignmentStatus = agentAssignmentStatus;
    }

    public String getAgentAssignment() {
        return agentAssignment;
    }

    public void setAgentAssignment(String agentAssignment) {
        this.agentAssignment = agentAssignment;
    }

    public Integer getInitialCreditDays() {
        return initialCreditDays;
    }

    public void setInitialCreditDays(Integer initialCreditDays) {
        this.initialCreditDays = initialCreditDays;
    }

    public Integer getReceivedPayAmt() {
        return receivedPayAmt;
    }

    public void setReceivedPayAmt(Integer receivedPayAmt) {
        this.receivedPayAmt = receivedPayAmt;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getCreditDaysIssued() {
        return creditDaysIssued;
    }

    public void setCreditDaysIssued(Integer creditDaysIssued) {
        this.creditDaysIssued = creditDaysIssued;
    }

    public Integer getPayDaysReceived() {
        return payDaysReceived;
    }

    public void setPayDaysReceived(Integer payDaysReceived) {
        this.payDaysReceived = payDaysReceived;
    }

}
