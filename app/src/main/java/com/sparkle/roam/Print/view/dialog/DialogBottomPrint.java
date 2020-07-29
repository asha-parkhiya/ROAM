package com.sparkle.roam.Print.view.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.printlibrary.base.BaseDialog;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/3.
 *
 */

public class DialogBottomPrint extends BaseDialog{
    private TextView mTypeTv;
    private DialogPrintOnClick mClick;
    private TextView mPrintNumTv;
    private TextView mCopiesTv;

    public DialogBottomPrint(Activity context) {
        super(context);
    }

//    public DialogBottomPrint(Activity context) {
//        super(context, R.layout.dialog_bottm_print);
//
//        setCancelable(true);
//        setCanceledOnTouchOutside(true);
//
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        //params.width = DimensUtil.dpToPixels(context, 255);
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        getWindow().setGravity(Gravity.BOTTOM);
//        getWindow().setAttributes(params);
//
//        init();
//    }

    @Override
    public void initView() {
        super.initView();

    }

    @Override
    public void initListener() {
        super.initListener();

//        findViewById(R.id.dialog_print_add_tv).setOnClickListener(this);
//        findViewById(R.id.dialog_print_type_rl).setOnClickListener(this);
//        findViewById(R.id.dialog_print_reduce_tv).setOnClickListener(this);
//        findViewById(R.id.dialog_page_reduce_tv).setOnClickListener(this);
//        findViewById(R.id.dialog_page_add_tv).setOnClickListener(this);
//        findViewById(R.id.dialog_copies_reduce_tv).setOnClickListener(this);
//        findViewById(R.id.dialog_copies_add_tv).setOnClickListener(this);
//        findViewById(R.id.dialog_copies_reduce_tv).setOnClickListener(this);
//        findViewById(R.id.toolbar_func1).setOnClickListener(this);
//        findViewById(R.id.toolbar_func2).setOnClickListener(this);

//        //显示是否连接打印机
//        mTypeTv = (TextView) findViewById(R.id.dialog_print_type_tv);
//        //显示打印浓度
//        mPrintNumTv = (TextView) findViewById(R.id.dialog_print_concentration_tv);
//        //TextView pageTv = (TextView) findViewById(R.id.dialog_page_show_tv);//显示打印页数
//        //显示打印份数
//        mCopiesTv = (TextView) findViewById(R.id.dialog_copies_show_tv);
//        //TextView valueTv = (TextView) findViewById(R.id.listitem_value);//显示分页
//        TextView nameTv = (TextView) findViewById(R.id.listitem_name);
//        nameTv.setText(R.string.print_collateCopies);
//        TextView name2Tv = (TextView) findViewById(R.id.toolbar_func2_name);
//        name2Tv.setText(R.string.str_print);
//        //ImageView switchIv = (ImageView) findViewById(R.id.listitem_switcher);
//        ImageView iconIv = (ImageView) findViewById(R.id.toolbar_func2_icon);
//        iconIv.setImageResource(R.drawable.toolbar_icon_print);
    }

    public void setTypeTv(String name){
        mTypeTv.setText(name);
    }

    public String getPrintConnectionType(){
        return mTypeTv.getText().toString();
    }

    public void setPrintNumTv(int num){
        mPrintNumTv.setText(num+"");
    }
    public void setCopiesNumTv(int num){
        mCopiesTv.setText(num+"");
    }


    @Override
    public void onClick(View view) {
        if(mClick != null){
            mClick.onDialogClick(view.getId());
        }
    }

    public interface DialogPrintOnClick{
        void onDialogClick(int item);
    }

    public void setDialogPrintOnClick(DialogPrintOnClick click){
        mClick = click;
    }
}
