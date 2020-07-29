package com.sparkle.roam.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.amazonaws.amplify.generated.graphql.GetUserPayaccountListQuery;
import com.bumptech.glide.Glide;
import com.sparkle.roam.Displaymodel.DisplayPayAccount;
import com.sparkle.roam.Model.SelectPayAcc;
import com.sparkle.roam.Model.SyncData.PayAccountData;
import com.sparkle.roam.R;
import com.sparkle.roam.Utils.Constants;
import com.sparkle.roam.Utils.ItemAnimation;
import com.sparkle.roam.Utils.MyPref;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPayAccountAdapter extends Adapter<UserPayAccountAdapter.ViewHolder> {

    Context context;
    OnMclick onMclick;
    List<DisplayPayAccount> allPostList;
    List<DisplayPayAccount> postList;
    private boolean isLongPressed;
    private boolean eventbus = false;
    MyPref myPref;


    public UserPayAccountAdapter(Context context, List<DisplayPayAccount> postList, OnMclick onMclick) {
        this.context = context;
        this.onMclick = onMclick;
        this.postList = postList;
        allPostList = new ArrayList<>();
        this.allPostList.addAll(postList);
        myPref = new MyPref(context);
    }

//    public void updateCode(String code) {
//
//        eventbus = true;
//        myPref.setPref(Constants.REMAINING_CODE,code);
//        notifyDataSetChanged();
//    }

    public interface OnMclick {
        void onMclick(int position);
        void OnItemClick(SelectPayAcc selectPayAcc,GetUserPayaccountListQuery.GetUserPayAccountListQuery getUserPayAccountListQuery,int position);
    }

    public void showCheckbox(){
        isLongPressed = true;
        notifyDataSetChanged();  // Required for update
    }

    public void hideCheckbox(){
        isLongPressed = false;
        notifyDataSetChanged();  // Required for update
    }

    public void offlinesearch(String searchQuery) {
        allPostList.clear();
        allPostList.addAll(postList);
        postList.clear();
        if (searchQuery.isEmpty())
            postList.addAll(allPostList);
        else {
            for (DisplayPayAccount getPayAccountListQuery : allPostList) {
//                if (Objects.requireNonNull(getPayAccountListQuery.payAccountID()).toLowerCase().contains(searchQuery))
//                    postList.add(getPayAccountListQuery);
            }
        }
        notifyDataSetChanged();
    }

    public void notifyList(List<DisplayPayAccount> getPayAccountLists) {
        this.postList = getPayAccountLists;
        notifyDataSetChanged();
    }

    public void setOnMclick(OnMclick onMclick) {
        this.onMclick = onMclick;
    }

    @NonNull
    @Override
    public UserPayAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userpayaccount, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull UserPayAccountAdapter.ViewHolder holder, int position) {
        DisplayPayAccount payAccountData = postList.get(position);
//        ViewHolder view = (ViewHolder) holder;
        ArrayList<String> codes = new ArrayList<>();
        String assignAgent = "";

            holder.tv_fname.setText(String.valueOf(payAccountData.getProductItemPAYGSN()));

        if(!eventbus) {
                assignAgent = payAccountData.getAgentAssignment();
                try {
                    JSONObject jsonObject = new JSONObject(assignAgent);
                    JSONArray jsonArray = jsonObject.getJSONArray("assignedCodes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        codes.add(String.valueOf(jsonArray.getJSONObject(i).getString("otpHashFormatted")));
                    }
                    holder.tv_lname.setText(String.valueOf("Total number of days : "+codes.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }

    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, ItemAnimation.FADE_IN);
            lastPosition = position;
        }
    }

    public int makeColor(int R, int G, int B) {
        return Color.argb(255, R, G, B);
    }

    public void getColor(String alphabet, CircleImageView cv_color) {

        if (alphabet.equals("A")) {
            Glide.with(context).load(R.drawable.ic_a).into(cv_color);
        } else if (alphabet.equals("B")) {
            Glide.with(context).load(R.drawable.ic_b).into(cv_color);
        } else if (alphabet.equals("C")) {
            Glide.with(context).load(R.drawable.ic_c).into(cv_color);
        } else if (alphabet.equals("D")) {
            Glide.with(context).load(R.drawable.ic_d).into(cv_color);
        } else if (alphabet.equals("E")) {
            Glide.with(context).load(R.drawable.ic_e).into(cv_color);
        } else if (alphabet.equals("F")) {
            Glide.with(context).load(R.drawable.ic_f).into(cv_color);
        } else if (alphabet.equals("G")) {
            Glide.with(context).load(R.drawable.ic_g).into(cv_color);
        } else if (alphabet.equals("H")) {
            Glide.with(context).load(R.drawable.ic_h).into(cv_color);
        } else if (alphabet.equals("I")) {
            Glide.with(context).load(R.drawable.ic_i).into(cv_color);
        } else if (alphabet.equals("J")) {
            Glide.with(context).load(R.drawable.ic_j).into(cv_color);
        } else if (alphabet.equals("K")) {
            Glide.with(context).load(R.drawable.ic_k).into(cv_color);
        } else if (alphabet.equals("L")) {
            Glide.with(context).load(R.drawable.ic_l).into(cv_color);
        } else if (alphabet.equals("M")) {
            Glide.with(context).load(R.drawable.ic_m).into(cv_color);
        } else if (alphabet.equals("N")) {
            Glide.with(context).load(R.drawable.ic_n).into(cv_color);
        } else if (alphabet.equals("O")) {
            Glide.with(context).load(R.drawable.ic_o).into(cv_color);
        } else if (alphabet.equals("P")) {
            Glide.with(context).load(R.drawable.ic_p).into(cv_color);
        } else if (alphabet.equals("Q")) {
            Glide.with(context).load(R.drawable.ic_q).into(cv_color);
        } else if (alphabet.equals("R")) {
            Glide.with(context).load(R.drawable.ic_r).into(cv_color);
        } else if (alphabet.equals("S")) {
            Glide.with(context).load(R.drawable.ic_s).into(cv_color);
        } else if (alphabet.equals("T")) {
            Glide.with(context).load(R.drawable.ic_t).into(cv_color);
        } else if (alphabet.equals("U")) {
            Glide.with(context).load(R.drawable.ic_u).into(cv_color);
        } else if (alphabet.equals("V")) {
            Glide.with(context).load(R.drawable.ic_v).into(cv_color);
        } else if (alphabet.equals("W")) {
            Glide.with(context).load(R.drawable.ic_w).into(cv_color);
        } else if (alphabet.equals("X")) {
            Glide.with(context).load(R.drawable.ic_x).into(cv_color);
        } else if (alphabet.equals("Y")) {
            Glide.with(context).load(R.drawable.ic_y).into(cv_color);
        } else if (alphabet.equals("Z")) {
            Glide.with(context).load(R.drawable.ic_z).into(cv_color);
        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fname, tv_lname, tv_phone, tv_letter;
        CircleImageView cv_color;
        RelativeLayout rl_image,rl_itemview, rl_check_box;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fname = (TextView) itemView.findViewById(R.id.tv_fname);
            tv_lname = (TextView) itemView.findViewById(R.id.tv_lname);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
//            tv_letter = (TextView) itemView.findViewById(R.id.tv_letter);
//            cv_color = itemView.findViewById(R.id.cv_color);
//            rl_image = itemView.findViewById(R.id.rl_image);
//            rl_image.setVisibility(View.VISIBLE);

            rl_itemview = itemView.findViewById(R.id.rl_itemview);
            rl_check_box = itemView.findViewById(R.id.rl_check_box);

            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    onMclick.onMclick(getAdapterPosition());
                }
            });
        }
    }

}
