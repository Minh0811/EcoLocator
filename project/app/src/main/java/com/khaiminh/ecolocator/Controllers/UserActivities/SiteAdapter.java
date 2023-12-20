package com.khaiminh.ecolocator.Controllers.UserActivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.khaiminh.ecolocator.Models.Site; // Ensure you have a Site model class
import com.khaiminh.ecolocator.R;

import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.SiteViewHolder> {

    private List<Site> sites;

    public SiteAdapter(List<Site> sites) {
        this.sites = sites;
    }

    @Override
    public SiteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sites, parent, false);
        return new SiteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SiteViewHolder holder, int position) {
        Site site = sites.get(position);
        holder.bind(site);
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    public void updateSites(List<Site> newSites) {
        sites = newSites;
        notifyDataSetChanged();
    }

    static class SiteViewHolder extends RecyclerView.ViewHolder {
        // UI components from item_sites.xml
        private TextView nameTextView, descriptionTextView, additionalInfoTextView;

        public SiteViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewChallengeName);
            descriptionTextView = itemView.findViewById(R.id.textViewChallengeDescription);
            additionalInfoTextView = itemView.findViewById(R.id.textViewChallengeDifficulty);
        }

        public void bind(Site site) {
            nameTextView.setText(site.getName());
            descriptionTextView.setText(site.getDescription());
            additionalInfoTextView.setText(site.getAdditionalInfo());
        }
    }
}