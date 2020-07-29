package com.sparkle.roam.Print.view.edit;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jc on 2017/11/9.
 */

public class ViewPageView implements EditorPropertyFragment.SetEditParams {

    private EditActivity mEditActivity;
    private final String[] tabTitle = {"Demo"};//标签
    private List<Fragment> mFragmentList = new ArrayList<>();
    private MyPagerAdapter mAdapter;
    private ViewPager mToolVp;
    private RadioButton mInsertRb;
    private View mPageBar1;

    private DragView mDragView;
    private EditText inputEt;

    public ViewPageView(EditActivity activity, DragView dragView, EditText inputEt) {
        mEditActivity = activity;
        mDragView = dragView;
        this.inputEt = inputEt;

        initView();
    }

    private void initView() {
        EditorInsertFragment editorInsertFragment = new EditorInsertFragment(mEditActivity);

        mFragmentList.add(editorInsertFragment);

        mAdapter = new MyPagerAdapter(mEditActivity.getSupportFragmentManager());

        mToolVp = (ViewPager) mEditActivity.findViewById(R.id.tool_zone_pager);

        mToolVp.setAdapter(mAdapter);

        mInsertRb = (RadioButton) mEditActivity.findViewById(R.id.pager_radio_insert);

        mPageBar1 = mEditActivity.findViewById(R.id.pager_bar1);

        mToolVp.addOnPageChangeListener(new MyOnPageChangeListener());
        mToolVp.setOffscreenPageLimit(1);
        mToolVp.setCurrentItem(0);// Set the currently displayed tab page as the first page
    }

    @Override
    public void onEditParams(int type, String value) {
        mEditActivity.setEditParams(type, value);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageSelected(int position) {
            mInsertRb.setTextColor(Color.parseColor("#444444"));

            mInsertRb.setChecked(true);

            mPageBar1.setVisibility(View.VISIBLE);
            mInsertRb.setTextColor(Color.parseColor("#23b4f3"));
            mInsertRb.setChecked(true);
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {//内容
        @Override
        public void onClick(View view) {
        }
    };

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

        @Override
        public int getCount() {
            return tabTitle.length;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

    }
}
