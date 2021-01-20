package com.daniel.flappybird;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankActivityAdapter extends RecyclerView.Adapter<RankActivityAdapter.MyViewHolder>{

    private Context mContext ;
    private List<UserHelperClass> mData ;

    public RankActivityAdapter(Context mContext, List<UserHelperClass> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    @Override
    public RankActivityAdapter.MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.score_rank_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankActivityAdapter.MyViewHolder holder, int position) {
        UserHelperClass currentuser = mData.get(position);


        holder.score_rec.setText(currentuser.getScore()+"");
        holder.username_rec.setText(currentuser.getUsername()+"");

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username_rec;
        TextView score_rec;

        public MyViewHolder(View itemView) {
            super(itemView);
            username_rec = (TextView) itemView.findViewById(R.id.username_rec) ;
            score_rec=(TextView) itemView.findViewById(R.id.score_rec);

        }
    }
}
