package com.sparkle.roam.Print.view.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.sparkle.roam.R;


@SuppressLint({"NewApi"})
public class DzCheckableLayout extends LinearLayout implements Checkable
{
  public DzCheckableLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DzCheckableLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DzCheckableLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  protected Checkable getCheckable()
  {
    try
    {
//      Checkable localCheckable = (Checkable)findViewById(R.id.iv_checkable);
//      return localCheckable;
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  public boolean isChecked()
  {
    Checkable localCheckable = getCheckable();
    if (localCheckable != null) {
      return localCheckable.isChecked();
    }
    return false;
  }
  
  public void setChecked(boolean paramBoolean)
  {
    Checkable localCheckable = getCheckable();
    if (localCheckable != null) {
      localCheckable.setChecked(paramBoolean);
    }
  }
  
  public void toggle()
  {
    Checkable localCheckable = getCheckable();
    if (localCheckable != null) {
      localCheckable.toggle();
    }
  }
}