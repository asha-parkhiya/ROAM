package com.sparkle.roam.Fragments;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Adapter.NewAdapter;
import com.sparkle.roam.ContentProvider.ToDo;
import com.sparkle.roam.ContentProvider.ToDoListDBAdapter;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;
import com.sparkle.roam.View.CodeErrorDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class PayEventHistoryDetailFragment extends BottomSheetDialogFragment implements View.OnClickListener, NewAdapter.onItemClickListener,CodeErrorDialog.OnLogout {

    private TextView tv_paydays, tv_payrecordamt, tv_payrecordnote, tv_eventtype, tv_codeissued, tv_codedays;
    private String paydays, payrecordamt, payrecordnote, eventtype, codeissued, codedays, codehashtop, payAccID,PPID,firmwareVersion;
    private int codesize;
    private Button btn_issuecode;
    ImageButton btn_back;
    public View myRoot;

    private ToDoListDBAdapter toDoListDBAdapter;
    private List<ToDo> toDos;
    private NewAdapter newAdapter;
    private MyPref myPref;
    private DatabaseHelper mdbhelper;

    private BottomSheetBehavior mBehavior;
    private AppBarLayout app_bar_layout;
    private LinearLayout container;
    
    public PayEventHistoryDetailFragment() {
        // Required empty public constructor
    }

    public static PayEventHistoryDetailFragment newInstance(String paydays, String payRecordAmt, String payRecordNotes, String eventtype, String codeIssued, String codedays, String codehashtop, String payAccID,String productItemOESN,String firmwareVersion,int codesize) {
        PayEventHistoryDetailFragment fragment = new PayEventHistoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PAY_DAYS, paydays);
        args.putString(Constants.PAY_RECORD_AMT, payRecordAmt);
        args.putString(Constants.PAY_RECORD_NOTES, payRecordNotes);
        args.putString(Constants.EVENT_TYPE, eventtype);
        args.putString(Constants.CODE_ISSUED, codeIssued);
        args.putString(Constants.CODE_DAYS, codedays);
        args.putString(Constants.CODE_HASHTOP, codehashtop);
        args.putString(Constants.PAYACCID, payAccID);
        args.putString(Constants.PRODUCTOESN, productItemOESN);
        args.putString(Constants.FIRMWAREVERSION, firmwareVersion);
        args.putInt(Constants.CODESIZE, codesize);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paydays = getArguments().getString(Constants.PAY_DAYS);
            payrecordamt = getArguments().getString(Constants.PAY_RECORD_AMT);
            payrecordnote = getArguments().getString(Constants.PAY_RECORD_NOTES);
            eventtype = getArguments().getString(Constants.EVENT_TYPE);
            codeissued = getArguments().getString(Constants.CODE_ISSUED);
            codedays = getArguments().getString(Constants.CODE_DAYS);
            codehashtop = getArguments().getString(Constants.CODE_HASHTOP);
            PPID = getArguments().getString(Constants.PRODUCTOESN);
            firmwareVersion = getArguments().getString(Constants.FIRMWAREVERSION);
            codesize = getArguments().getInt(Constants.CODESIZE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.fragment_pay_event_history_detail, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        app_bar_layout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        container = (LinearLayout) view.findViewById(R.id.container);

        tv_paydays = (TextView) view.findViewById(R.id.tv_paydays);
        tv_payrecordamt = (TextView) view.findViewById(R.id.tv_payrecordamt);
        tv_payrecordnote = (TextView) view.findViewById(R.id.tv_payrecordnote);
        tv_eventtype = (TextView) view.findViewById(R.id.tv_eventtype);
        tv_codeissued = (TextView) view.findViewById(R.id.tv_codeissued);
        tv_codedays = (TextView) view.findViewById(R.id.tv_codedays);
//        btn_back = view.findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(this::onClick);

        btn_issuecode = view.findViewById(R.id.btn_issuecode);
        btn_issuecode.setOnClickListener(this);

        myPref = new MyPref(getContext());
        mdbhelper = new DatabaseHelper(getContext());

        init();

        hideView(app_bar_layout);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    showView(app_bar_layout, getActionBarSize());
                    hideView(container);
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    hideView(app_bar_layout);
                    showView(container, getActionBarSize());
                }

                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

//        ((ImageButton) view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View itemView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(itemView, savedInstanceState);
        
    }

    public void init() {
        if (codesize == 0){
            btn_issuecode.setEnabled(false);
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.btn_back_disable);
            btn_issuecode.setBackground(unwrappedDrawable);
        }

        if (firmwareVersion != null){
            try {
                JSONObject obj = new JSONObject(firmwareVersion);
                String device_type = obj.getString("firmware");

                System.out.println("--------firmwareVersion---------"+firmwareVersion);
                System.out.println("--------device_type---------"+device_type);
                if (device_type.startsWith("ovCamp")){
                    myPref.setPref(Constants.DEVICE_TYPE,Constants.OVCAMP_DEVICE);
                }else {
                    myPref.setPref(Constants.DEVICE_TYPE,Constants.LUMN_DEVICE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        if (paydays.equals("null")) {

        } else
            tv_paydays.setText(paydays);
        tv_payrecordamt.setText(payrecordamt);
        tv_payrecordnote.setText(payrecordnote);
        tv_eventtype.setText(eventtype);
        tv_codeissued.setText(codeissued);
        if (codedays.equals("null")) {

        } else
            tv_codedays.setText(codedays);

        ((HomeActivity) getActivity()).updateButtonAdd(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_back:
//                ((HomeActivity) getActivity()).onBackPressed();
//                break;

            case R.id.btn_issuecode:

                toDoListDBAdapter = ToDoListDBAdapter.getToDoListDBAdapterInstance(getContext());
                toDos=toDoListDBAdapter.getAllToDos();

                newAdapter = new NewAdapter(toDos,getContext(),this::changevalue);
                System.out.println("------toDos-------"+toDos.size());

//                Offline(Integer.parseInt(payAccID),codedays,codeissued,codehashtop);

                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
                Calendar c = Calendar.getInstance();
                String formattedDate = input.format(c.getTime());

                mdbhelper.updatePayEventData(codeissued,formattedDate);

                if (!codeissued.equals("")) {
                    if (myPref.getPref(Constants.DEVICE_TYPE,"").equals(Constants.OVCAMP_DEVICE)){
                        add_PPID(PPID,codeissued,Constants.OVCAMP_DEVICE);
                    }else {
                        add_PPID(PPID,codeissued,Constants.LUMN_DEVICE);
                    }
                }

                break;
        }
    }

    private void Offline(int payAccID, String codevalue,String codehasformate, String codehashtop) {

        int str = 0;
        Cursor cursor = mdbhelper.getLastEventData();
        if(cursor.moveToLast()){
            str  = Integer.parseInt(cursor.getString( cursor.getColumnIndex("payEventID") ));
            str = str + 1;
        }
//        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Calendar c = Calendar.getInstance();
        String formattedDate = input.format(c.getTime());

        mdbhelper.insertPayEventData(String.valueOf(str),formattedDate,"","",
                "",String.valueOf(payAccID),"codeIssue",String.valueOf(codevalue),String.valueOf(codehasformate),String.valueOf(codehashtop), String.valueOf(0));

    }

    public void add_PPID(String PPID, String codehasformate,String device_type){
        newAdapter.filter(String.valueOf(PPID));
        String string = myPref.getPref("string","");
        int position = myPref.getPref("position",0);
        if (string.contains(String.valueOf(PPID))){
            toDos.remove(position-1);
            toDos.add(position-1,new ToDo(PPID,codehasformate,device_type));

            toDoListDBAdapter.modify(PPID,codehasformate,device_type);
            System.out.println("--------------yes--if-----------"+string);
            myPref.setPref("string","");
            myPref.setPref("position",0);

        }else {
            System.out.println("--------------yes--else-----------"+string);
            toDos.add(new ToDo(PPID,codehasformate,device_type));
            toDoListDBAdapter.insert(String.valueOf(PPID),codehasformate,device_type);
        }
        CodeErrorDialog codeErrorDialog = new CodeErrorDialog(getContext(), "Code successfully save.",this::OnLogout);
        codeErrorDialog.show();
        newAdapter.notifyList(toDos);
        System.out.println("---after---toDos-------"+toDos.size());
    }

    @Override
    public void changevalue(String string, int position) {
        myPref.setPref("string",string);
        myPref.setPref("position",position);
    }

    @Override
    public void OnLogout(boolean msg) {
        if (msg) {
            dismiss();
//            ((HomeActivity) getActivity()).onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);
    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) styledAttributes.getDimension(0, 0);
        return size;
    }
}
