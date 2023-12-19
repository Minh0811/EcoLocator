package com.khaiminh.ecolocator.Controllers.UserActivities.SiteDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.khaiminh.ecolocator.Models.Participant;
import com.khaiminh.ecolocator.R;

import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private List<Participant> participantList;

    public ParticipantsAdapter(List<Participant> participantList) {
        this.participantList = participantList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Participant participant = participantList.get(position);
        holder.tvName.setText(participant.getName());
        holder.tvEmail.setText(participant.getEmail());
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvParticipantName);
            tvEmail = itemView.findViewById(R.id.tvParticipantEmail);
        }
    }
}

