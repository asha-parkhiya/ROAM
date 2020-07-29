package com.sparkle.roam.Print.view.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * Created by Administrator on 2017/11/30.
 */

public class AddIconImageView extends ImageView {

    private String mPath = null;
    private int degrees = 0;

    public AddIconImageView(Context context) {
        super(context);
    }

    public AddIconImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AddIconImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIconPath(String path){
        mPath = path;
    }

    public String getIconPath(){
        return mPath;
    }

    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }
}
