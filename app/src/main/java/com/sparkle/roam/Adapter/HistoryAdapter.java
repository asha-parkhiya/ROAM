package com.sparkle.roam.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sparkle.roam.Model.PayEventSync.PayEventData;
import com.sparkle.roam.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    OnItem onItem;

    public interface OnItem {
        void onItemClick(int position);
    }

    List<PayEventData> getpayEventListQueryLists;
    Context context;

    public HistoryAdapter(Context context, List<PayEventData> getpayEventListQueryLists, OnItem onItem) {
        this.context = context;
        this.getpayEventListQueryLists = getpayEventListQueryLists;
        this.onItem = onItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payaccount, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayEventData getpayEventListQuery1 = getpayEventListQueryLists.get(position);
        String date = getpayEventListQuery1.getPayEventDate();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Date newDate= null;
        try {
            newDate = spf.parse(date);
            spf= new SimpleDateFormat("MM/dd/yyyy HH:mm");
            date = spf.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (getpayEventListQuery1.getEventType().equals("payCollect")){
            holder.title.setText("Pay Received : " + getpayEventListQuery1.getPayRecordAmt()+" pesos");
            holder.tv_date.setText(date+"  PayAccID : "+getpayEventListQuery1.getPayAccountID());
        }else {
            holder.title.setText("Code Iussed : " + getpayEventListQuery1.getCodeIssued());
            holder.tv_date.setText(date+"  PayAccID : "+getpayEventListQuery1.getPayAccountID());
        }
    }

    @Override
    public int getItemCount() {
        return getpayEventListQueryLists.size();
    }

    public void notifyList(List<PayEventData> getpayEventListQueryLists) {
        this.getpayEventListQueryLists = getpayEventListQueryLists;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, tv_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_fname);
            title = (TextView) itemView.findViewById(R.id.tv_lname);
//            title.setText("Pay Event Date");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItem != null)
                        onItem.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
