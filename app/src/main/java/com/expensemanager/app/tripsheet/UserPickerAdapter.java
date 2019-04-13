package com.expensemanager.app.tripsheet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.expensemanager.app.R;
import com.expensemanager.app.models.User;
import com.expensemanager.app.service.enums.EIcon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG= UserPickerAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_DEFAULT = 0;
    private static final int VIEW_TYPE_NULL = 1;

    private ArrayList<User> users;
    private Context context;

    public UserPickerAdapter(Context context, ArrayList<User> conductors) {
        this.context = context;
        this.users = conductors;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return users.get(position) != null ? VIEW_TYPE_DEFAULT : VIEW_TYPE_NULL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        switch (viewType) {
            case VIEW_TYPE_DEFAULT:
                view = inflater.inflate(R.layout.user_picker_item, parent, false);
                viewHolder = new ViewHolderDefault(view);
                break;
            case VIEW_TYPE_NULL:
                view = inflater.inflate(R.layout.user_picker_item_null, parent, false);
                viewHolder = new ViewHolderDefault(view);
                break;

            default:
                View defaultView = inflater.inflate(R.layout.user_picker_item, parent, false);
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
            case VIEW_TYPE_NULL:
                ViewHolderDefault viewHolderNull = (ViewHolderDefault) viewHolder;
                viewHolderNull.colorImageView.setVisibility(View.INVISIBLE);
                viewHolderNull.iconImageView.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    private void configureViewHolderDefault(ViewHolderDefault viewHolder, int position) {
        // Reset views
        viewHolder.iconImageView.setVisibility(View.INVISIBLE);

        User user = users.get(position);
        EIcon eIcon = EIcon.instanceFromName("face");

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("black"));
        viewHolder.colorImageView.setImageDrawable(colorDrawable);
        viewHolder.colorImageView.setVisibility(View.VISIBLE);

        if (eIcon != null) {
            viewHolder.iconImageView.setImageResource(eIcon.getValueRes());
            viewHolder.iconImageView.setVisibility(View.VISIBLE);
        }
        viewHolder.nameTextView.setText(user.getFullname());
    }

    public void clear() {
        this.users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void add(User user) {
        this.users.add(user);
        notifyDataSetChanged();
    }

    public static class ViewHolderDefault extends RecyclerView.ViewHolder {
        @BindView(R.id.user_picker_item_color_image_view_id) CircleImageView colorImageView;
        @BindView(R.id.user_picker_item_icon_image_view_id) ImageView iconImageView;
        @BindView(R.id.user_picker_item_name_text_view_id) TextView nameTextView;

        private View itemView;

        public ViewHolderDefault(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView = view;
        }
    }
}
