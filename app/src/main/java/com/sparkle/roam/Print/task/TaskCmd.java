package com.sparkle.roam.Print.task;


import com.lvrenyang.io.Pos;
import com.sparkle.roam.Print.view.main.MainActivity;


/**
 * Created by hzh on 2018/07/15.
 *
 * 蓝牙连接相关操作 已移到WorkThread.java中
 * 具体参考WorkThread相关
 */

public class TaskCmd implements Runnable {

    private static final String TAG = "TaskCmd";

    private MainActivity mActivity = null;
    private Pos mPos = null;

    public final static int mEncryptKey = 0x12345678;       // 弱加密密码, 不同机器密码不同，具体咨询志众电子.
    public final static int mEncryptKey2 = 1;

    public final static String mEncryptPsw = "keyword";     // 强加密密码, 不同机器密码不同，具体咨询志众电子.

    public TaskCmd(MainActivity activity, Pos pos) {
        mActivity = activity;
        mPos = pos;
    }

    @Override
    public void run() {
    }

    //  
    private boolean sendWrite(byte[] buffer, int count) {
        if (mPos.POS_SendBuffer(buffer, 0, count) == count) {
            return true;
        } else {
            return false;
        }
    }

    private int sendRead(byte[] buffer, int count, int timeout) {
        int read = mPos.POS_ReadBuffer(buffer, 0, count, timeout);
        if (read != -1 && read != 0 && read != count) {
            return read;
        } else {
            return -2;
        }
    }

}
