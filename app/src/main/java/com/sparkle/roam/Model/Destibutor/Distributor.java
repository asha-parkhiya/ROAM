
package com.sparkle.roam.Model.Destibutor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distributor {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
