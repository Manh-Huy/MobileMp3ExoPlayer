package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private List<String> userNames;
    private Context mContext;

    public ParticipantAdapter(Context context, List<String> userNames) {
        this.mContext = context;
        this.userNames = userNames != null ? userNames : new ArrayList<>();
    }

    public void setData(List<String> userNames) {
        this.userNames = userNames != null ? userNames : new ArrayList<>();
        LogUtils.ApplicationLogE("ParticipantAdapter" + "userNames size: " + this.userNames.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = userNames.get(position);
        LogUtils.ApplicationLogE("ParticipantAdapter " + "Binding user at position: " + position + " - " + username);
        holder.tvUserName.setText(username);
        holder.btnKick.setOnClickListener(v -> {
            ErrorUtils.showError(mContext, "User " + username + " kicked");
        });
    }

    @Override
    public int getItemCount() {
        LogUtils.ApplicationLogE("ParticipantAdapter "+ "getItemCount: " + userNames.size());
        return userNames.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final Button btnKick;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_participant_name);
            btnKick = itemView.findViewById(R.id.btn_kick);
        }
    }
}
