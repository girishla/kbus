package com.expensemanager.app.tripsheet.conductor_picker;

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
import com.expensemanager.app.models.User;
import com.expensemanager.app.models.Group;
import com.expensemanager.app.models.User;
import com.expensemanager.app.tripsheet.UserPickerAdapter;
import com.expensemanager.app.models.User;
import com.expensemanager.app.tripsheet.UserPickerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConductorPickerFragment extends DialogFragment {
    private static final String TAG= ConductorPickerFragment.class.getSimpleName();

    private Unbinder unbinder;
    private ConductorPickerListener conductorPickerListener;
    private ArrayList<User> users;
    private UserPickerAdapter userPickerAdapter;
    private String groupId;

    @BindView(R.id.busdailysummary_conductor_fragment_close_image_view_id)
    public ImageView closeImageView;
    @BindView(R.id.busdailysummary_conductor_fragment_recycler_view_id)
    public RecyclerView conductorRecyclerView;

    public ConductorPickerFragment() {}

    public static ConductorPickerFragment newInstance() {
        return new ConductorPickerFragment();
    }

    public void setListener(ConductorPickerListener conductorPickerListener) {
        this.conductorPickerListener = conductorPickerListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CategoryColorDialogStyle);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_preferences_session_key), 0);
        groupId = sharedPreferences.getString(Group.GROUP_ID_KEY, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.busdailysummary_conductor_picker_fragment, container);
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
        // Add no conductor option
        userPickerAdapter.add(null);
        // Add all categories
//        userPickerAdapter.addAll(User.getAllUsersByGroupId(groupId));
        userPickerAdapter.addAll(User.getAllUsers());
    }

    private void setupRecyclerView() {
        conductorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        conductorRecyclerView.setAdapter(userPickerAdapter);
        ItemClickSupport.addTo(conductorRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                conductorPickerListener.onFinishTripSheetConductorDialog(users.get(position));
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface ConductorPickerListener {
        void onFinishTripSheetConductorDialog(User conductor);
    }
}
