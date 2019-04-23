package com.bigmantra.natco.tripsheet.driver_picker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bigmantra.natco.R;
import com.bigmantra.natco.helpers.ItemClickSupport;
import com.bigmantra.natco.models.Group;
import com.bigmantra.natco.models.User;
import com.bigmantra.natco.service.SyncBusDailySummary;
import com.bigmantra.natco.service.SyncUser;
import com.bigmantra.natco.tripsheet.UserPickerAdapter;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
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

    @BindView(R.id.busdailysummary_driver_fragment_close_image_view_id)
    public ImageView closeImageView;
    @BindView(R.id.busdailysummary_driver_fragment_recycler_view_id)
    public RecyclerView driverRecyclerView;
    @BindView(R.id.swipeContainer_id) SwipeRefreshLayout swipeContainer;


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
        groupId = sharedPreferences.getString(Group.GROUP_ID_KEY, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.busdailysummary_driver_picker_fragment, container);
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
        setupSwipeToRefresh();

        invalidateViews();
    }

    private void setupSwipeToRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SyncUser.getAllUsers().continueWith(onGetUsersFinished, Task.UI_THREAD_EXECUTOR);
            }
        });

        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
    }

    private Continuation<Void, Void> onGetUsersFinished = new Continuation<Void, Void>() {
        @Override
        public Void then(Task<Void> task) throws Exception {
            if (task.isFaulted()) {
                Log.e(TAG, "Error:", task.getError());
            }

            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);
            }

            return null;
        }
    };

    private void invalidateViews() {
        userPickerAdapter.clear();
        // Add no driver option
        userPickerAdapter.add(null);
        // Add all categories
//        userPickerAdapter.addAll(User.getAllUsersByGroupId(groupId));
        userPickerAdapter.addAll(User.getAllUsers());
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
