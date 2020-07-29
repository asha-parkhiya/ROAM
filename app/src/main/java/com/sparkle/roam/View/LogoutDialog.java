package com.sparkle.roam.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.MyPref;

public class LogoutDialog extends Dialog implements View.OnClickListener {

    OnOkClick onOkClick;
    public interface OnOkClick{
        void onOkClick();
    }
    MyPref myPref;
    private AWSAppSyncClient mAWSAppSyncClient;
    Button btn_cancel,btn_ok;
    Context context;
    public LogoutDialog(Context context, OnOkClick onOkClick) {
        super(context);
        this.context = context;
        this.onOkClick = onOkClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_logout);
        Window w = getWindow();
        setCancelable(false);
        myPref = new MyPref(getContext());
        w.getAttributes().windowAnimations = R.style.DialogAnimation;
        w.setBackgroundDrawableResource(android.R.color.transparent);

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
//                ((NavigationActivity)getOwnerActivity()).setLogout();
                onOkClick.onOkClick();
                dismiss();

                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }

    }

}
