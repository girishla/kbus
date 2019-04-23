package com.bigmantra.natco.overview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bigmantra.natco.R;
import com.bigmantra.natco.expense.ExpenseDetailActivity;
import com.bigmantra.natco.expense.ProfileExpenseActivity;
import com.bigmantra.natco.helpers.Helpers;
import com.bigmantra.natco.models.BusDailySummary;
import com.bigmantra.natco.models.Category;
import com.bigmantra.natco.models.Expense;
import com.bigmantra.natco.models.User;
import com.bigmantra.natco.service.enums.EIcon;
import com.bigmantra.natco.tripsheet.BusDailySummaryDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG= OverviewAdapter.class.getSimpleName();

    public static final String NO_CATEGORY_ID = "No Category";
    public static final String NO_CATEGORY_COLOR = "#BDBDBD";

    private static final int VIEW_TYPE_DEFAULT = 0;
    private ArrayList<BusDailySummary> summaries;
    private Context context;
    private boolean showMember;

    public OverviewAdapter(Context context, ArrayList<BusDailySummary> summaries) {
        this.context = context;
        this.summaries = summaries;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return this.summaries.size();
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
                View view = inflater.inflate(R.layout.summary_item_default, parent, false);

                viewHolder = new ViewHolderDefault(view);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.summary_item_default, parent, false);
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
        // Reset views
        viewHolder.userFullnameTextView.setVisibility(View.GONE);
        viewHolder.userPhotoImageView.setVisibility(View.GONE);
        viewHolder.dividerView.setVisibility(View.GONE);

        BusDailySummary summary = summaries.get(position);


        viewHolder.spentAtTextView.setText(Helpers.formatCreateAt(summary.getSummaryDate()));
        viewHolder.amountTextView.setText("₹" + summary.getTotalCollection());


        User user = User.getUserById(summary.getSubmittedById());
        if (showMember && user != null) {
            Helpers.loadIconPhoto(viewHolder.userPhotoImageView, user.getPhotoUrl());
            viewHolder.userPhotoImageView.setOnClickListener(v -> {
                ProfileExpenseActivity.newInstance(context, user.getId());
                ((Activity)getContext()).overridePendingTransition(R.anim.right_in, R.anim.stay);
            });

            viewHolder.userFullnameTextView.setText(user.getFullname());
            viewHolder.userFullnameTextView.setVisibility(View.VISIBLE);
            viewHolder.userPhotoImageView.setVisibility(View.VISIBLE);
            viewHolder.dividerView.setVisibility(View.VISIBLE);
        }

        // Set item click listener
        viewHolder.cardView.setOnClickListener(v -> {
            BusDailySummaryDetailActivity.newInstance(context, summaries.get(position).getId());
            ((Activity)getContext()).overridePendingTransition(R.anim.right_in, R.anim.stay);
        });
    }

    public void setShowMember(boolean showMember) {
        this.showMember = showMember;
        notifyDataSetChanged();
    }

    public void clear() {
        summaries.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<BusDailySummary> summaries) {
        if (summaries == null) {
            return;
        }

        this.summaries.addAll(summaries);
        notifyDataSetChanged();
    }

    public static class ViewHolderDefault extends RecyclerView.ViewHolder {
        @BindView(R.id.summary_item_default_spent_at_text_view_id) TextView spentAtTextView;
        @BindView(R.id.summary_item_default_amount_text_view_id) TextView amountTextView;
        @BindView(R.id.summary_item_default_user_photo_image_view_id) ImageView userPhotoImageView;
        @BindView(R.id.summary_item_default_name_text_view_id) TextView userFullnameTextView;
        @BindView(R.id.summary_item_default_view_id) View dividerView;
        @BindView(R.id.summary_item_default_card_view_id) CardView cardView;

        private View itemView;

        public ViewHolderDefault(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView = view;
        }
    }
}
