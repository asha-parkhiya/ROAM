package com.sparkle.roam.Print.view.main;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.sparkle.roam.Print.base.AbsBaseActivity;
import com.sparkle.roam.R;


public class ModelActivity extends AbsBaseActivity {

    private static final String TAG = "ModelActivity";

    private RadioButton mLabelRb;
    private RadioButton mInsertRb;
    private View mPageBar1;
    private View mPageBar2;
    private RecyclerView mModelRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_model);

        init();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }


    @Override
    public void onClick(View view) {
    }
}
