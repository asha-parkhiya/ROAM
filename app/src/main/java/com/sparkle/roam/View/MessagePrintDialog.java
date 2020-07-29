package com.sparkle.roam.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sparkle.roam.R;
import com.sparkle.roam.Utils.MyPref;

public class MessagePrintDialog extends Dialog implements View.OnClickListener {
    private MyPref myPref;

    private OnOkClose onOkClose;

    private TextView tv_message;
    private Button btn_ok;
    private String message;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (message.equals("Stay connected while syncing with OVES Roam Hub. You credits and user data can be lost if you do not sync with the Hub completely.")) {
                    if (onOkClose != null){
                        onOkClose.OnOkClose(false);
                        dismiss();
                    }
                } else if (message.equals("You have completed sync with server and you can logout safely. You can also login again on this phone, or another phone.")) {
                    if (onOkClose != null){
                        onOkClose.OnOkClose(true);
                        dismiss();
                    }
                } else{
                    onOkClose.OnOkClose(false);
                    dismiss();
                }
                break;
        }
    }

    public MessagePrintDialog(Context context, String message, OnOkClose onLogout) {
        super(context);
        this.message = message;
        this.onOkClose = onLogout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_code_error);
        Window w = getWindow();
        setCancelable(false);
        myPref = new MyPref(getContext());
        w.getAttributes().windowAnimations = R.style.DialogAnimation;
        w.setBackgroundDrawableResource(android.R.color.transparent);
        tv_message = findViewById(R.id.tv_message);

        if (message != null) {
            tv_message.setText(message);
        }

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
    }

    public interface OnOkClose {
        void OnOkClose(boolean msg);
    }
}
