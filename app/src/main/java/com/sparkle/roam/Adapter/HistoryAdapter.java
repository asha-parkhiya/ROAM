package com.sparkle.roam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sparkle.roam.Model.PayEventSync.PayEventData;
import com.sparkle.roam.R;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PayEventData getpayEventListQuery1 = getpayEventListQueryLists.get(position);
        if (getpayEventListQuery1.getEventType().equals("payCollect")){
            holder.title.setText("Amount : " + getpayEventListQuery1.getPayRecordAmt());
            holder.tv_date.setText("Date : " + getpayEventListQuery1.getPayEventDate());
        }else {
            holder.title.setText("Code : " + getpayEventListQuery1.getCodeIssued());
            holder.tv_date.setText("Date : " + getpayEventListQuery1.getPayEventDate());
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
            title = (TextView) itemView.findViewById(R.id.tv_fname);
            tv_date = (TextView) itemView.findViewById(R.id.tv_lname);
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
