package com.expensemanager.app.tripsheet.driver_picker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.expensemanager.app.R;
import com.expensemanager.app.helpers.ItemClickSupport;
import com.expensemanager.app.models.Group;
import com.expensemanager.app.models.User;
import com.expensemanager.app.tripsheet.UserPickerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DriverPickerFragment extends DialogFragment {
    private static final String TAG= DriverPickerFragment.class.getSimpleName();

    private Unbinder unbinder;
    private DriverPickerListener driverPickerListener;
    private ArrayList<User> users;
    private UserPickerAdapter userPickerAdapter;
    private String groupId;

    @BindView(R.id.expense_category_fragment_close_image_view_id) ImageView closeImageView;
    @BindView(R.id.expense_category_fragment_recycler_view_id) RecyclerView driverRecyclerView;

    public DriverPickerFragment() {}

    public static DriverPickerFragment newInstance() {
        return new DriverPickerFragment();
    }

    public void setListener(DriverPickerListener driverPickerListener) {
        this.driverPickerListener = driverPickerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CategoryColorDialogStyle);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        groupId = sharedPreferences.getString(Group.ID_KEY, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_category_filter_fragment, container);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Window window = getDialog().getWindow();

        if (window != null) {
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        users = new ArrayList<>();
        userPickerAdapter = new UserPickerAdapter(getActivity(), users);

        closeImageView.setOnClickListener(v -> dismiss());
        setupRecyclerView();
        invalidateViews();
    }

    private void invalidateViews() {
        userPickerAdapter.clear();
        // Add no driver option
        userPickerAdapter.add(null);
        // Add all categories
        userPickerAdapter.addAll(User.getAllUsersByGroupId(groupId));
    }

    private void setupRecyclerView() {
        driverRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        driverRecyclerView.setAdapter(userPickerAdapter);
        ItemClickSupport.addTo(driverRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                driverPickerListener.onFinishTripSheetDriverDialog(users.get(position));
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface DriverPickerListener {
        void onFinishTripSheetDriverDialog(User driver);
    }
}
