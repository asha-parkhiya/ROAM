package com.sparkle.roam.Print.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.printlibrary.base.BaseActivity;

/**
 *
 * Created by jc on 2017/10/29.
 */

abstract public class AbsBaseActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "AbsBaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        absInit();
    }

    protected void absInit(){

    }
}
