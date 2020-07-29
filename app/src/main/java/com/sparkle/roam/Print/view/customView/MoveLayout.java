package com.sparkle.roam.Print.view.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

/**
 * Created by Robert on 2017/6/20.
 */

public class MoveLayout extends RelativeLayout implements View.OnTouchListener {

    private static final String TAG = "MoveLayout";

    private Context mContext;

    /**
     * 标示此类的每个实例的id
     */
    private int identity = 0;


    private int type;

    private int isEdit;

    public void set_id(long _id) {

    }

    public void setEdit(int edit) {
        isEdit = edit;
    }

    public void setCurrentView(View currentView) {

    }


    public MoveLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MoveLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MoveLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();

    }

    private void init() {
        setOnTouchListener(this);
    }

    private float downX, downY;
    private float moveX, moveY;

    //拖动事件处理
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();

        if (isEdit == 1) {

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mListener != null) {
                        mListener.onMoveLayoutID(this, identity, type);
                    }
                    downX = event.getRawX();
                    downY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = event.getRawX();
                    moveY = event.getRawY();

                    this.setX(getX() + (moveX - downX));
                    this.setY(getY() + (moveY - downY));
                    downX = moveX;
                    downY = moveY;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //delete listener
    private DeleteMoveLayout mListener = null;

    public interface DeleteMoveLayout {
        void onMoveLayoutID(MoveLayout moveLayout, int identity, int type);
    }

}
