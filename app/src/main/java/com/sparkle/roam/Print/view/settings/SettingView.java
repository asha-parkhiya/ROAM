package com.sparkle.roam.Print.view.settings;

import android.view.View;
import android.widget.LinearLayout;

import com.example.printlibrary.utils.SharedPreferencesUtils;
import com.sparkle.roam.R;

/**
 * Created by Administrator on 2018/1/16.
 */

public class SettingView {
    public static final int LANGUAGE_SYS = 0;   // 跟随系统
    public static final int LANGUAGE_CN = 1;    // 中文
    public static final int LANGUAGE_EN = 2;    // 英文
    public static final int LANGUAGE_TW = 3;    // 台湾

    public static final int PrintCmdNormal = 0;
    public static final int PrintCmdLable = 1;
    public static final int PrintCmdLableBuffer = 2;
    private int language = LANGUAGE_SYS;
    private SettingPrinterActivity mActivity;

    public SettingView(SettingPrinterActivity activity){
        mActivity = activity;

        init();
    }

    // 获取语言
    private String getLanguageString(int lang){
        String str = null;
//        if(lang == LANGUAGE_CN){
//            str = mActivity.getString(R.string.DzCommon_language_SIMPLIFIED_CHINESE);
//        }else if(lang == LANGUAGE_EN){
//            str = mActivity.getString(R.string.DzCommon_language_ENGLISH);
//        }else if (lang == LANGUAGE_TW){
//            str = mActivity.getString(R.string.DzCommon_language_TRADITIONAL_CHINESE);
//        }else{
//            str = mActivity.getString(R.string.DzCommon_language_auto);
//        }
        return str;
    }

    // 获取语言
    private int getLanguageString(String str){
        int lang = LANGUAGE_SYS;
//        if (str != null) {
//            if (str.equals(mActivity.getString(R.string.DzCommon_language_SIMPLIFIED_CHINESE))) {
//                lang = LANGUAGE_CN;
//            } else if (str.equals(mActivity.getString(R.string.DzCommon_language_TRADITIONAL_CHINESE))) {
//                lang = LANGUAGE_TW;
//            } else if (str.equals(mActivity.getString(R.string.DzCommon_language_ENGLISH))) {
//                lang = LANGUAGE_EN;
//            }
//        }
        return lang;
    }

    private void init(){
        //  set language
//        LinearLayout ll_lang = (LinearLayout)mActivity.findViewById(R.id.setting_language_ll);
//        ll_lang.setOnClickListener(checkListener);

//        language = SharedPreferencesUtils.getIntPreferences(mActivity,"appLanguage", LANGUAGE_SYS);
    }

    private void softversion_check(){

    }

    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
//                case R.id.setting_language_ll:  // 语言设置
//                    break;
            }
        }
    };
}
