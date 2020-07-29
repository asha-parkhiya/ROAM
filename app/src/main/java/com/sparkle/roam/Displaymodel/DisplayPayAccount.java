package com.sparkle.roam.Displaymodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DisplayPayAccount {

    public DisplayPayAccount( String firstName,String lastName, String productItemPAYGSN, Integer payAccountID, String startDate, Integer depositDays, Integer payoffAmt, Integer minPayDays, Integer maxPayDays, Integer schPayDays, Integer userID, Integer distributorID, Integer assignedItemsID, Integer agentID, String agentAssignmentStatus, String agentAssignment, Integer initialCreditDays, Integer receivedPayAmt, Integer durationDays, Integer creditDaysIssued, Integer payDaysReceived, String firmwareVersion) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.productItemPAYGSN = productItemPAYGSN;
        this.payAccountID = payAccountID;
        this.startDate = startDate;
        this.depositDays = depositDays;
        this.payoffAmt = payoffAmt;
        this.minPayDays = minPayDays;
        this.maxPayDays = maxPayDays;
        this.schPayDays = schPayDays;
        this.userID = userID;
        this.distributorID = distributorID;
        this.assignedItemsID = assignedItemsID;
        this.agentID = agentID;
        this.agentAssignmentStatus = agentAssignmentStatus;
        this.agentAssignment = agentAssignment;
        this.initialCreditDays = initialCreditDays;
        this.receivedPayAmt = receivedPayAmt;
        this.durationDays = durationDays;
        this.creditDaysIssued = creditDaysIssued;
        this.payDaysReceived = payDaysReceived;
        this.firmwareVersion = firmwareVersion;
    }

    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("productItemPAYG_SN")
    @Expose
    private String productItemPAYGSN;

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
    @SerializedName("firmwareVersion")
    @Expose
    private String firmwareVersion;

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProductItemPAYGSN() {
        return productItemPAYGSN;
    }

    public void setProductItemPAYGSN(String productItemPAYGSN) {
        this.productItemPAYGSN = productItemPAYGSN;
    }
}
