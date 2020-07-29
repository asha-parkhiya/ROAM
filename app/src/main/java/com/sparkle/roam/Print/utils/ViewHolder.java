package com.sparkle.roam.Print.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * @author zhengjb
 * 2015/03/13 
 * 
 * ViewHolder通用类
 * 减少代码书写
 * */
public class ViewHolder {
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}