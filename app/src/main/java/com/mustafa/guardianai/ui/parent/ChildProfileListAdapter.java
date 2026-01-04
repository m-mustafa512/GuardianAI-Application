package com.mustafa.guardianai.ui.parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.ChildProfile;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for child profile list in management screen
 */
public class ChildProfileListAdapter extends RecyclerView.Adapter<ChildProfileListAdapter.ViewHolder> {
    private List<ChildProfile> childProfiles;
    private OnChildProfileClickListener clickListener;
    private OnChildProfileDeleteListener deleteListener;

    public interface OnChildProfileClickListener {
        void onChildProfileClick(ChildProfile profile);
    }

    public interface OnChildProfileDeleteListener {
        void onChildProfileDelete(ChildProfile profile);
    }

    public ChildProfileListAdapter(OnChildProfileClickListener clickListener,
                                   OnChildProfileDeleteListener deleteListener) {
        this.childProfiles = new ArrayList<>();
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child_profile_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChildName;
        private TextView tvAge;
        private TextView tvDeviceInfo;
        private TextView tvStatus;
        private ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvDeviceInfo = itemView.findViewById(R.id.tvDeviceInfo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onChildProfileClick(childProfiles.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onChildProfileDelete(childProfiles.get(position));
                }
            });
        }

        public void bind(ChildProfile profile) {
            tvChildName.setText(profile.getName());
            tvAge.setText(profile.getAge() + " Years Old");
            
            String deviceInfo = profile.getDeviceName();
            if (profile.getDeviceType() != null) {
                deviceInfo += " (" + profile.getDeviceType() + ")";
            }
            tvDeviceInfo.setText(deviceInfo);
            
            if (profile.isOnline()) {
                tvStatus.setText("Online");
                tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
            } else {
                tvStatus.setText("Offline");
                tvStatus.setTextColor(itemView.getContext().getColor(android.R.color.darker_gray));
            }
        }
    }
}









