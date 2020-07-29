package com.sparkle.roam.Print.view.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.printlibrary.BuildConfig;
import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.sparkle.roam.Print.base.AbsBaseActivity;
import com.sparkle.roam.R;

public class SettingPrinterActivity extends AbsBaseActivity {

    private CheckedTextView mLeftCtv;
    private CheckedTextView mCenterCtv;
    private CheckedTextView mRightCtv;
    private int check = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_printer_table);

        init();
    }

    @Override
    public void initView() {
        initTitle();

//        mLeftCtv = (CheckedTextView) findViewById(R.id.iv_checkable_left);
//        mLeftCtv.setOnClickListener(checkListener);
//        mCenterCtv = (CheckedTextView) findViewById(R.id.iv_checkable_center);
//        mCenterCtv.setOnClickListener(checkListener);
//        mRightCtv = (CheckedTextView) findViewById(R.id.iv_checkable_right);
//        mRightCtv.setOnClickListener(checkListener);

        check = SharedPreferencesUtils.getIntPreferences(this,"checkNum", 1);

        if(check == 0){
            setMyChecked(mLeftCtv);
        }else if(check == 1){
            setMyChecked(mCenterCtv);
        }else if(check == 2){
            setMyChecked(mRightCtv);
        }

        new SettingView(this);


//        TextView tvVer = (TextView) findViewById(R.id.listitem_value);
//        tvVer.setText("v" + BuildConfig.VERSION_NAME);
//
//        TextView tvBuildVer = (TextView) findViewById(R.id.build_vername);
//        tvBuildVer.setText("b" + BuildConfig.BUILD_TIMESTAMP);
    }

    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            setMyChecked(view);
        }
    };

    private void setMyChecked(View view) {

        mLeftCtv.setChecked(false);
        mCenterCtv.setChecked(false);
        mRightCtv.setChecked(false);

        switch (view.getId()){
//            case R.id.iv_checkable_left:
//                check = 0;
//                mLeftCtv.setChecked(true);
//                break;
//            case R.id.iv_checkable_center:
//                check = 1;
//                mCenterCtv.setChecked(true);
//                break;
//            case R.id.iv_checkable_right:
//                check = 2;
//                mRightCtv.setChecked(true);
//                break;
        }

        SharedPreferencesUtils.putIntPreferences(this,"checkNum",check);
    }

    private void initTitle() {
//        findViewById(R.id.title_main_left).setOnClickListener(this);
//        TextView title = (TextView) findViewById(R.id.title_main_name);
//        title.setText(getString(R.string.main_func_setting));
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.title_main_left:
//                finish();
//                break;
        }
    }
}
