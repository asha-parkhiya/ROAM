package com.sparkle.roam.Fragments;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Displaymodel.DisplayPayAccount;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.MyPref;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PayAccountDetailFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    OnButtonClick onButtonClick;

    public interface  OnButtonClick{
        void onbclick(int btn_number,int position,String agentAssignment);
    }
    ImageButton btn_back,bt_close;
    ImageView btn_menu;
    Button btn_payeventhistory,btn_issuecode,btn_addpayevent;
    private TextView tv_accountProduct;
    private TextView tv_payoffAmt;
    private TextView tv_minPayDays;
    private TextView tv_maxPayDays;
    private TextView tv_depositDays;
    private TextView tv_schPayDays;
    private TextView tv_initialCreditDays;
    private TextView tv_receivedPayAmt;
    private TextView tv_firstname;
    private TextView tv_lastname;
    private TextView tv_leftday;
    private String payoffAmt;
    private String minPayDays;
    private String maxPayDays;
    private String depositDays;
    private String schPayDays;
    private String initialCreditDays;
    private String agentAssignment;
    private String receivedPayAmt;
    private String firmwareVersion;
    private String fname;
    private String lname;
    private String productItemOESN;
    private String assignmentstatus;
    private String searchtext;
    private int position;
    ArrayList<String> codes = new ArrayList<>();
    ArrayList<String> codesHashtop = new ArrayList<>();
    public View myRoot;
    MyPref myPref;

    private String payAccId;
    private DatabaseHelper mdbhelper;

    private boolean eventbus = false;

    private BottomSheetBehavior mBehavior;
    private AppBarLayout app_bar_layout;
    private LinearLayout container;


    public PayAccountDetailFragment(OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
        // Required empty public constructor
    }


    public static PayAccountDetailFragment newInstance(String payID,int position, String fname, String lname, String productItemOEM_SN, OnButtonClick onButtonClick) {
        PayAccountDetailFragment fragment = new PayAccountDetailFragment(onButtonClick);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.POSITION,position);
        bundle.putString(Constants.FNAME,fname);
        bundle.putString(Constants.PAYACCID,payID);
        bundle.putString(Constants.LNAME,lname);
        bundle.putString(Constants.PRODUCTOESN,productItemOEM_SN);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.fragment_pay_account_detail2, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);

        app_bar_layout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        container = (LinearLayout) view.findViewById(R.id.container);

        myPref = new MyPref(getContext());
//        btn_back = view.findViewById(R.id.btn_back);
        bt_close = view.findViewById(R.id.bt_close);
        btn_menu = view.findViewById(R.id.btn_menu);
        btn_addpayevent = view.findViewById(R.id.btn_addpayevent);
        btn_issuecode = view.findViewById(R.id.btn_issuecode);
        btn_payeventhistory = view.findViewById(R.id.btn_payeventhistory);
        btn_addpayevent.setOnClickListener(this::onClick);
        btn_issuecode.setOnClickListener(this::onClick);
        btn_payeventhistory.setOnClickListener(this::onClick);
//        btn_back.setOnClickListener(this::onClick);
        btn_menu.setOnClickListener(this::onClick);

        btn_payeventhistory = view.findViewById(R.id.btn_payeventhistory);
        tv_firstname = view.findViewById(R.id.tv_firstname);
        tv_lastname = view.findViewById(R.id.tv_lastname);
        tv_leftday = view.findViewById(R.id.tv_leftday);
        tv_accountProduct = view.findViewById(R.id.tv_productitem);
        tv_payoffAmt = view.findViewById(R.id.tv_payoffamount);
        tv_minPayDays = view.findViewById(R.id.tv_minpaydays);
        tv_maxPayDays = view.findViewById(R.id.tv_maxpaydays);
        tv_depositDays = view.findViewById(R.id.tv_depositedays);
        tv_schPayDays = view.findViewById(R.id.tv_schpaydays);
        tv_initialCreditDays = view.findViewById(R.id.tv_initialcreditdays);
        tv_receivedPayAmt = view.findViewById(R.id.tv_receivepay);

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

        ((ImageButton) view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position =  getArguments().getInt(Constants.POSITION);
            fname = getArguments().getString(Constants.FNAME);
            lname = getArguments().getString(Constants.LNAME);
            payAccId = getArguments().getString(Constants.PAYACCID);
            productItemOESN = getArguments().getString(Constants.PRODUCTOESN);
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        if (myRoot == null) {
//            myRoot = inflater.inflate(R.layout.fragment_pay_account_detail2, container, false);
//        }
//        return myRoot;
//    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void UpdateUI() {
        init();
    }

    private void init() {

        Cursor res = mdbhelper.getOnePayAccountData(payAccId);
        while (res.moveToNext()){

            int payAccountID = res.getInt(res.getColumnIndex("payAccountID"));
            String startDate = res.getString(res.getColumnIndex("startDate"));
            depositDays = String.valueOf(res.getInt(res.getColumnIndex("depositDays")));
            payoffAmt = String.valueOf(res.getInt(res.getColumnIndex("payoffAmt")));
            minPayDays = String.valueOf(res.getInt(res.getColumnIndex("minPayDays")));
            maxPayDays = String.valueOf(res.getInt(res.getColumnIndex("maxPayDays")));
            schPayDays = String.valueOf(res.getInt(res.getColumnIndex("schPayDays")));
            int userID = res.getInt(res.getColumnIndex("userID"));
            int distributorID = res.getInt(res.getColumnIndex("distributorID"));
            int assignedItemsID = res.getInt(res.getColumnIndex( "assignedItemsID"));
            int agentID = res.getInt(res.getColumnIndex( "agentID"));
            String agentAssignmentStatus = res.getString(res.getColumnIndex( "agentAssignmentStatus"));
            agentAssignment = res.getString(res.getColumnIndex( "agentAssignment"));
            System.out.println("-------------------query fire--------------"+agentAssignment);
            initialCreditDays = String.valueOf(res.getInt(res.getColumnIndex( "initialCreditDays")));
            receivedPayAmt = String.valueOf(res.getInt(res.getColumnIndex( "receivedPayAmt")));
            int durationDays = res.getInt(res.getColumnIndex( "durationDays"));
            int creditDaysIssued = res.getInt(res.getColumnIndex( "creditDaysIssued"));
            int payDaysReceived = res.getInt(res.getColumnIndex( "payDaysReceived"));

        }

        ArrayList<String> codes1 = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(agentAssignment);
            JSONArray jsonArray = jsonObject.getJSONArray("assignedCodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                codes1.add(jsonArray.getJSONObject(i).getString("otpHashFormatted"));
                System.out.println(jsonArray.getJSONObject(i).getString("otpHashFormatted"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_firstname.setText(fname);
        tv_lastname.setText(lname);
        tv_leftday.setText(String.valueOf(findLeftDays(codes1)));
        tv_accountProduct.setText(productItemOESN);
        tv_payoffAmt.setText(payoffAmt);
        tv_minPayDays.setText(minPayDays);
        tv_maxPayDays.setText(maxPayDays);
        tv_depositDays.setText(depositDays);
        tv_schPayDays.setText(schPayDays);
        tv_initialCreditDays.setText(initialCreditDays);
        tv_receivedPayAmt.setText(receivedPayAmt);

        ((HomeActivity) getActivity()).updateButtonAdd(false);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.btn_back:
//                ((HomeActivity)getActivity()).onBackPressed();
//                break;
            case R.id.btn_menu:
                Toast.makeText(getContext(),"Coming Soon...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_payeventhistory:
                if(onButtonClick!=null){
                    onButtonClick.onbclick(100,position,"");
                }
                break;
            case R.id.btn_issuecode:
                if(onButtonClick!=null){
                    onButtonClick.onbclick(200,position,agentAssignment);
                }
                break;
            case R.id.btn_addpayevent:
                if(onButtonClick!=null){
                    onButtonClick.onbclick(300,position,"");
                }
                break;

        }
    }
    public int findLeftDays(List<String> code){
        return code.size();
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

//    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
//    public void onMessageEvent(Integer days) {
//        eventbus = true;
//        tv_leftday.setText(String.valueOf(days));
//        EventBus.getDefault().removeAllStickyEvents();
//    }
}
