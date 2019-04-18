package com.expensemanager.app.tripsheet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.expensemanager.app.R;
import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.models.BusDailySummary;
import com.expensemanager.app.models.User;
import com.expensemanager.app.models.User;
import com.expensemanager.app.service.enums.EIcon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class BusDailySummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = BusDailySummaryAdapter.class.getSimpleName();

    public static final String NO_CONDUCTOR_ID = "Unknown Conductor";

    private static final int VIEW_TYPE_DEFAULT = 0;
    private ArrayList<BusDailySummary> busdailysummaries;
    private Context context;
    private boolean showMember;
    private boolean isBackgroundPrimary = true;

    public BusDailySummaryAdapter(Context context, ArrayList<BusDailySummary> busdailysummaries) {
        this.context = context;
        this.busdailysummaries = busdailysummaries;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {

        return this.busdailysummaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_DEFAULT:
                View view = inflater.inflate(R.layout.busdailysummary_item_constrained, parent, false);
                viewHolder = new ViewHolderDefault(view);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.busdailysummary_item_constrained, parent, false);
                viewHolder = new ViewHolderDefault(defaultView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_DEFAULT:
                ViewHolderDefault viewHolderDefault = (ViewHolderDefault) viewHolder;
                configureViewHolderDefault(viewHolderDefault, position);
                break;
            default:
                break;
        }
    }

    private void configureViewHolderDefault(ViewHolderDefault viewHolder, int position) {

        viewHolder.dividerView.setVisibility(View.VISIBLE);

        BusDailySummary busdailysummary = busdailysummaries.get(position);

        viewHolder.spentAtTextView.setText(Helpers.formatCreateAt(busdailysummary.getSummaryDate()));
        viewHolder.amountTextView.setText(Helpers.doubleToCurrency(busdailysummary.getTotalCollection()));


        if (busdailysummary.isApproved()) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#77dd77"));
            viewHolder.approvedColorImageView.setImageDrawable(colorDrawable);

        } else {
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#cfcfc4"));
            viewHolder.approvedColorImageView.setImageDrawable(colorDrawable);
        }

//         Set item click listener
        viewHolder.viewDetailsButton.setOnClickListener(v -> {
            BusDailySummaryDetailActivity.newInstance(context, busdailysummaries.get(position).getId());
            ((Activity) getContext()).overridePendingTransition(R.anim.right_in, R.anim.stay);
        });
    }

    public void setShowMember(boolean showMember) {
        this.showMember = showMember;
        notifyDataSetChanged();
    }

    public void setIsBackgroundPrimary(boolean isBackgroundPrimary) {
        this.isBackgroundPrimary = isBackgroundPrimary;
    }

    public void clear() {
        busdailysummaries.clear();

        notifyDataSetChanged();
    }

    public void addAll(List<BusDailySummary> busdailysummaries) {
        this.busdailysummaries.addAll(busdailysummaries);
        notifyDataSetChanged();
    }

    public static class ViewHolderDefault extends RecyclerView.ViewHolder {
        @BindView(R.id.busdailysummary_item_default_spent_at_text_view_id)
        TextView spentAtTextView;
        @BindView(R.id.busdailysummary_item_default_amount_text_view_id)
        TextView amountTextView;

        @BindView(R.id.busdailysummary_item_default_view_id)
        View dividerView;
        @BindView(R.id.busdailysummary_item_default_conductor_name_text_view_id)
        TextView conductorNameTextView;
//        @BindView(R.id.busdailysummary_item_default_card_view_id)
//        CardView cardView;

        @BindView(R.id.busdailysummary_item_default_approved_color_image_view_id)
        CircleImageView approvedColorImageView;

        @BindView(R.id.busdailysummary_item_view_trip_details_button)
        Button viewDetailsButton;


        private View itemView;

        public ViewHolderDefault(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView = view;
        }
    }
}
