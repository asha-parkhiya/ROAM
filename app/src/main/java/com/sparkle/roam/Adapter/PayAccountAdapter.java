package com.sparkle.roam.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sparkle.roam.Displaymodel.DisplayPayAccount;
import com.sparkle.roam.Fragments.PayAccountDetailFragment;
import com.sparkle.roam.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PayAccountAdapter extends RecyclerView.Adapter<PayAccountAdapter.ViewHolder> {

    Context context;
    PayAccountAdapter.OnMclick onMclick;
    List<DisplayPayAccount> allPostList;
    List<DisplayPayAccount> postList;
    String agentAssignment;

    PayAccountAdapter.OnButtonClick onButtonClick;

    public interface  OnButtonClick{
        void onbclick(int btn_number,int position,String agentAssignment);
    }

    public interface OnMclick {
        void onMclick(int position);
    }

    public PayAccountAdapter(Context context, List<DisplayPayAccount> postList, PayAccountAdapter.OnButtonClick onMclick) {
        this.context = context;
        this.onButtonClick = onMclick;
        this.postList = postList;
        allPostList = new ArrayList<>();
        this.allPostList.addAll(postList);
    }

    public void notifyList(List<DisplayPayAccount> getPayAccountLists) {
        this.postList = getPayAccountLists;
        notifyDataSetChanged();
    }

//    public void filter(String searchQuery) {
//        allPostList.clear();
//        allPostList.addAll(postList);
//        postList.clear();
//        if (searchQuery.isEmpty())
//            postList.addAll(allPostList);
//        else {
//            for (DisplayPayAccount getPayAccountListQuery : allPostList) {
//                if (Objects.requireNonNull(getPayAccountListQuery.getFirstName()).toLowerCase().contains(searchQuery) || Objects.requireNonNull(getPayAccountListQuery.getLastName()).toLowerCase().contains(searchQuery))
//                    postList.add(getPayAccountListQuery);
//            }
//        }
//        notifyDataSetChanged();
//
//    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payaccount, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_account_expand_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisplayPayAccount payAccountData = postList.get(position);
        holder.tv_fname.setText(payAccountData.getFirstName()+" "+payAccountData.getLastName());
        holder.tv_fname.setTypeface(holder.tv_fname.getTypeface(), Typeface.BOLD);
        String fname = payAccountData.getFirstName().toString();
        char firstletter = fname.charAt(0);

        holder.tv_lname.setText("PPID : "+payAccountData.getProductItemPAYGSN());

//        getColor(String.valueOf(firstletter).toUpperCase(), holder.cv_color);

        agentAssignment = payAccountData.getAgentAssignment();

        ArrayList<String> codes1 = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(agentAssignment);
            JSONArray jsonArray = jsonObject.getJSONArray("assignedCodes");
            for (int i = 0; i < jsonArray.length(); i++) {
                codes1.add(jsonArray.getJSONObject(i).getString("otpHashFormatted"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        holder.tv_firstname.setText(payAccountData.getFirstName());
//        holder.tv_lastname.setText(payAccountData.getLastName());
        holder.tv_leftday.setText(String.valueOf(codes1.size()));
//        holder.tv_accountProduct.setText(payAccountData.getProductItemPAYGSN());
        holder.tv_payoffAmt.setText(String.valueOf(payAccountData.getPayoffAmt()));
        holder.tv_minPayDays.setText(String.valueOf(payAccountData.getMinPayDays()));
        holder.tv_maxPayDays.setText(String.valueOf(payAccountData.getMaxPayDays()));
        holder.tv_depositDays.setText(String.valueOf(payAccountData.getDepositDays()));
        holder.tv_schPayDays.setText(String.valueOf(payAccountData.getSchPayDays()));
        holder.tv_initialCreditDays.setText(String.valueOf(payAccountData.getInitialCreditDays()));
        holder.tv_receivedPayAmt.setText(String.valueOf(payAccountData.getReceivedPayAmt()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fname, tv_lname, tv_phone, tv_letter;
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
        CircleImageView cv_color;
        RelativeLayout rl_image;
        Button btn_payeventhistory,btn_issuecode,btn_addpayevent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_fname = (TextView) itemView.findViewById(R.id.tv_fname);
            tv_lname = (TextView) itemView.findViewById(R.id.tv_lname);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
//            tv_firstname = (TextView) itemView.findViewById(R.id.tv_firstname);
//            tv_lastname = (TextView) itemView.findViewById(R.id.tv_lastname);
            tv_leftday = (TextView) itemView.findViewById(R.id.tv_leftday);
//            tv_accountProduct = (TextView) itemView.findViewById(R.id.tv_productitem);
            tv_payoffAmt = (TextView) itemView.findViewById(R.id.tv_payoffamount);
            tv_minPayDays = (TextView) itemView.findViewById(R.id.tv_minpaydays);
            tv_maxPayDays = (TextView) itemView.findViewById(R.id.tv_maxpaydays);
            tv_depositDays = (TextView) itemView.findViewById(R.id.tv_depositedays);
            tv_schPayDays = (TextView) itemView.findViewById(R.id.tv_schpaydays);
            tv_initialCreditDays = (TextView) itemView.findViewById(R.id.tv_initialcreditdays);
            tv_receivedPayAmt = (TextView) itemView.findViewById(R.id.tv_receivepay);

            btn_addpayevent = itemView.findViewById(R.id.btn_addpayevent);
            btn_issuecode = itemView.findViewById(R.id.btn_issuecode);
            btn_payeventhistory = itemView.findViewById(R.id.btn_payeventhistory);

            btn_addpayevent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onButtonClick!=null){
                        onButtonClick.onbclick(300,getAdapterPosition(),"");
                    }
                }
            });

            btn_issuecode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onButtonClick!=null){
                        onButtonClick.onbclick(200,getAdapterPosition(),agentAssignment);
                    }
                }
            });

            btn_payeventhistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onButtonClick!=null){
                        onButtonClick.onbclick(100,getAdapterPosition(),"");
                    }
                }
            });
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onMclick.onMclick(getAdapterPosition());
//                }
//            });
        }
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

}
