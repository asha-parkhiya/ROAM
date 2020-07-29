package com.sparkle.roam.Print.view.edit;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.printlibrary.permission.PermissionCenter;
import com.example.printlibrary.utils.BaseParser;
import com.example.printlibrary.utils.DimensUtil;
import com.example.printlibrary.utils.LogUtils;
import com.example.printlibrary.utils.StringUtils;
import com.example.printlibrary.utils.URIUtil;
import com.sparkle.roam.Print.base.AbsBaseActivity;
import com.sparkle.roam.Print.bean.PrintParamsBean;
import com.sparkle.roam.Print.bean.SaveBean;
import com.sparkle.roam.Print.service.StartZPService;
import com.sparkle.roam.Print.service.ZPrinterManager;
import com.sparkle.roam.Print.utils.ToastUtil;
import com.sparkle.roam.Print.utils.UIHelp;
import com.sparkle.roam.Print.view.customView.DragView;
import com.sparkle.roam.Print.view.dialog.DialogSaveInfo;
import com.sparkle.roam.Print.view.dialog.DialogSetInfo;
import com.sparkle.roam.Print.view.settings.SettingPrinterActivity;
import com.sparkle.roam.R;

public class EditActivity extends AbsBaseActivity implements View.OnClickListener, DialogSaveInfo.EditDialogSaveParams {

    private static final String TAG = "EditActivity";//adb shell monkey -p com.wisdom.tian -v 10000 > D:\MonkeyTestLog.txt
    public static final int REQUEST_IMAGE_SELECT = 1;
    public static final int REQUEST_IMAGE_LOG0 = 2;
	
    public static final int REQUEST_IMAGE_SELECT_EDIT = 3;
    public static final int REQUEST_IMAGE_LOG0_EDIT = 4;
    public static final int REQUEST_IMAGE_BACKGROUND = 5;
	
	public static final int PARAMS_TYPE_LABELNAME = 0;      // 标签名
    public static final int PARAMS_TYPE_PRINTWIDTH = 1;     // 打印宽度
    public static final int PARAMS_TYPE_PRINTHEIGHT = 2;    // 打印高度

    // 用于处理各种通知消息，刷新界面的handler
    private final Handler mHandler = new Handler();

    private DialogSetInfo mDialogSetInfo;
    private static EditorMethod mEditorMethod;
    private ZPrinterManager mPrinterManager;
    private PermissionCenter mPermissionCenter;
    private DragView mDragView;
    private EditText mInputEt;
    private View mRootFl;
    private PrintParamsBean mParamsBean;
    public static int mPosition;
    private ViewPageView mViewPageView;
    private DialogSaveInfo mDialogSaveInfo;
    private StartZPService mStartZPService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        init();
    }

    @Override
    public void initView() {

        initTitle();

        mParamsBean = (PrintParamsBean) getIntent().getSerializableExtra("printParams");
        boolean isSet = getIntent().getBooleanExtra("isSet", false);
        SaveBean saveBean = (SaveBean) getIntent().getSerializableExtra("saveParams");

        if(mParamsBean == null){
            mParamsBean = new PrintParamsBean();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("tool_action_image");
        registerReceiver(mBroadcastReceiver, intentFilter);

        mPermissionCenter = new PermissionCenter(this);

        mStartZPService = StartZPService.getInstance();
        mPrinterManager = mStartZPService.getPrinterManager();

        findViewById(R.id.title_settings).setOnClickListener(this);
        mRootFl = findViewById(R.id.edit_container_label_viewer);
        mDragView = (DragView) findViewById(R.id.drag_view);
        mDragView.setEditor(1);
        if (mDragView.getChildCount() >0){
            mDragView.removeAllViews();
        }

        ViewGroup.LayoutParams layoutParams = mRootFl.getLayoutParams();
        layoutParams.width = DimensUtil.getDisplayWidth(this);
        layoutParams.height = (DimensUtil.getDisplayWidth(this)/4)*3;
        mRootFl.setLayoutParams(layoutParams);

        mInputEt = (EditText) findViewById(R.id.input_content_editor);
        mEditorMethod = new EditorMethod(EditActivity.this, mDragView, mInputEt);

        if(mParamsBean != null) {
            if (isSet) {
                UIHelp.setWidgetRefresh(this, mRootFl, mParamsBean);
            }

            if (!StringUtils.isEmpty(mParamsBean.getBackModelPath())) {

                mEditorMethod.showButton();
                mDragView.clearView();
                mDragView.setBackModel(true);
                mDragView.setBackModelBitmap(this, mParamsBean.getBackModelPath(), null);
            }
        }

        mViewPageView = new ViewPageView(this, mDragView, mInputEt);

        mDialogSaveInfo = new DialogSaveInfo(this);
        mDialogSaveInfo.setEditDialogSaveParams(this);


        LogUtils.d(TAG,"------- saveBean ->"+saveBean+";="+mParamsBean.getLabelName());
        if(saveBean != null){
            mDragView.clearView();
            mEditorMethod.showButton();
        }
    }

    public void sendToast(final String cotent){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ToastUtil.showShortToast(EditActivity.this, cotent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDragView.clearView();
        mDragView = null;
        unregisterReceiver(mBroadcastReceiver);
        System.gc();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && intent.getAction().equals("tool_action_image")) {
                String editorType = intent.getStringExtra("editorType");
                mPosition = intent.getIntExtra("position", -1);
                switch (editorType){
                    case "photo":
                        mPermissionCenter.permissionCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                        getImageViewPhoto();
                        break;
                }
            }
        }
    };


    private void initTitle() {

        findViewById(R.id.title_main_left).setOnClickListener(this);
        ((ImageView)findViewById(R.id.title_multiple)).setImageResource(R.drawable.toolbtn_single);

        ImageView zoomOutIv = (ImageView) findViewById(R.id.title_zoom_out);
        ImageView zoomInIv = (ImageView) findViewById(R.id.title_zoom_in);
        ImageView undoIv = (ImageView) findViewById(R.id.title_undo);
        ImageView redoIv = (ImageView) findViewById(R.id.title_redo);
        ImageView addImageIv = (ImageView) findViewById(R.id.title_addimage);

        zoomOutIv.setOnClickListener(this);
        zoomInIv.setOnClickListener(this);
        undoIv.setOnClickListener(this);
        redoIv.setOnClickListener(this);
        addImageIv.setOnClickListener(this);
        findViewById(R.id.title_print).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mPermissionCenter.setOnActivityResult(requestCode);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE_SELECT) {// /storage/emulated/0/DCIM/Camera/IMG_20180413_200954.jpg
                try {
                    String filePathByUri = URIUtil.getFilePathByUri(this, data.getData());
                    Bitmap bitmap = URIUtil.getSrcImage(this, data.getData(),350,350);
                    if(bitmap != null){
                        mEditorMethod.addImage(bitmap, filePathByUri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void getImageViewPhoto() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, REQUEST_IMAGE_SELECT);
        } else {
            Intent intentFromGallery = new Intent();
            intentFromGallery.setType("image/*");
            intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intentFromGallery, REQUEST_IMAGE_SELECT);
        }
    }

    public void setEditParams(int type, String value){
        Log.d(TAG,"-------obj------>"+( value)+";type="+type);
        if(mParamsBean != null) {
            if (type == 0) {
                mParamsBean.setLabelName(value);
            } else if (type == 1) {
                mParamsBean.setPrintWidth((int) UIHelp.mmToPointWidth(BaseParser.parseInt(value)));
                mParamsBean.setWidgetWidth(mDragView.getWidth());
                mParamsBean.setCurrentWidth(BaseParser.parseInt(value));
            } else if (type == 2) {
                mParamsBean.setPrintHeight((int) UIHelp.mmToPointHeight(BaseParser.parseInt(value)));
                mParamsBean.setWidgetHeight(mDragView.getHeight());
                mParamsBean.setCurrentHeight(BaseParser.parseInt(value));
            }

            UIHelp.setWidgetRefresh(this, mRootFl, mParamsBean);
        }
    }

    public View.OnClickListener insertOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (!StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
                sendToast(getString(R.string.msg_connect_printer_first));
                return ;
            }
            switch (id) {
                case R.id.tool_action_tspl:
                case R.id.tool_action_image://图片
                case R.id.tool_action_cpcl:
                case R.id.tool_action_text:
                case R.id.tool_action_barcode:
                case R.id.tool_action_qrcode:   // start print special democ
                    StartZPService.getInstance().getPrinterManager().workThread.MyPrinter(mDragView, mParamsBean, id);
                    break;
            }
        }
    };

    private boolean getIsSave(){
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_main_left:
                finish();
                break;
            case R.id.title_addimage: // add picture
                mPermissionCenter.permissionCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                getImageViewPhoto();
                break;

            case R.id.title_print://打印
                mDragView.startPrint();
                if (!StartZPService.getInstance().getPrinterManager().workThread.isConnected()) {
                    ToastUtil.showShortToast(this, getString(R.string.printer_not_connected));
                    return;
                }
                currentPrint();
                break;
            case R.id.title_settings://设置
                startActivity(new Intent(this, SettingPrinterActivity.class));
                break;
        }
    }

    @Override
    public void setSaveParams(DialogSaveInfo dialog, int type) {
        finish();
        dialog.dismiss();
    }

    public void currentPrint(){
        StartZPService.getInstance().getPrinterManager().workThread.MyPrinter( mDragView, mParamsBean);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
