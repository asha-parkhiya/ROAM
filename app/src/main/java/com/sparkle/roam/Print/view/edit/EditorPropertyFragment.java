package com.sparkle.roam.Print.view.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printlibrary.base.BaseFragment;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.Print.view.customView.MoveLayout;
import com.sparkle.roam.Print.view.dialog.DialogSetInfo;
import com.sparkle.roam.R;

/**
 * Created by jc on 2017/11/10.
 */

public class EditorPropertyFragment extends BaseFragment  implements DragView.IMoveLayoutInfo,
        DialogSetInfo.EditDialogSetParams {

    private static final String TAG = "EditorPropertyFragment";

    private DragView mDragView;
    private DialogSetInfo mDialogSetInfo;

    private int currentType = -2;

    private int type = -1;
    public PropertyNoSelectView mNoSelectView;

    private RecyclerView mRecyclerView;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.layout_editor_property,null);
//        init(view);
//        return view;
//    }

    public void setDragView(DragView dragView){
        mDragView = dragView;
    }

    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(getUserVisibleHint()) {
            isVisible = true;
            getLayoutType(null, mDragView.getMoveLayoutId(), mDragView.getType());
        } else {
            isVisible = false;
        }
    }

    @Override
    public void initView() {
//        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);

        mDialogSetInfo = new DialogSetInfo(getActivity());
        mDialogSetInfo.setEditDialogSetParams(this);

        mNoSelectView = new PropertyNoSelectView(this, mDragView, mRecyclerView);
    }

    @Override
    public void initData() {

    }

    @Override
    public void getLayoutType(MoveLayout moveLayout, int identity, int type) {
        this.type = type;
        if(mNoSelectView != null) {

            mNoSelectView.init(moveLayout, type);

            currentType = type;
        }
    }

    @Override
    public void setParams(DialogSetInfo dialog, int type, String value) {
        dialog.dismiss();
    }

    @Override
    public void cancel(DialogSetInfo dialog, int type, String value) {

    }
    public interface SetEditParams{
        void onEditParams(int type, String value);
    }

    @Override
    public void onClick(View view) {
    }
}
