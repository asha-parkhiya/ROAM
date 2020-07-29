package com.sparkle.roam.Print.bean;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;

import java.io.Serializable;

/**
 * @author Tianbf
 * Created by jc on 2017/11/20.
 */

public class PrintParamsBean implements Serializable{

    private int printWidth = 312;//标签宽度 264 384(2寸) 576(3寸)
    private int printHeight = 234;//标签高度 216 600

    private String backModelPath;

    private int printNum = 1;


    private int widgetWidth = 40;
    private int widgetHeight = 30;
    private int currentWidth = 40;
    private int currentHeight = 30;

    /****************************** saveParamsStart ***********************************************/
    private int type;
    private float txtFontSize;
    private String txtContent = "1234567890";
    private int left;
    private int top;
    private int right;
    private int bottom;
    private boolean isTxtAdj = true;
    private String labelName;//标签名称
    private Bitmap bitmap;
    private BarcodeFormat barcodeFormatType = BarcodeFormat.CODE_128;
    private int printDark = 2;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    /****************************** saveParamsEnd ***********************************************/

    public int getPrintNum() {
        return printNum;
    }

    public void setPrintNum(int printNum) {
        this.printNum = printNum;
    }

    public String getBackModelPath() {
        return backModelPath;
    }

    public void setBackModelPath(String backModelPath) {
        this.backModelPath = backModelPath;
    }

    public int getCurrentWidth() {
        return currentWidth;
    }

    public void setCurrentWidth(int currentWidth) {
        this.currentWidth = currentWidth;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    public void setWidgetWidth(int widgetWidth) {
        this.widgetWidth = widgetWidth;
    }

    public void setWidgetHeight(int widgetHeight) {
        this.widgetHeight = widgetHeight;
    }

    public int getPrintHeight() {
        return printHeight;
    }

    public void setPrintHeight(int printHeight) {
        this.printHeight = printHeight;
    }

    public int getPrintWidth() {
        return printWidth;
    }

    public void setPrintWidth(int printWidth) {
        this.printWidth = printWidth;
    }

    @Override
    public String toString() {
        return "PrintParamsBean{" +
                "printWidth=" + printWidth +
                ", printHeight=" + printHeight +
                ", backModelPath='" + backModelPath + '\'' +
                ", printNum=" + printNum +
                ", widgetWidth=" + widgetWidth +
                ", widgetHeight=" + widgetHeight +
                ", currentWidth=" + currentWidth +
                ", currentHeight=" + currentHeight +
                ", type=" + type +
                ", txtFontSize=" + txtFontSize +
                ", txtContent='" + txtContent + '\'' +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", isTxtAdj=" + isTxtAdj +
                ", labelName='" + labelName + '\'' +
                ", bitmap=" + bitmap +
                ", barcodeFormatType=" + barcodeFormatType +
                ", printSpeed=" + printDark +
                '}';
    }
}
