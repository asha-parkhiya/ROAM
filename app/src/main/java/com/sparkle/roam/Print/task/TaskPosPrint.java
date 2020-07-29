package com.sparkle.roam.Print.task;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.example.printlibrary.utils.DimensUtil;
import com.example.printlibrary.utils.StringUtils;
import com.lvrenyang.io.Pos;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.Print.utils.AppConst;
import com.sparkle.roam.Print.utils.PrinterApi;
import com.sparkle.roam.Print.utils.ToastUtil;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.R;
import com.wisdom.tian.utils.ImageUtil;

import static com.example.printlibrary.utils.StringUtils.hexStringToBytes;

/**
 * Created by jc on 2017/11/18.
 */
@SuppressLint("NewApi")
public class TaskPosPrint implements Runnable {

    private static final String TAG = "TaskPosPrint";

    private Bitmap mBitmap;
    private PrintParamsBean mPrintParamsBean;
    private boolean isPrint = true;
    private Pos pos;
    private int printType = -1;
    private DragView mDragView = null;

    public TaskPosPrint(Pos pos, DragView dragView, PrintParamsBean printParamsBean) {
        this.pos = pos;
        this.isPrint = isPrint;
        mDragView = dragView;
        mPrintParamsBean = printParamsBean;

        printType = -1;

        mBitmap = getLableBitmap(dragView, printParamsBean);
    }

    public TaskPosPrint(Pos pos, DragView dragView, PrintParamsBean printParamsBean, int type) {
        this(pos, dragView, printParamsBean);

        printType = type;
    }

    private Bitmap getLableBitmap(DragView dragView, PrintParamsBean printParamsBean){
        if (dragView != null && dragView.getChildCount() > 0){
            Bitmap bitmap = ImageUtil.getViewBitmap(dragView);

            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                int printWidth = printParamsBean.getPrintWidth();
                int printHeight = printParamsBean.getPrintHeight();


                if (width < height) {
                    //mBitmap = ImageUtil.resizeImage(mBitmap, DimensUtil.getDisplayWidth(dragView.getContext()), (DimensUtil.getDisplayWidth(dragView.getContext())/4)*3);
                    //mBitmap = ImageUtil.imageCropAll(mBitmap, width, height, true);
                    //mBitmap = ImageUtil.imageCropAll(mBitmap, width- DimensUtil.dpToPixels(dragView.getContext(), 8), height, true);
                } else {
                    bitmap = ImageUtil.imageCropAll(bitmap, width - DimensUtil.dpToPixels(dragView.getContext(), 35), height, true);
                    bitmap = ImageUtil.imageCropAll(bitmap, width, height - DimensUtil.dpToPixels(dragView.getContext(), 8), true);
                }
                bitmap = ImageUtil.resizeImage(bitmap, printWidth, printHeight);
            }
            return bitmap;
        }
        return null;
    }

    // BT发送数据
    // send data to bluetooth
    private boolean sendBuffer(byte []data){
        return pos.POS_SendBuffer(data);
    }

    // print demo
    private void PrintDemo2(){
        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));

        // 打印文本
        // print text
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_LEFT));        // print aligne left
        sendBuffer(StringUtils.hexStringToBytes("31323334353637380d0a"));       // 12345678
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_CENTER));        // print aligne center
        sendBuffer(StringUtils.hexStringToBytes("41424344454647480d0a"));       // ABCDEFG..
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_RIGHT));        // print aligne right
        sendBuffer(StringUtils.hexStringToBytes("61626364656667680d0a"));       // ABCDEFG..

        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_LEFT));        // print aligne left

        // 条码
        // print barcode
        sendBuffer(StringUtils.hexStringToBytes("1d4802"));
        sendBuffer(StringUtils.hexStringToBytes("1d6832"));
        sendBuffer(StringUtils.hexStringToBytes("1d7702"));
        sendBuffer(StringUtils.hexStringToBytes("1d6b083031323334353637383900"));   // 条码

        // 二维码
        // print QR Code demo
        sendBuffer(StringUtils.hexStringToBytes("1d286b03003143081d286B0700315030616263640a"));

        int printWidth = mPrintParamsBean.getPrintWidth();
        int printHeight = mPrintParamsBean.getPrintHeight();
        // 打印图片
        // print bitmap demo
        if (mBitmap != null) {
            // 以下打印图片指令，适用于所有打印机。
            int algo = 1;   // true = only picture, false= Text & Picture
            byte[] data1 = pos.POS_Bitmap2Data(mBitmap, printWidth, algo, 0);
            sendBuffer(data1);    // 直接打印数据

            algo = 0;       // true = only picture, false= Text & Picture
            byte[] data2 = pos.POS_Bitmap2Data(mBitmap, printWidth, algo, 0);
            sendBuffer(data2);    // 直接打印数据

            // 以下打印图片指令，适用于专用的标签打印机。数据有压缩。
            // 不是所有机器都支持。 zz mdjbt系列机器支持。 或咨询 志众电子...
            //
            // print bitmap, not for all printer. only for zz mdjbt_xxx
            byte[] data3 = ImageUtil.Bitmap2PosCmd(mBitmap, printWidth, true);
            sendBuffer(data3);    // 直接打印数据
        }
        // 走纸,方便撕纸
        // feed paper to cut.
        sendBuffer(hexStringToBytes("1B6408"));      // 走纸

        // 只有标签打印机，才有对纸命令
        // 普通小票打印机，没有对纸命令
        // feed label paper to gap. noly for label printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEXT_GAP));     // 对纸
    }

    // 建议参考使用
    // 使用zz打印接口打印
    // 演示如何打印中文、英文、条码、二维码、图片等。
    private void PrintDemo1(){
        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));

        //
        // 打印文本
        // print Text demo
        byte []data = PrinterApi.getPrintStringGBKBytes("厦门志众电子\n1234567890abc!\n\n");
        sendBuffer(data);

        // 条码
        // Barcode demo
        data = PrinterApi.getPrintBarcodeBytes("0123456789", 2, 2, 0x32);        // 条码
        sendBuffer(data);

        // 换行
        // new line (CR)
        data = PrinterApi.getPrintStringGBKBytes("\n\n");
        sendBuffer(data);

        // 二维码1
        // QR Code GBK demo Demo
        data = PrinterApi.getPrintQrcodeGBKBytes("厦门志众电子SDK-二维码GBK", 8, 0);
        sendBuffer(data);

        // 换行
        // new line (CR)
        data = PrinterApi.getPrintStringGBKBytes("\n\n\n");
        sendBuffer(data);

        // 二维码2
        // QR code UTF8 data demo
        data = PrinterApi.getPrintQrcodeUTF8Bytes("厦门志众电子SDK-二维码UTF8", 8, 0);
        sendBuffer(data);

        // 二维码3
        // QR code UTF8 data demo
        data = PrinterApi.getPrintQrcodeUTF8Bytes("QRCodeDemoUTF8", 8, 0);
        sendBuffer(data);

        // 换行
        // new line (CR)
        data = PrinterApi.getPrintStringGBKBytes("\n\n\n");
        sendBuffer(data);

        int printWidth = mPrintParamsBean.getPrintWidth();      // paper width, dots
        int printHeight = mPrintParamsBean.getPrintHeight();    // paper height, dots
        // 打印图片
        // print picture
        if (mBitmap != null) {
            // 以下打印图片指令，适用于所有打印机。
            // print picture, use for all bill note printer.
            boolean algo = true;   // true = only picture, false= Text & Picture
            byte[] data1 = PrinterApi.getPrintBitmap(mBitmap, printWidth, algo);
            sendBuffer(data1);

            algo = false;           // true = only picture, false= Text & Picture
            byte[] data2 = PrinterApi.getPrintBitmap(mBitmap, printWidth, algo);
            sendBuffer(data2);

            // 以下打印图片指令，适用于专用的标签打印机。数据有压缩, 压缩率一般
            // 不是所有机器都支持。 具体咨询 志众电子...
            //
            // picture print command, not all printer supported.
            byte[] data3 = PrinterApi.getPrintBitmap_zz(mBitmap, printWidth, true);
            sendBuffer(data3);

            // 以下打印图片指令，适用于专用的标签打印机。数据有压缩, 压缩率较高
            // 不是所有机器都支持。 zz mdjbt适合, 具体咨询 志众电子...
            //
            // picture print command, not all printer supported, noly for zz mdjbt_xxx
            byte[] data4 = PrinterApi.getPrintBitmapFast_zz(mBitmap, printWidth, true);
            sendBuffer(data4);
        }

        // 走纸,方便撕纸
        // feed paper, to cut paper
        sendBuffer(hexStringToBytes("1B6408"));

        // 只有标签打印机，才有对纸命令
        // 普通小票打印机，没有对纸命令

        // feed label paper to gap.
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEXT_GAP));
    }

    // 建议参考使用
    // 使用zz打印接口打印
    // 演示如何打印CPCL指令
    // CPCL指令演示，以下指令只有标签打印机 且在快递模式下才能正常打印。
    //
    // only use for zz mdjbt machine
    private void PrintCPCL_Demo(){
        // CPCL指令演示，以下指令只有标签打印机 且在快递模式下才能正常打印。
        //
        // only zz label printer can use
        String str = "! 0 200 200 766 1\r\n" +
                "PAGE-WIDTH 580\r\n" +
                "TEXT 6 0 8 0 Test Demo Text1\r\n" +
                "TEXT90 6 0 100 200 Test Demo Text2\r\n" +
                "TEXT 4 0 8 40 Text Font 4-0\r\n" +
                "TEXT 4 3 8 80 Text Font 4-3\r\n" +
                "TEXT 55 0 8 150 Text Font 55-0\r\n" +
                "TEXT 55 3 8 200 Text Font 55-3\r\n" +
                "TEXT 24 0 8 250 Text Font 24-0\r\n" +
                "TEXT 24 6 8 300 Text Font 24-6\r\n" +
                "BARCODE 128 3 2 90 80 350 534480735076\r\n" +  // barcode
                "B QR 32 216 M 2 U 6\r\n" +         // QR code
                "MA,310113663227571710\r\n" +
                "ENDQR\r\n" +
                "FORM\r\n" +
                "PRINT\r\n";
        byte []cmd = PrinterApi.getPrintStringGBKBytes(str);
        sendBuffer(cmd);
    }

    // return CPCL data
    private byte[]getCpclCommand(int x, int y, int w, int h, byte[]dat){
        String str1 = "! 0 200 200 766 1\r\n" +
                "PAGE-WIDTH 580\r\n";
        byte []data1 = PrinterApi.getPrintStringGBKBytes(str1);
        String str2 = String.format("CG2 %d %d %d %d ", w, h, x, y);
        byte []data2 = PrinterApi.getPrintStringGBKBytes(str2);
        data2 = PrinterApi.byteMerger(data2, dat);

        data1 = PrinterApi.byteMerger(data1, data2);

        String strPrint = "\r\nFORM\r\nPRINT\r\n";
        byte []dataPrint = PrinterApi.getPrintStringGBKBytes(strPrint);
        data1 = PrinterApi.byteMerger(data1, dataPrint);
        return data1;
    }

    // only use for zz mdjbt machine
    // this is show how to print bitmap with CPCL command
    private void PrintCPCL_Demo2(){
        int printWidth = mPrintParamsBean.getPrintWidth();      // paper width, dots
        int printHeight = mPrintParamsBean.getPrintHeight();    // paper height, dots

        // CPCL指令演示，以下指令只有标签打印机 且在快递模式下才能正常打印。
        //
        // only zz label printer can use
        // TSPL demo
        if (mBitmap != null) {
            boolean algo = false;   // true = only picture, false= Text & Picture
            byte []image = PrinterApi.getPrintBitmapTspl(mBitmap, printWidth, algo, true);
            if (image != null){
                int w_bytes = (printWidth+7)/8;
                byte []data = getCpclCommand(0, 0, w_bytes, image.length / w_bytes, image);
                sendBuffer(data);
            }
        }
        else{
            ToastUtil.showShortToast(mDragView.getContext(), "please add picture.");
        }
    }

    // exp CPCL demo
    private void PrintCPCL_ExpDemo(){
        String str = "! 0 200 200 1256 1\r\n"+
                "PW 600\r\n"+
                "LINE 6 192 578 192 1\r\n"+
                "LINE 6 448 442 448 1\r\n"+
                "LINE 6 512 442 512 1\r\n"+
                "LINE 6 775 578 775 1\r\n"+
                "LINE 6 1162 578 1162 1\r\n"+
                "LINE 442 192 442 670 1\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 0 始发网点: 鹰潭YD(鹰潭市韵达快运有限公司)\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 100 32 收件人 :duhgfgh\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 100 52 电话 :15875585555\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 100 72 收件地址 :北京市 北京市 朝阳区 北京市 北京市 朝阳区dhj\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 100 92 gfffff \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 448 28 2016-11-07\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 448 48 09:58:42\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 448 8 体积:\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 328 8 5.00 kg\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 328 33 普通\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 11 24 32 送达\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 11 24 60 地址\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 16 167 集包地：北京网点包 \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 365 168 J100470 \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 11 200 216 100 \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 11 200 276  \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 11 200 336  \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 24 470 运单编号 :3101136632275\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 600 收件人/代签人\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 634 签收时间:  年  月  日\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 782 发件人 :dhhhgg\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 802 电话 :13558855558\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 822 发件地址 :重庆市 重庆市 大渡口区fhhhgfff \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 842  \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 862 收件人 :duhgfgh\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 882 电话 :15875585555\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 902 收件地址 :北京市 北京市 朝阳区北京市 北京市 朝阳区\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 922 dhjgfffff \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 964 发件人 :dhhhgg\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 984 电话 :13558855558\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 1004 发件地址 :重庆市 重庆市 大渡口区fhhhgfff \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 1024  \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0\r\n"+
                " 32 1044 收件人 :duhgfgh\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 1064 电话 :15875585555\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 1084 收件地址 :北京市 北京市 朝阳区北京市 北京市 朝阳区\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 32 1104 dhjgfffff \r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 448 1120 2016-11-07\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 448 1140 09:58:42\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 448 1055 5.00 kg\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 448 1080 普通\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 32 1135 运单编号 :3101136632275\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 55 0 24 1168 官方网址: http://www.yundaex.com 客服热线:  95546  发货人联\r\n"+
                "B 128 1  2 48 320 118 J100470 \r\n"+
                "VB 128 2  2 80 460 610 310113663227571710\r\n"+
                "B 128 1  2 50 23 690 310113663227571710\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 23 750 3101136632275 \r\n"+
                "B QR 32 216 M 2 U 6\r\n"+
                "MA,310113663227571710\r\n"+
                "ENDQR\r\n"+
                "LINE 448 862 560 862 5\r\n"+
                "LINE 448 902 560 902 5\r\n"+
                "LINE 448 862 448 902 5\r\n"+
                "LINE 560 862 560 902 5\r\n"+
                "UT 0\r\n"+
                "SETBOLD 0\r\n"+
                "IT 0\r\n"+
                "TEXT 24 0 470 872 已验视\r\n"+
                "PR 0\r\n"+
                "FORM\r\n"+
                "PRINT\r\n";
        byte []cmd = PrinterApi.getPrintStringGBKBytes(str);
        sendBuffer(cmd);
    }

    // print text demo
    private void PrintTextDemo(){
        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));

        // 打印文本
        // print text
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_LEFT));        // print aligne left
        sendBuffer(PrinterApi.getPrintStringGBKBytes("123456789\n"));

        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_CENTER));        // print aligne center
        sendBuffer(PrinterApi.getPrintStringGBKBytes("ABCDEFGHIJKL\n"));

        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_RIGHT));        // print aligne right
        sendBuffer(PrinterApi.getPrintStringGBKBytes("abcdefghijkl\n"));

        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));
    }

    // print barcode demo
    private void PrintBarcodeDemo(){
        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));

        // 条码
        // Barcode demo
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_LEFT));        // print aligne left
        sendBuffer(PrinterApi.getPrintBarcodeBytes("0123456789", 2, 2, 0x32));

        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_CENTER));        // print aligne center
        sendBuffer(PrinterApi.getPrintBarcodeBytes("0123456789", 2, 2, 0x32));

        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_RIGHT));        // print aligne right
        sendBuffer(PrinterApi.getPrintBarcodeBytes("0123456789", 2, 2, 0x32));

        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));
    }
    // print qr code demo
    private void PrintQrcodeDemo(){
        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));

        // 二维码3
        // QR code UTF8 data demo
        sendBuffer(PrinterApi.getPrintQrcodeUTF8Bytes("http://xmzzdz.com/", 8, 0));

        // 换行
        // new line (CR)
        sendBuffer(PrinterApi.getPrintStringGBKBytes("\n\n\n"));

        sendBuffer(PrinterApi.getPrintQrcodeUTF8Bytes("http://xmzzdz.com/", 9, 1));

        // 换行
        // new line (CR)
        sendBuffer(PrinterApi.getPrintStringGBKBytes("\n\n\n"));

        sendBuffer(PrinterApi.getPrintQrcodeUTF8Bytes("http://xmzzdz.com/", 10, 2));

        // 换行
        // new line (CR)
        sendBuffer(PrinterApi.getPrintStringGBKBytes("\n\n\n"));

        sendBuffer(PrinterApi.getPrintQrcodeUTF8Bytes("http://xmzzdz.com/", 11, 3));

        // 初始化打印机，打印机恢复默认值
        // init printer, reset printer
        sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));
    }

    private void PrintImageDemo(){
        int printWidth = mPrintParamsBean.getPrintWidth();      // paper width, dots
        int printHeight = mPrintParamsBean.getPrintHeight();    // paper height, dots
        // 打印图片
        // print picture
        if (mBitmap != null) {
            // 初始化打印机，打印机恢复默认值
            // init printer, reset printer
            sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));

            // 以下打印图片指令，适用于专用的标签打印机。数据有压缩, 压缩率较高
            // 不是所有机器都支持。 zz mdjbt适合, 具体咨询 志众电子...
            //
            // picture print command, not all printer supported, noly for zz mdjbt_xxx
            byte[] data4 = PrinterApi.getPrintBitmapFast_zz(mBitmap, printWidth, true);
            sendBuffer(data4);

            // 初始化打印机，打印机恢复默认值
            // init printer, reset printer
            sendBuffer(hexStringToBytes(AppConst.PRNCMD_TEST_INIT));
        }
        else{
            ToastUtil.showShortToast(mDragView.getContext(), "please add picture.");
        }
    }

    // return TSPL data
    private byte[]getTsplCommand(int x, int y, int w, int h, byte[]dat){
        String str1 = "CLS\r\n" +
                "SET TEAR 1\r\n" +
                "SET CUTTER 0\r\n" +
                "SIZE 76 mm,48 mm\r\n" +
                "GAP 2 mm,0 mm\r\n";

        byte []data1 = PrinterApi.getPrintStringGBKBytes(str1);
        String str2 = String.format("BITMAP %d,%d,%d,%d,7,", x, y, w, h);
        byte []data2 = PrinterApi.getPrintStringGBKBytes(str2);
        data2 = PrinterApi.byteMerger(data2, dat);

        data1 = PrinterApi.byteMerger(data1, data2);

        String strPrint = "\r\nPRINT 1,1\r\n";
        byte []dataPrint = PrinterApi.getPrintStringGBKBytes(strPrint);
        data1 = PrinterApi.byteMerger(data1, dataPrint);
        return data1;
    }

    // TSPL Demo
    // this is show how to print bitmap with TSPL command
    private void PrintTSPLDemo(){
        int printWidth = mPrintParamsBean.getPrintWidth();      // paper width, dots
        int printHeight = mPrintParamsBean.getPrintHeight();    // paper height, dots
        // TSPL demo
        if (mBitmap != null) {
            boolean algo = false;   // true = only picture, false= Text & Picture
            byte []image = PrinterApi.getPrintBitmapTspl(mBitmap, printWidth, algo, false);
            if (image != null){
                int w_bytes = (printWidth+7)/8;
                byte []data = getTsplCommand(0, 0, w_bytes, image.length / w_bytes, image);
                sendBuffer(data);
            }
        }
        else{
            ToastUtil.showShortToast(mDragView.getContext(), "please add picture.");
        }
    }

    // bill note printer demo
    private void PrintDemo(){
        //PrintDemo2();        // print bill note demo
        PrintDemo1();        // print bill note demo, recommend to use
    }

    @Override
    public void run() {
        if (printType > 0){     // print special demo
//            switch (printType) {
//                case R.id.tool_action_text:
//                    PrintTextDemo();
//                    break;
//                case R.id.tool_action_barcode:
//                    PrintBarcodeDemo();
//                    break;
//                case R.id.tool_action_qrcode:
//                    PrintQrcodeDemo();
//                    break;
//                case R.id.tool_action_image:    // print image
//                    PrintImageDemo();
//                    break;
//                case R.id.tool_action_cpcl: // CPCL demo
//                    PrintCPCL_Demo();   // print CPCL demo, only for ZZ Label printer
//                    PrintCPCL_Demo2();   // print CPCL demo, only for ZZ Label printer
//
//                    //PrintCPCL_ExpDemo();    // express CPCL demo, only chinese version.
//                    break;
//                case R.id.tool_action_tspl: // TSPL demo:
//                    PrintTSPLDemo();            // print TSPL Demo
//                    break;
//            }
            printType = -1;
        }
        else {
            PrintDemo();        // print demo
        }
    }
}
