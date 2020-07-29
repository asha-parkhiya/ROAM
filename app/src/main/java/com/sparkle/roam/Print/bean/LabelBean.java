package com.sparkle.roam.Print.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by tian on 2018/1/8.
 */

public class LabelBean implements Serializable{
    private String labelName;
    private int printWidth;
    private int printHeight;
    private Bitmap bitmap;

    public String getLabelName() {
        return labelName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "LabelBean{" +
                "labelName='" + labelName + '\'' +
                ", printWidth=" + printWidth +
                ", printHeight=" + printHeight +
                '}';
    }
}
