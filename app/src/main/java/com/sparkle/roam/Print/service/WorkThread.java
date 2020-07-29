package com.sparkle.roam.Print.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.Pos;

import com.example.printlibrary.utils.LogUtils;
import com.sparkle.roam.Print.bean.BluetoothInfoBean;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.Print.task.TaskCmd;
import com.sparkle.roam.Print.task.TaskPosPrint;
import com.sparkle.roam.Print.utils.AppConst;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.Print.view.main.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.printlibrary.utils.StringUtils.byte2hex;
import static com.example.printlibrary.utils.StringUtils.hexStringToBytes;
import static com.example.printlibrary.utils.StringUtils.randomHexString;
import static com.sparkle.roam.Print.cpp.NativeUtil.EncodeBuffer;
import static com.sparkle.roam.Print.cpp.NativeUtil.KeyInit;
import static com.sparkle.roam.Print.cpp.NativeUtil.encodeBuffer;
import static com.sparkle.roam.Print.cpp.NativeUtil.keyInit;
import static com.sparkle.roam.Print.utils.PrintUtil.convert2HexArray;


public class WorkThread{

	private static final String TAG = "WorkThread";
	private static Handler mWorkHandler = null;
	private static Looper mLooper = null;
	private static Handler mTargetHandler = null;
	private static boolean mThreadInitOK = false;
	private static boolean isConnecting = false;
	private static BTPrinting m_bt = null;
	private static Pos m_pos = new Pos();
	private static Context mContext = null;
	private TaskCmd mCmd = null;

	private ExecutorService m_es = Executors.newScheduledThreadPool(30);

	public WorkThread(Context context, Handler handler) {
		mContext = context;
		mThreadInitOK = false;
		mTargetHandler = handler;
		if (m_bt == null)
			m_bt = new BTPrinting();

		m_es.submit(new MyTask());
	}


	public class MyTask implements Runnable {

		@Override
		public void run() {
			myRun();
		}
	}



	public void myRun() {
		Looper.prepare();
		mLooper = Looper.myLooper();

		mWorkHandler = new WorkHandler();
		mThreadInitOK = true;
		Looper.loop();
	}

	private static class WorkHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case Global.MSG_WORKTHREAD_HANDLER_CONNECTBT: {
					if (!isConnecting) {    // 忙, 退出
						isConnecting = true;
						try {
							m_pos.Set(m_bt);

							Bundle bundle = msg.getData();
							BluetoothInfoBean infoBean = (BluetoothInfoBean) bundle.getSerializable("infoBean");
							boolean result = m_bt.Open(infoBean.getAddress(), mContext);
							if (result) {
								String mRandStr = randomHexString(16);
								byte[] buffer = getPrintCheckBytes(mRandStr);
								result = getPrinterStatus(buffer, mRandStr); // 检查打印机
							}
							LogUtils.e(TAG, "------------Open-1--->" + result);
							Message smsg = mTargetHandler
									.obtainMessage(Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT);
							int value = result ? 1 : 0;
							smsg.arg1 = value;
							infoBean.setResult(value);
							Bundle data = new Bundle();
							data.putSerializable("infoBeans", infoBean);
							smsg.setData(data);
							mTargetHandler.sendMessage(smsg);

							mContext.sendBroadcast(new Intent("BluetoothItem")
									.putExtra("BluetoothInfo", infoBean)
									.putExtra("BlueTag", Global.MSG_INFO_BEAN));
						}catch (Exception e){
							e.printStackTrace();
						}finally {
							isConnecting = false;
						}
					}
					break;
				}
				case Global.CMD_WRITE: {
					Bundle data = msg.getData();
					byte[] buffer = data.getByteArray(Global.BYTESPARA1);
					int offset = data.getInt(Global.INTPARA1);
					int count = data.getInt(Global.INTPARA2);

					Message smsg = mTargetHandler
							.obtainMessage(Global.CMD_WRITERESULT);
					if (m_pos.POS_SendBuffer(buffer, offset, count) == count) {
						smsg.arg1 = 1;
					} else {
						smsg.arg1 = 0;
					}
					mTargetHandler.sendMessage(smsg);
					break;
				}
				case Global.CMD_COMMON: {
					Bundle data = msg.getData();
					byte[] buffer = data.getByteArray(Global.BYTESPARA1);
					int offset = data.getInt(Global.INTPARA1);
					int count = data.getInt(Global.INTPARA2);

					if (m_pos.POS_SendBuffer(buffer, offset, count) == count) {
						LogUtils.e(TAG, "------------Open-发送成功-->" + count);
					}
					break;
				}
			}
		}

		// 检查打印机
		private boolean getPrinterStatus(byte []dat, String mRandStr){
			int retry = 0;
			boolean isOk = false;
			byte[] readBuffer = new byte[128];

			// 是否过滤指定的打印机
			if (false) {
				do {
					isOk = false;
					sendRead(readBuffer, readBuffer.length, 100 + retry * 100);        // read dummy data
					boolean result = sendWrite(dat, dat.length);			//发送随机数
					if (result) {
						isOk = waitPrinterAck(readBuffer, readBuffer.length, mRandStr, 2000);//读取返回数据
					}
					retry++;
				} while (!isOk && retry < 3);    // 重试2次
			}else{		// 不过滤指定的打印机
				isOk = true;
			}
			return isOk;
		}

		// 获取监测数据
		private byte [] getPrintCheckBytes(String mRandStr){
			String str = "";
			byte[] dat = hexStringToBytes(mRandStr);		// 1. 产生随机数
			if (true) {// 强加密. hzh add
				KeyInit(TaskCmd.mEncryptPsw);		// 设置加密密码
				EncodeBuffer(dat);  						// 2. 加密数据
				str = AppConst.PRNCMD_TEST_ENCRYPT + byte2hex(dat);	// 添加 1e5618 + d1...dn
			} else { // 加密2
				keyInit(TaskCmd.mEncryptKey, TaskCmd.mEncryptKey2);
				encodeBuffer(dat);  // encrypt
				str = AppConst.PRNCMD_TEST_QUERY + byte2hex(dat);
			}
			byte[] buffer = convert2HexArray(str);
			return buffer;
		}


		// 写数据
		private boolean sendWrite(byte[] buffer, int count) {
			if (m_pos.POS_SendBuffer(buffer, 0, count) == count) {
				return true;
			} else {
				return false;
			}
		}

		// 读数据
		private int sendRead(byte[] buffer, int count, int timeout) {
			int read = m_pos.POS_ReadBuffer(buffer, 0, count, timeout);
			if (read != -1 && read != 0 && read != count) {
				return read;
			} else {
				return -2;
			}
		}

		// 等待打印机应答
		private boolean waitPrinterAck(byte[] buffer, int max_size, String valueNum, int timeout) {
			int time = 0;
			int pos = 0;
			int count = 8;
			do {
				int read = m_pos.POS_ReadBuffer(buffer, pos, 1, 100);
				if (read > 0) {
					time = 0;   // 重新计时
					pos += read;
					if (pos >= count) {
						boolean equals = false;
						byte[] orig = hexStringToBytes(valueNum);
						if (orig.length >= count) {
							equals = true;
							// 第一个字节的数据为版本号
							for (int i = 1; i < count; i++) {   // first type is command type
								if (orig[i] != buffer[i]) {
									equals = false;
									break;
								}
							}
						}
						return equals;
					}
				}
				time += 10;
			} while (time < timeout && pos < count);
			return false;
		}
	}

	// 蓝牙连接命令(检查打印机是否合法)
	public synchronized void inAndOutCmd(MainActivity activity){
		try {
			if (mCmd == null) {
				mCmd = new TaskCmd(activity, m_pos);
			}
			if (mCmd != null) {
				m_es.submit(mCmd);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void quit() {
		try {
			if (null != mLooper) {
				mLooper.quit();
				mLooper = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectBt() {
		try {
			m_bt.Close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean connectBt(BluetoothInfoBean infoBean) {
		boolean ret = false;
		if ((null != mWorkHandler) && (null != mLooper)) {
			Bundle bundle = new Bundle();
			Message msg = mWorkHandler
					.obtainMessage(Global.MSG_WORKTHREAD_HANDLER_CONNECTBT);
			bundle.putSerializable("infoBean", infoBean);
			msg.setData(bundle);
			mWorkHandler.sendMessage(msg);

			mContext.sendBroadcast(new Intent("BluetoothStatus")
					.putExtra("BlueStatus", MainActivity.BT_STA_CONNECTING));    // 正在连接
			ret = true;
		} else {
			if (null == mWorkHandler) {
				Log.v(TAG, "workHandler is null pointer");
			}
			if (null == mLooper) {
				Log.v(TAG, "mLooper is null pointer");
			}
		}
		return ret;
	}

	public boolean isConnected() {
		if (m_bt.IsOpened())
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param cmd
	 */
	public void handleCmd(int cmd, Bundle data) {
		if ((null != mWorkHandler) && (null != mLooper)) {
			Message msg = mWorkHandler.obtainMessage(cmd);
			msg.setData(data);
			mWorkHandler.sendMessage(msg);
		} else {
			if (null == mWorkHandler)
				Log.v(TAG, "workHandler is null pointer");

			if (null == mLooper)
				Log.v(TAG, "mLooper is null pointer");
		}
	}

	public void MyPrinter(DragView dragView, PrintParamsBean printParamsBean){
		m_es.submit(new TaskPosPrint(m_pos, dragView, printParamsBean));
	}

	public void MyPrinter(DragView dragView, PrintParamsBean printParamsBean, int id){
		m_es.submit(new TaskPosPrint(m_pos, dragView, printParamsBean, id));
	}

}
