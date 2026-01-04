package com.mustafa.guardianai.ui.parent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.data.model.Alert;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for Alerts
 * Displays alert cards in the alerts fragment
 */
public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {
    private List<Alert> alerts;
    private String currentDateSection = "";

    public AlertsAdapter() {
        this.alerts = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Alert alert = alerts.get(position);
        
        // Check if we need to show date section header
        String dateSection = getDateSection(alert.getCreatedAt());
        if (!dateSection.equals(currentDateSection)) {
            currentDateSection = dateSection;
            holder.tvDateSection.setVisibility(View.VISIBLE);
            holder.tvDateSection.setText(dateSection);
        } else {
            holder.tvDateSection.setVisibility(View.GONE);
        }
        
        holder.bind(alert);
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public void updateAlerts(List<Alert> alerts) {
        this.alerts = alerts != null ? alerts : new ArrayList<>();
        currentDateSection = "";
        notifyDataSetChanged();
    }

    /**
     * Get date section (TODAY, YESTERDAY, or date)
     */
    private String getDateSection(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        long days = diff / (1000 * 60 * 60 * 24);
        
        if (days == 0) {
            return "TODAY";
        } else if (days == 1) {
            return "YESTERDAY";
        } else {
            // Format date
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp)).toUpperCase();
        }
    }

    class AlertViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateSection;
        private View iconContainer;
        private TextView tvAlertTitle;
        private TextView tvAlertMessage;
        private TextView tvAlertTime;
        private View vUnreadDot;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateSection = itemView.findViewById(R.id.tvDateSection);
            iconContainer = itemView.findViewById(R.id.iconContainer);
            tvAlertTitle = itemView.findViewById(R.id.tvAlertTitle);
            tvAlertMessage = itemView.findViewById(R.id.tvAlertMessage);
            tvAlertTime = itemView.findViewById(R.id.tvAlertTime);
            vUnreadDot = itemView.findViewById(R.id.vUnreadDot);
        }

        public void bind(Alert alert) {
            tvAlertTitle.setText(alert.getTitle());
            tvAlertMessage.setText(alert.getMessage());
            tvAlertTime.setText(alert.getTimeAgo());
            
            // Set icon based on alert type
            int iconRes = getIconForAlertType(alert.getType());
            int iconColor = getColorForAlertType(alert.getType());
            if (iconContainer instanceof android.widget.ImageView) {
                ((android.widget.ImageView) iconContainer).setImageResource(iconRes);
                ((android.widget.ImageView) iconContainer).setColorFilter(iconColor);
            }
            
            // Show/hide unread dot
            vUnreadDot.setVisibility(alert.isRead() ? View.GONE : View.VISIBLE);
        }

        private int getIconForAlertType(Alert.AlertType type) {
            switch (type) {
                case GEO_FENCE_BREACH:
                    return android.R.drawable.ic_dialog_map;
                case TIME_LIMIT_REACHED:
                    return android.R.drawable.ic_menu_recent_history;
                case NEW_APP_INSTALL:
                    return android.R.drawable.ic_menu_upload;
                case LOW_BATTERY:
                    return android.R.drawable.ic_menu_info_details;
                case WEEKLY_REPORT:
                    return android.R.drawable.ic_menu_view;
                default:
                    return android.R.drawable.ic_dialog_info;
            }
        }

        private int getColorForAlertType(Alert.AlertType type) {
            switch (type) {
                case GEO_FENCE_BREACH:
                    return itemView.getContext().getColor(R.color.status_alert);
                case TIME_LIMIT_REACHED:
                    return itemView.getContext().getColor(R.color.status_warning);
                case NEW_APP_INSTALL:
                    return itemView.getContext().getColor(R.color.primary);
                case LOW_BATTERY:
                    return itemView.getContext().getColor(R.color.status_warning);
                case WEEKLY_REPORT:
                    return itemView.getContext().getColor(R.color.text_secondary);
                default:
                    return itemView.getContext().getColor(R.color.text_secondary);
            }
        }
    }
}








