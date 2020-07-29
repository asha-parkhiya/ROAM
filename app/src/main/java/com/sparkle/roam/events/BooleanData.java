package com.sparkle.roam.events;

import com.amazonaws.amplify.generated.graphql.GetPayAccountListQuery;

import java.util.List;

/**
 * Created by josejuansanchez on 04/10/16.
 */

public class BooleanData {
    public boolean data;

    public BooleanData(boolean action) {
        this.data = action;
    }
}