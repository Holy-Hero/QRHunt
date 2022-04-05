package com.bigyoshi.qrhunt.bottom_navigation.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.player.Player;

import java.util.List;
import java.util.Objects;

// yes, i know ideally we are supposed to use objects blag lah la jglksjdfldsf
public class LeaderboardListAdapter extends ArrayAdapter<Player> {
    private final String playerId;

    public LeaderboardListAdapter(@NonNull Context context, int resource, @NonNull List<Player> objects, String playerId) {
        super(context, resource, objects);
        this.playerId = playerId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_list_content, parent, false);
        }

        Player player = getItem(position);
        if (Objects.equals(player.getPlayerId(), playerId)) {
            convertView.setBackgroundResource(R.drawable.rec_list_item_highlight);
        } else {
            convertView.setBackgroundResource(R.drawable.rec_list_item);
        }

        TextView rank = (TextView) convertView.findViewById(R.id.rank_num);
        rank.setText(String.valueOf(position + 4));

        TextView username = (TextView) convertView.findViewById(R.id.rank_username);
        username.setText(player.getUsername());
        TextView bestUnique = (TextView) convertView.findViewById(R.id.rank_highest_unique);
        bestUnique.setText(String.valueOf(player.getBestUniqueQr().getScore()));
        TextView numScanned = (TextView) convertView.findViewById(R.id.rank_num_scans);
        numScanned.setText(String.valueOf(player.getNumScanned()));
        TextView totalScore = (TextView) convertView.findViewById(R.id.rank_score);
        totalScore.setText(String.valueOf(player.getTotalScore()));
        return convertView;
    }
}
