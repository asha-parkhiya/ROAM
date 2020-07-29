package com.sparkle.roam.Print.view.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.printlibrary.base.BaseDialog;
import com.example.printlibrary.utils.DimensUtil;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/11.
 */

public class DialogSaveInfo extends BaseDialog{

    private EditDialogSaveParams mDialogSetParams;
    private TextView mCancelTv;
    private TextView mEnterTv;
    private int mType;//0为字符串 1为int类型
    private TextView mLeftTitleTv;

    public DialogSaveInfo(Activity context) {
        super(context);
    }

//    public DialogSaveInfo(Activity context) {
//        super(context, R.layout.dialog_save_info);
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

//        mLeftTitleTv = (TextView) findViewById(R.id.dialog_hide_tv);//再想想
//        mCancelTv = (TextView) findViewById(R.id.dialog_no_tv);//不保存
//        mEnterTv = (TextView) findViewById(R.id.dialog_ok_tv);//保存
        mCancelTv.setOnClickListener(this);
        mEnterTv.setOnClickListener(this);
        mLeftTitleTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.dialog_hide_tv:
//                dismiss();
//                break;
//            case R.id.dialog_no_tv:
//              if(mDialogSetParams != null){
//                  mDialogSetParams.setSaveParams(this, 1);
//              }
//                break;
//            case R.id.dialog_ok_tv:
//                if(mDialogSetParams != null){
//                    mDialogSetParams.setSaveParams(this, 2);
//                }
//                break;
        }
    }

    public interface EditDialogSaveParams{
        void setSaveParams(DialogSaveInfo dialog, int type);
    }

    public void setEditDialogSaveParams(EditDialogSaveParams dialogSetParams){
        mDialogSetParams = dialogSetParams;
    }
}
