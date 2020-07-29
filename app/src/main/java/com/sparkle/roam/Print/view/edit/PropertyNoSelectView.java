package com.sparkle.roam.Print.view.edit;

import androidx.recyclerview.widget.RecyclerView;

import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.Print.view.customView.MoveLayout;


/**
 * Created by Administrator on 2017/12/13.
 */

public class PropertyNoSelectView {

    private static final String TAG = "PropertyNoSelectView";

    private EditorPropertyFragment mFragment;
    private RecyclerView mRecyclerView;
    private String iconPathName;
    private int mType = -3;
    private DragView mDragView;

    public PropertyNoSelectView(EditorPropertyFragment fragment, DragView dragView, RecyclerView recyclerView){
        mFragment = fragment;
        mRecyclerView = recyclerView;
        mDragView = dragView;

        //init(-1);
    }

    public void setTxtContent(String content, int type){
        refreshTxtData(EditActivity.mPosition, content);
    }

    private void refreshTxtData(int position, String content){
    }

    public void init(MoveLayout moveLayout, int type){

    }

}
