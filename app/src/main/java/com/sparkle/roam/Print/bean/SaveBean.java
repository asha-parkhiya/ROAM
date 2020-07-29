package com.sparkle.roam.Print.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/12/29.
 */

public class SaveBean implements Serializable{
    private LabelBean labelBean;
    private int position;
    private List<PrintParamsBean> paramsBeanList;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LabelBean getLabelBean() {
        return labelBean;
    }

    public void setLabelBean(LabelBean labelBean) {
        this.labelBean = labelBean;
    }

    public List<PrintParamsBean> getParamsBeanList() {
        return paramsBeanList;
    }

    public void setParamsBeanList(List<PrintParamsBean> paramsBeanList) {
        this.paramsBeanList = paramsBeanList;
    }

    @Override
    public String toString() {
        return "SaveBean{" +
                "labelBean=" + labelBean +
                ", position=" + position +
                ", paramsBeanList=" + paramsBeanList +
                '}';
    }
}
