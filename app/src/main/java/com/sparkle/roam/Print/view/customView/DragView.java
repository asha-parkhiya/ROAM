package com.sparkle.roam.Print.view.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.printlibrary.utils.DimensUtil;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.R;

/**
 * Created by Robert on 2017/6/21.
 */

public class DragView extends RelativeLayout implements MoveLayout.DeleteMoveLayout {

    private static final String TAG = "DragView";

    private Context mContext;
    private int mType = -1;
    /**
     * the identity of the moveLayout
     */
    private int mLocalIdentity = 0;

    /*
    * 拖拽框最小尺寸
    */
    private int mMinHeight = 90;
    private int mMinWidth = 177;


    private int isEditor;
    private long _id;

    public long get_id() {
        return _id;
    }

    public DragView(Context context) {
        super(context);
        init(context, this);
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, this);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, this);
    }

    private void init(Context c, DragView thisp) {
        mContext = c;
    }

    public void setEditor(int editor) {
        isEditor = editor;
    }

    public void setBackModel(boolean isBackModel){
    }

    public void clearView(){
        removeAllViews();
    }

    public void setBackModelBitmap(Context c, String path, Bitmap bmp) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return super.onTouchEvent(event);
    }


    @Override
    public void onMoveLayoutID(MoveLayout moveLayout, int identity, int type) {
        invalidate();
    }


    public void addDragView(View selfView, PrintParamsBean bean, boolean isFixedSize, boolean whitebg) {

        mMinWidth = DimensUtil.dpToPixels(mContext,37);
        mMinHeight = DimensUtil.dpToPixels(mContext,37);

        addDragView(selfView, bean, isFixedSize, whitebg, mMinWidth, mMinHeight);
    }

    /**
     * 每个moveLayout都可以拥有自己的最小尺寸
     */
    public void addDragView(View selfView, PrintParamsBean bean, boolean isFixedSize, boolean whiteBg, int minWidth, int minHeight) {
        //  Log.e(TAG, "addDragView: height="+getHeight() +"   width+"+ getWidth() );
        addViewDone(selfView, bean, isFixedSize, minWidth, minHeight);
    }

    private void addViewDone(View selfView, PrintParamsBean bean, boolean isFixedSize, int minWidth1, int minHeight1) {
        MoveLayout moveLayout = new MoveLayout(mContext);
        moveLayout.setClickable(true);
        moveLayout.setType(bean.getType());
        moveLayout.setCurrentView(selfView);
        moveLayout.setEdit(isEditor);
        moveLayout.set_id(get_id());

//        LayoutParams lv = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        moveLayout.addView(selfView, lv);
//
//        moveLayout.setIdentity(mLocalIdentity++);
        addView(moveLayout);
        RelativeLayout editText = selfView.findViewById(R.id.rl);
        TextView text2 = editText.getRootView().findViewById(R.id.text2);
        TextView text4 = editText.getRootView().findViewById(R.id.text4);
        System.out.println("---------helllooooooo.o-------"+text2.getText().toString());
        System.out.println("---------helllooooooo.o-------"+text4.getText().toString());

    }

    public void startPrint() {
        invalidate();
    }

    public int getMoveLayoutId() {
        return mLocalIdentity;
    }

    public interface IMoveLayoutInfo {
        void getLayoutType(MoveLayout moveLayout, int identity, int type);
    }

    public int getType() {
        return mType;
    }

}
