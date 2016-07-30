package com.afollestad.nocknock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.nocknock.R;
import com.afollestad.nocknock.api.ServerModel;
import com.afollestad.nocknock.api.ServerStatus;
import com.afollestad.nocknock.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerVH> {

    private final Object LOCK = new Object();
    private ArrayList<ServerModel> mServers;

    public ServerAdapter() {
        mServers = new ArrayList<>(2);
    }

    public void add(ServerModel model) {
        mServers.add(model);
        notifyItemInserted(mServers.size() - 1);
    }

    public void update(int index, ServerModel model) {
        mServers.set(index, model);
        notifyItemChanged(index);
    }

    public void update(ServerModel model) {
        synchronized (LOCK) {
            for (int i = 0; i < mServers.size(); i++) {
                if (mServers.get(i).id == model.id) {
                    update(i, model);
                    break;
                }
            }
        }
    }

    public void remove(int index) {
        mServers.remove(index);
        notifyItemRemoved(index);
    }

    public void remove(ServerModel model) {
        synchronized (LOCK) {
            for (int i = 0; i < mServers.size(); i++) {
                if (mServers.get(i).id == model.id) {
                    remove(i);
                    break;
                }
            }
        }
    }

    public void set(ServerModel[] models) {
        mServers = new ArrayList<>(models.length);
        Collections.addAll(mServers, models);
        notifyDataSetChanged();
    }

    @Override
    public ServerAdapter.ServerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_server, parent, false);
        return new ServerVH(v);
    }

    @Override
    public void onBindViewHolder(ServerAdapter.ServerVH holder, int position) {
        final ServerModel model = mServers.get(position);

        holder.textName.setText(model.name);
        holder.textUrl.setText(model.url);

        switch (model.status) {
            case ServerStatus.OK:
                holder.textStatus.setText(R.string.everything_checks_out);
                holder.iconStatus.setImageResource(R.drawable.green_circle);
                break;
            case ServerStatus.WAITING:
                holder.textStatus.setText(R.string.waiting);
                holder.iconStatus.setImageResource(R.drawable.yellow_circle);
                break;
            case ServerStatus.CHECKING:
                holder.textStatus.setText(R.string.checking_status);
                holder.iconStatus.setImageResource(R.drawable.yellow_circle);
                break;
            case ServerStatus.ERROR:
                holder.textStatus.setText(R.string.something_wrong);
                holder.iconStatus.setImageResource(R.drawable.red_circle);
                break;
        }

        final long now = System.currentTimeMillis();
        final long nextCheck = model.lastCheck + model.checkInterval;
        final long difference = nextCheck - now;
        holder.textInterval.setText(TimeUtil.str(difference));
    }

    @Override
    public int getItemCount() {
        return mServers.size();
    }

    public static class ServerVH extends RecyclerView.ViewHolder {

        final ImageView iconStatus;
        final TextView textName;
        final TextView textInterval;
        final TextView textUrl;
        final TextView textStatus;

        public ServerVH(View itemView) {
            super(itemView);
            iconStatus = (ImageView) itemView.findViewById(R.id.iconStatus);
            textName = (TextView) itemView.findViewById(R.id.textName);
            textInterval = (TextView) itemView.findViewById(R.id.textInterval);
            textUrl = (TextView) itemView.findViewById(R.id.textUrl);
            textStatus = (TextView) itemView.findViewById(R.id.textStatus);
        }
    }
}