package com.example.spacewar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<PlayerCard> players;
    private List<Integer> MedalResId;

    public PlayerAdapter(List<PlayerCard> players) {
        this.players = players;
        this.MedalResId = new ArrayList<>();

        MedalResId.add(R.drawable.ic_first);
        MedalResId.add(R.drawable.ic_second_prize);
        MedalResId.add(R.drawable.ic_third_prize);
        MedalResId.add(R.drawable.ic_four);
        MedalResId.add(R.drawable.ic_five);
        MedalResId.add(R.drawable.ic_six);
        MedalResId.add(R.drawable.ic_seven);
        MedalResId.add(R.drawable.ic_eight);
        MedalResId.add(R.drawable.ic_nine);
        MedalResId.add(R.drawable.ic_ten);
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_cell,parent,false);
        PlayerViewHolder playerViewHolder = new PlayerViewHolder(view);
        return playerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        PlayerCard player = players.get(position);
        holder.name.setText(player.get_Name());
        if(player.get_Score() == 0)
        {
            holder.score.setText("-");
        }
        else{
            holder.score.setText(player.get_Score()+"");
        }
        holder.medal.setImageResource(MedalResId.get(position));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView score;
        ImageView medal;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Player_name_text_view);
            score = itemView.findViewById(R.id.Player_score_text_view);
            medal = itemView.findViewById(R.id.medal_icon);
        }
    }
}
