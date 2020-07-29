package com.sparkle.roam.Print.view.edit;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;

import com.example.printlibrary.utils.DimensUtil;
import com.sparkle.roam.Fragments.GenerateCodeDialog;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.Print.view.customView.AddIconImageView;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/10.
 */

public class EditorMethod implements View.OnClickListener{

    private static final String TAG = "EditorMethod";
    private EditActivity mEditActivity;
    private DragView mDragView;

    private int type;
    private EditText mInputEt;
    private int mHeight;

    public EditorMethod(EditActivity activity, DragView dragView, EditText inputEt){
        mEditActivity = activity;
        mDragView = dragView;
        mInputEt = inputEt;

        initView();
        initDefaultData();
    }

    private void initDefaultData(){

        mDragView.post(new Runnable() {
            @Override
            public void run() {
                mHeight = mDragView.getHeight();
            }
        });
    }

    private void initView() {
//        mEditActivity.findViewById(R.id.input_cancel_button).setOnClickListener(this);
//        mEditActivity.findViewById(R.id.input_finish_button).setOnClickListener(this);
    }

    /**添加图片*/
    public void addImage(Bitmap bitmap, String path){
        type = 4;
        AddIconImageView imageView = new AddIconImageView(mEditActivity);
        imageView.setImageBitmap(bitmap);
        imageView.setIconPath(path);

        PrintParamsBean bean = new PrintParamsBean();
        bean.setLeft(DimensUtil.dpToPixels(mEditActivity,25));
        bean.setTop(DimensUtil.dpToPixels(mEditActivity,25));
        bean.setRight(DimensUtil.dpToPixels(mEditActivity,400));
        bean.setBottom(DimensUtil.dpToPixels(mEditActivity,600));
        bean.setType(type);

        mDragView.addDragView(imageView, bean, false,false);
        showButton();
    }


    public void showButton(){
    }

    public void showInput(){
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.input_cancel_button://取消输入
                break;
            case R.id.input_finish_button://输入完成
                break;
        }
    }
}
