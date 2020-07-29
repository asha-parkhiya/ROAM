package com.sparkle.roam.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.sparkle.roam.Activity.HomeActivity;
import com.sparkle.roam.Adapter.UserAdapter;
import com.sparkle.roam.LocalDatabase.DatabaseHelper;
import com.sparkle.roam.Model.SyncUserData.UserData;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.MyPref;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class UserListFragment extends Fragment implements View.OnClickListener, UserAdapter.OnMclick {

    private OnNavigation onNavigation;
    private ImageButton btn_back;
    private RecyclerView rv_list;
    private LinearLayoutManager layoutManager;
    private MyPref myPref;
    private Gson gson;
    private UserAdapter userAdapter;
    private List<UserData> userDataList;

    private DatabaseHelper mdbhelper;

    private BottomSheetBehavior mBehavior;
    private AppBarLayout app_bar_layout;
    private LinearLayout container;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                ((HomeActivity) getActivity()).onBackPressed();
                break;
        }

    }
    public interface OnNavigation {

        void onNavigationClick();
    }
     public UserListFragment() {
        // Required empty public constructor
    }


    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);

        myPref = new MyPref(getContext());
        rv_list = view.findViewById(R.id.rv_list);
        rv_list.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(layoutManager);

        userDataList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),userDataList,this::onMclick);
        rv_list.setAdapter(userAdapter);

        mdbhelper = new DatabaseHelper(getContext());

        UpdateUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        return  view;
    }

    public void UpdateUI() {
        ((HomeActivity) getActivity()).updateButtonAdd(false);
        Cursor res = mdbhelper.getUserData();
        while (res.moveToNext()){
            int userID = res.getInt(res.getColumnIndex("userID"));
            int distributorID = res.getInt(res.getColumnIndex("distributorID"));
            int userCode = res.getInt(res.getColumnIndex("userCode"));
            int agentID = res.getInt(res.getColumnIndex("agentID"));
            String lastName = res.getString(res.getColumnIndex("lastName"));
            String firstName = res.getString(res.getColumnIndex("firstName"));
            String phoneNumber = res.getString(res.getColumnIndex("phoneNumber"));
            String email = res.getString(res.getColumnIndex("email"));
            String locationGPS = res.getString(res.getColumnIndex("locationGPS"));
            String address1 = res.getString(res.getColumnIndex( "address1"));
            String address2 = res.getString(res.getColumnIndex( "address2"));
            String city = res.getString(res.getColumnIndex( "city"));
            String state = res.getString(res.getColumnIndex( "state"));
            String country = res.getString(res.getColumnIndex( "country"));
            String postCode = res.getString(res.getColumnIndex( "postCode"));

            UserData userData = new UserData(userID,distributorID,userCode,agentID,lastName,firstName,phoneNumber, email, locationGPS, address1, address2, city, state, country, postCode );
            userDataList.add(userData);
        }

        userAdapter.notifyList(userDataList);

    }

    @Override
    public void onMclick(int position) {
        UserData userData = userDataList.get(position);
        ((HomeActivity) getActivity()).setUserPayAccountFragment(String.valueOf(userData.getUserID()));
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
//        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBus event) {
        /* Do something */
    }

    public void updatedata() {
        userAdapter = null;
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
