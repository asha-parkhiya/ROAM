package com.sparkle.roam.Print.view.dialog;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.printlibrary.base.BaseDialog;
import com.example.printlibrary.customView.TabButton;
import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/3.
 *
 */

public class DialogBottomEditor extends BaseDialog{

    private TabButton left0;
    private TabButton center90;
    private TabButton center180;
    private TabButton right270;
    private TabButton mContinuousBt;
    private TabButton mHoleBt;
    private TabButton mIntervalBt;
    private TabButton mBlockBt;
    private DialogBottomClick click;
    private TextView mTicketNameTv;
    private TextView mTicketWidthTv;
    private TextView mTextViewicketHeightTv;
    private Activity mContext;

    public DialogBottomEditor(Activity context) {
        super(context);
    }

//    public DialogBottomEditor(Activity context) {
//        super(context, R.layout.dialog_bottom_editer);
//
//        mContext = context;
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

//        mTicketNameTv = (TextView) findViewById(R.id.ticket_name_tv);
//        mTicketWidthTv = (TextView) findViewById(R.id.dialog_width_ticket);
//        mTextViewicketHeightTv = (TextView) findViewById(R.id.dialog_width_height);
//
//        setDirection();
//        setPaper();
//
//        int printNum = SharedPreferencesUtils.getIntPreferences(mContext,"printNum",0);
//        printNum ++;
//        setTicketName(mContext.getString(R.string.DzLabelEditor_default_label_prefix)+printNum);
    }

    @Override
    public void initListener() {
        super.initListener();

//        findViewById(R.id.dialog_name_rl).setOnClickListener(this);
//        findViewById(R.id.dialog_width_rl).setOnClickListener(this);
//        findViewById(R.id.dialog_height_rl).setOnClickListener(this);
//        findViewById(R.id.toolbar_func1).setOnClickListener(this);
//        findViewById(R.id.toolbar_func2).setOnClickListener(this);

        mContinuousBt.setOnClickListener(this);
        mHoleBt.setOnClickListener(this);
        mIntervalBt.setOnClickListener(this);
        mBlockBt.setOnClickListener(this);

        left0.setOnClickListener(this);
        center90.setOnClickListener(this);
        center180.setOnClickListener(this);
        right270.setOnClickListener(this);
    }

    private void setPaper() {
//        mContinuousBt = (TabButton) findViewById(R.id.btn_paper_continuous);
//        mHoleBt = (TabButton) findViewById(R.id.btn_paper_hole);
//        mIntervalBt = (TabButton) findViewById(R.id.btn_paper_interval);
//        mBlockBt = (TabButton) findViewById(R.id.btn_paper_block);
//
//        resetPaper(R.id.btn_paper_interval);
    }

    private void setDirection() {
//        left0 = (TabButton) findViewById(R.id.btn_left);
//        center90 = (TabButton) findViewById(R.id.btn_center1);
//        center180 = (TabButton) findViewById(R.id.btn_center2);
//        right270 = (TabButton) findViewById(R.id.btn_right);
//
//        resetState(R.id.btn_left);
    }

    public void setTicketName(String value){
        mTicketNameTv.setText(value);
    }

    public  void setTicketWidth(String value){
        addPraise(mTicketWidthTv, value);
    }

    public  void setTicketHeight(String value){
        addPraise(mTextViewicketHeightTv, value);
    }
    private void addPraise(TextView tv,String num){
//        Drawable img = context.getApplicationContext().getResources().getDrawable(R.drawable.icon_listview_normal_end);
//        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//        img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//        tv.setCompoundDrawables(null, null, img, null); //设置左图标
//        tv.setText(num + mContext.getString(R.string.str_mm));
    }

    @Override
    public void onClick(View view) {
        int type = -1;
        switch (view.getId()) {
//            case R.id.btn_left:
//                type = 0;
//                break;
//            case R.id.btn_center1:
//                type = 0;
//                break;
//            case R.id.btn_center2:
//                type = 0;
//                break;
//            case R.id.btn_right:
//                type = 0;
//                break;
//            case R.id.btn_paper_continuous:
//                type = 1;
//                break;
//            case R.id.btn_paper_hole:
//                type = 1;
//                break;
//            case R.id.btn_paper_interval:
//                type = 1;
//                break;
//            case R.id.btn_paper_block:
//                type = 1;
//                break;
        }

        if(type == 0) {
            resetState(view.getId());
        }else if(type == 1){
            resetPaper(view.getId());
        }else {
            if(click != null){
                click.dialogClick(this,view.getId());
            }
        }
    }

    private void resetPaper(int id){
        // 将三个按钮背景设置为未选中
        mContinuousBt.setSelected(false);
        mHoleBt.setSelected(false);
        mIntervalBt.setSelected(false);
        mBlockBt.setSelected(false);

        // 将点击的按钮背景设置为已选中
        switch (id) {
//            case R.id.btn_paper_continuous:
//                mContinuousBt.setSelected(true);
//                break;
//            case R.id.btn_paper_hole:
//                mHoleBt.setSelected(true);
//                break;
//            case R.id.btn_paper_interval:
//                mIntervalBt.setSelected(true);
//                break;
//            case R.id.btn_paper_block:
//                mBlockBt.setSelected(true);
//                break;
        }

        if(click != null){
            click.dialogClick(this,id);
        }
    }

    private void resetState(int id) {
        // 将三个按钮背景设置为未选中
        left0.setSelected(false);
        center90.setSelected(false);
        center180.setSelected(false);
        right270.setSelected(false);

        // 将点击的按钮背景设置为已选中
//        switch (id) {
//            case R.id.btn_left:
//                left0.setSelected(true);
//                break;
//            case R.id.btn_center1:
//                center90.setSelected(true);
//                break;
//            case R.id.btn_center2:
//                center180.setSelected(true);
//                break;
//            case R.id.btn_right:
//                right270.setSelected(true);
//                break;
//        }
        if(click != null){
            click.dialogClick(this,id);
        }
    }

    public interface DialogBottomClick{
        void dialogClick(DialogBottomEditor dialog, int id);
    }

    public void setDialogBottomClick(DialogBottomClick click){
        this.click = click;
    }
}
