package com.sparkle.roam.Print.view.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.printlibrary.base.BaseDialog;
import com.example.printlibrary.utils.DimensUtil;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/11.
 */

public class DialogSetInfo extends BaseDialog{

    private EditDialogSetParams mDialogSetParams = null;
    private TextView mCancelTv = null;
    private TextView mEnterTv = null;
    private EditText mInputValueEt = null;
    private int mType = 0;//0为字符串 1为int类型
    private TextView mTitleTv = null;
    private TextView mSubTitleTv = null;
    private TextView mLeftTitleTv = null;

    public DialogSetInfo(Activity context) {
        super(context);
    }

//    public DialogSetInfo(Activity context) {
//        super(context, R.layout.dialog_input_info);
//
//        setCancelable(true);
//        setCanceledOnTouchOutside(true);
//
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.width = DimensUtil.dpToPixels(context, 340);
//        //params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        getWindow().setGravity(Gravity.CENTER);
//        getWindow().setAttributes(params);
//
//        init();
//    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void initView() {
        super.initView();

//        mTitleTv = (TextView) findViewById(R.id.dialog_screen_type_title_tv);
//        mSubTitleTv = (TextView) findViewById(R.id.dialog_sub_tv);
//        mLeftTitleTv = (TextView) findViewById(R.id.iv_value_hint);
//        mInputValueEt = (EditText) findViewById(R.id.iv_value_edit);
//        mCancelTv = (TextView) findViewById(R.id.dialog_cancel_tv);
//        mEnterTv = (TextView) findViewById(R.id.dialog_confirm_tv);
        mCancelTv.setOnClickListener(this);
        mEnterTv.setOnClickListener(this);
    }

    public void setEditDialogType(int type){
        mType = type;
        if(type == 0){
            mInputValueEt.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        }else if(type == 1 || type == 2 || type == 3){
            mInputValueEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        }
    }

    public void setTitleValue(String value){
        mTitleTv.setText(value);
    }

    public void setSubTitleValue(String value){
        mSubTitleTv.setText(value);
    }

    public void setLeftTitleValue(String value){
        mLeftTitleTv.setText(value);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.dialog_cancel_tv:
//                dismiss();
//                mInputValueEt.setText("");
//                break;
//            case R.id.dialog_confirm_tv:
//                if(mDialogSetParams != null){
//                    mDialogSetParams.setParams(this, mType, mInputValueEt.getText().toString());
//                }
//                mInputValueEt.setText("");
//                break;
        }
    }

    public interface EditDialogSetParams{
        void setParams(DialogSetInfo dialog, int type, String value);
        void cancel(DialogSetInfo dialog, int type, String value);
    }

    public void setEditDialogSetParams(EditDialogSetParams dialogSetParams){
        mDialogSetParams = dialogSetParams;
    }
}
