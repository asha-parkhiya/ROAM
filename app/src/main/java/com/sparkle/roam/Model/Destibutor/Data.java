
package com.sparkle.roam.Model.Destibutor;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("getdistributorProfileForAgent")
    @Expose
    private List<GetdistributorProfileForAgent> getdistributorProfileForAgent = null;

    public List<GetdistributorProfileForAgent> getGetdistributorProfileForAgent() {
        return getdistributorProfileForAgent;
    }

    public void setGetdistributorProfileForAgent(List<GetdistributorProfileForAgent> getdistributorProfileForAgent) {
        this.getdistributorProfileForAgent = getdistributorProfileForAgent;
    }

}
