package com.sparkle.roam.Print.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.printlibrary.utils.DimensUtil;
import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.example.printlibrary.utils.StringUtils;
import com.sparkle.roam.Print.bean.PrintParamsBean;

/**
 * Created by jc on 2017/11/6.
 */

public class UIHelp {

    private static final String TAG = "UIHelp";
    public static final String PRINTOR = "printName";

    public static double mmToPointWidth(int mm){
        return mm * 7.8;
    }

    public static double mmToPointHeight(int mm){
        return mm * 7.8;
    }

    public static void setWidgetRefresh(Context context, View dragLayoutFl, PrintParamsBean bean){
        int disPlayWidth = DimensUtil.getDisplayWidth(context);//屏幕的宽度
        int displayHeight = DimensUtil.getDisplayHeight(context)/2;
        int dragViewWidth = disPlayWidth;//dragView宽度
        int dragViewHeight = (disPlayWidth/4)*3;//dragView高度

        int widthUnit = disPlayWidth / 40;
        int heighthUnit = dragViewHeight / 30;

        float currentWidth = bean.getCurrentWidth();
        float currentHeight = bean.getCurrentHeight();
        float width = -1;//换算后的宽度
        float height = -1;//换算后的高度

        if(currentWidth == 0){
            currentWidth = 40;
        }

        if(currentHeight == 0){
            currentHeight = 30;
        }

        if(currentWidth > currentHeight){
            width = currentWidth * widthUnit;

            height = width * (currentHeight/currentWidth);
        }else if(currentHeight > currentWidth){
            height = currentHeight * heighthUnit;
            width = height * (currentWidth/currentHeight);
        }else if(currentHeight == currentWidth){
            height = currentHeight * heighthUnit;
            width = currentWidth * widthUnit;
        }

        if(width > dragViewWidth){
            width = dragViewWidth;
        }

        if(height > dragViewHeight){
            height = dragViewHeight;
        }
        if(width <= 0){
            width = DimensUtil.dpToPixels(context, 40);
        }
        if(height <= 0){
            height = DimensUtil.dpToPixels(context, 30);
        }
        if(width > height){
                height = dragViewWidth*(currentHeight/currentWidth);
                width = dragViewWidth;
        }else if(height > width){
            width = displayHeight*(currentWidth/currentHeight);
            height = displayHeight;
        }else if(width == height){
            width = currentWidth * widthUnit;
            height = width;
        }

        if(height > dragViewHeight){
            height = displayHeight;
        }

        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams((int)width, (int)height);
        dlp.gravity = Gravity.CENTER;
        dragLayoutFl.setLayoutParams(dlp);
    }

    // 字符串转化为十六进制字节数组
    public static byte[] convert2HexArray(String apdu) {
        int len = apdu.length() / 2;
        char[] chars = apdu.toCharArray();
        String[] hexes = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i = i + 2, j++) {
            hexes[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexes[j], 16);
        }
        return bytes;
    }

    public static boolean redStringValue(Context context, String value, byte[] byteValue, int read){
        byte[] bytes = PrintUtil.redByteValue(byteValue, read);
        if(bytes.length == 0 || bytes[0] == -1){
            return false;
        }
        String str = StringUtils.byte2hex(bytes);
        boolean equals = value.equals(str);
        SharedPreferencesUtils.putBooleanPreferences(context,UIHelp.PRINTOR,equals);
        return equals;
    }
}
