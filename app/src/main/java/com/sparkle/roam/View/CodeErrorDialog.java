package com.sparkle.roam.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.MyPref;

public class CodeErrorDialog extends Dialog implements View.OnClickListener {
    private MyPref myPref;

    private OnLogout onLogout;

    private TextView tv_message;
    private Button btn_ok;
    private String message;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (message.equals("Your token is expired please login again.")) {
                    if (onLogout != null)
                        onLogout.OnLogout(false);
                    else
                        dismiss();
                }
                else if (message.equals("Code successfully save.")) {
                    if (onLogout != null)
                        onLogout.OnLogout(true);
                    dismiss();
                }
                else if (message.equals("Your card is not belongs from this account please write data first into card.")) {
                    if (onLogout != null)
                        onLogout.OnLogout(true);
                    dismiss();
                }
                else
                    dismiss();
                break;
        }
    }

    public CodeErrorDialog(Context context, String message, OnLogout onLogout) {
        super(context);
        this.message = message;
        this.onLogout = onLogout;
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

    public interface OnLogout {
        void OnLogout(boolean msg);
    }
}
