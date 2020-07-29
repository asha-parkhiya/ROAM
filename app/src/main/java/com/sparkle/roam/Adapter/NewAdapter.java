package com.sparkle.roam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.GetpayEventListQuery;
import com.sparkle.roam.ContentProvider.ToDo;
import com.sparkle.roam.R;

import java.util.ArrayList;
import java.util.List;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.ViewHolder> {

    List<ToDo> toDos;
    private List<ToDo> allscoreList = new ArrayList<>();
    Context context;

    public NewAdapter(List<ToDo> toDos, Context context, onItemClickListener listener) {
        this.toDos = toDos;
        this.context = context;
        allscoreList = new ArrayList<>();
        allscoreList.addAll(toDos);
        this.listener = listener;
    }

    public interface onItemClickListener{
        void changevalue(String toDo, int position);
    }
    private onItemClickListener listener;

    public void notifyList(List<ToDo> getPayAccountLists) {
        this.allscoreList = getPayAccountLists;
        notifyDataSetChanged();
    }

    public void filter(String charText) {

        int position = 0;
        toDos = new ArrayList<>();
        if (charText.length() == 0) {
            allscoreList.addAll(toDos);
        } else {
            for (ToDo wp : allscoreList) {
                position = position + 1;
                if (String.valueOf(wp.getPPID()).equals(charText)) {
                    toDos.add(wp);
                    listener.changevalue(String.valueOf(wp.getPPID()),position);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new NewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ToDo toDo = allscoreList.get(position);
        holder.ppid.setText(String.valueOf(toDo.getPPID()));
        holder.code.setText(toDo.getCodehasformate());
        holder.device.setText(toDo.getType());
    }

    @Override
    public int getItemCount() {
        return allscoreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ppid, code,device;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ppid = (TextView) itemView.findViewById(R.id.ppid);
            code = (TextView) itemView.findViewById(R.id.code);
            device = (TextView) itemView.findViewById(R.id.device);
        }
    }
}
