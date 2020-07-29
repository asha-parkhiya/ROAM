
package com.sparkle.roam.Displaymodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CodeFormat {

    @SerializedName("assignmentID")
    @Expose
    private Integer assignmentID;
    @SerializedName("assignedCodes")
    @Expose
    private List<AssignedCode> assignedCodes = null;
    @SerializedName("assignmentDate")
    @Expose
    private String assignmentDate;
    @SerializedName("assignmentDays")
    @Expose
    private Integer assignmentDays;

    public Integer getAssignmentID() {
        return assignmentID;
    }

    public void setAssignmentID(Integer assignmentID) {
        this.assignmentID = assignmentID;
    }

    public List<AssignedCode> getAssignedCodes() {
        return assignedCodes;
    }

    public void setAssignedCodes(List<AssignedCode> assignedCodes) {
        this.assignedCodes = assignedCodes;
    }

    public String getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(String assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Integer getAssignmentDays() {
        return assignmentDays;
    }

    public void setAssignmentDays(Integer assignmentDays) {
        this.assignmentDays = assignmentDays;
    }

}
