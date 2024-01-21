package com.example.uas_mobile.ui.view.adapter;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uas_mobile.R;
import com.example.uas_mobile.utils.ProfileItem;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private List<ProfileItem> items;
    private OnItemClickListener listener;

    public ProfileAdapter(List<ProfileItem> items) {
        this.items = items;
    }

    public interface OnItemClickListener {
        void onItemClick(ProfileItem item, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProfileItem item = items.get(position);
        holder.itemName.setText(item.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Log.d("ProfileAdapter", "Item clicked: " + item.getTitle());
            if (listener != null) {
                listener.onItemClick(item, position);
            }
        });

        // Handle the visibility of the subItems based on the isExpanded flag
        holder.subItemLayout.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
        if (item.isExpanded()) {
            // Populate the subItems
            holder.subItemLayout.removeAllViews();
            for (String subItem : item.getSubItems()) {
                TextView subItemView = new TextView(holder.itemView.getContext());
                subItemView.setText(subItem);
                subItemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Set text size to 14sp
                subItemView.setPadding(0, 8, 0, 8); // Set top and bottom padding to 8dp
                holder.subItemLayout.addView(subItemView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public LinearLayout subItemLayout;

        public ViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.item_name);
            subItemLayout = view.findViewById(R.id.sub_item_layout);
        }
    }
}