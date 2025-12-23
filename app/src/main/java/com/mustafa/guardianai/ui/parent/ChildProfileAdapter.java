package com.mustafa.guardianai.ui.parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.ChildProfile;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for Child Profiles
 * Displays child profile cards in the dashboard
 */
public class ChildProfileAdapter extends RecyclerView.Adapter<ChildProfileAdapter.ChildProfileViewHolder> {
    private List<ChildProfile> childProfiles;
    private OnChildProfileClickListener listener;

    public interface OnChildProfileClickListener {
        void onChildProfileClick(ChildProfile profile);
    }

    public ChildProfileAdapter(OnChildProfileClickListener listener) {
        this.childProfiles = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChildProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child_profile_card, parent, false);
        return new ChildProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildProfileViewHolder holder, int position) {
        ChildProfile profile = childProfiles.get(position);
        holder.bind(profile);
    }

    @Override
    public int getItemCount() {
        return childProfiles.size();
    }

    public void updateChildProfiles(List<ChildProfile> profiles) {
        this.childProfiles = profiles != null ? profiles : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ChildProfileViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChildName;
        private TextView tvStatus;
        private TextView tvDeviceInfo;
        private TextView tvScreenTime;
        private TextView tvScreenTimeLimit;
        private CircularProgressIndicator progressIndicator;

        public ChildProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDeviceInfo = itemView.findViewById(R.id.tvDeviceInfo);
            tvScreenTime = itemView.findViewById(R.id.tvScreenTime);
            tvScreenTimeLimit = itemView.findViewById(R.id.tvScreenTimeLimit);
            progressIndicator = itemView.findViewById(R.id.progressIndicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onChildProfileClick(childProfiles.get(position));
                }
            });
        }

        public void bind(ChildProfile profile) {
            tvChildName.setText(profile.getName());
            
            // Set status (Online/Offline)
            if (profile.isOnline()) {
                tvStatus.setText("Online");
                tvStatus.setBackgroundResource(R.drawable.status_online_background);
            } else {
                long lastSeen = profile.getLastSeen();
                if (lastSeen > 0) {
                    long diff = System.currentTimeMillis() - lastSeen;
                    long hours = diff / (1000 * 60 * 60);
                    tvStatus.setText("Last seen " + hours + "h ago");
                } else {
                    tvStatus.setText("Offline");
                }
                tvStatus.setBackgroundColor(itemView.getContext().getColor(android.R.color.darker_gray));
            }
            
            // Set device info
            String deviceInfo = profile.getDeviceName();
            if (profile.getCurrentLocation() != null && !profile.getCurrentLocation().isEmpty()) {
                deviceInfo += " • " + profile.getCurrentLocation();
            }
            tvDeviceInfo.setText(deviceInfo);
            
            // Set screen time
            tvScreenTime.setText(profile.getFormattedScreenTime());
            tvScreenTimeLimit.setText("/ " + profile.getFormattedScreenTimeLimit());
            
            // Update circular progress indicator
            int percentage = profile.getScreenTimePercentage();
            if (progressIndicator != null) {
                progressIndicator.setProgress(percentage, true);
            }
        }
    }
}

