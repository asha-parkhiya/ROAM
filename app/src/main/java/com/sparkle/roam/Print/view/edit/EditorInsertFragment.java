package com.sparkle.roam.Print.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.printlibrary.base.BaseFragment;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/10.
 */

public class EditorInsertFragment extends BaseFragment {

    private static final String TAG = "EditorInsertFragment";
    private EditActivity mEditActivity = null;

    public EditorInsertFragment(EditActivity editActivity){
        mEditActivity = editActivity;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.layout_editor_insert,null);
//        init(view);
//        return view;
//    }

    @Override
    public void initView() {
//        findViewById(R.id.tool_action_image).setOnClickListener(mEditActivity.insertOnClick);
//        findViewById(R.id.tool_action_text).setOnClickListener(mEditActivity.insertOnClick);
//        findViewById(R.id.tool_action_barcode).setOnClickListener(mEditActivity.insertOnClick);
//        findViewById(R.id.tool_action_qrcode).setOnClickListener(mEditActivity.insertOnClick);
//        findViewById(R.id.tool_action_cpcl).setOnClickListener(mEditActivity.insertOnClick);
//        findViewById(R.id.tool_action_tspl).setOnClickListener(mEditActivity.insertOnClick);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
    }
}
