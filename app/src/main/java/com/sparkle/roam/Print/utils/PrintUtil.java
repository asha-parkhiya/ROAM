package com.sparkle.roam.Print.utils;

import com.example.printlibrary.utils.ArrayUtils;
import com.example.printlibrary.utils.LogUtils;
import com.example.printlibrary.utils.StringUtils;
import com.lvrenyang.io.Pos;

/**
 * @author Tian
 *         Created by jc on 2017/11/6.
 */

public class PrintUtil {

    private static final String TAG = "PrintUtil";

    private static int printNum = 0;

    public static void WaitMs(long ms) {
        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < ms)
            ;
    }

    /**读取数据*/
    public static byte[] redByteValue(byte[] bufferR, int read){
        if(read < 0){
            return new byte[]{-1};
        }
        return ArrayUtils.subBytes(bufferR, 0, read);
    }


    private static void sendPrint(Pos pos,byte[] buf){
        if (pos.POS_SendBuffer(buf, 0, buf.length) == buf.length) {
            LogUtils.e(TAG,"------------readCommon-发送成功-->"+buf.length);
        } else {
            LogUtils.e(TAG,"------------readCommon-发送失败-->");
        }
    }

    public static String readCommon(Pos pos,int timeout){
        String type = "";
        byte[] buf = new byte[128];
        int offset = 0;
        int read = pos.POS_ReadBuffer(buf, offset, buf.length, timeout);
        LogUtils.e(TAG,"------------readCommon-read--3->"+read);
        if (read != -1 && read != 0 && read != buf.length) {
            byte[] bytes = redByteValue(buf, read);
            LogUtils.d(TAG, "------readCommon--value=>"+";bytes="+bytes.length);

            if(bytes.length == 0 || bytes[0] == -1){
                return type;
            }

            type = StringUtils.byte2hex(bytes);
            String name = StringUtils.getStringGB2312(bytes);


            LogUtils.d(TAG, "------readCommon--str=>"+type);
            LogUtils.d(TAG, "------readCommon--uu-str=>"+name);
        } else {
            type = "";
            LogUtils.e(TAG,"------------readCommon-read--fild->"+read);
        }

        return type;
    }

    // 字符串转化为十六进制字节数组
    public static byte[] convert2HexArray(String apdu) {
        int len = apdu.length() / 2;
        char[] chars = apdu.toCharArray();
        String[] hexes = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i = i + 2, j++) {
            hexes[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexes[j], 16);
        }
        return bytes;
    }

    public static String ResultCodeToString(int code) {
        switch (code) {
            case 0:
                return "打印成功";
            case -1:
                return "连接断开";
            case -2:
                return "写入失败";
            case -3:
                return "读取失败";
            case -4:
                return "打印机脱机";
            case -5:
                return "打印机缺纸";
            case -7:
                return "实时状态查询失败";
            case -8:
                return "查询状态失败";
            case -6:
            default:
                return "未知错误";
        }
    }
}
