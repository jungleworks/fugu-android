package com.skeleton.mvp.ui.notificationrecycler;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.getnotifications.Notification;
import com.skeleton.mvp.data.model.getnotifications.NotificationData;
import com.skeleton.mvp.ui.base.BaseActivity;
import com.skeleton.mvp.ui.notificationrecycler.adapter.MyNotificationsAdapter;

import java.util.ArrayList;

/**
 * Created by bhavya on 07/12/17.
 * juggernaut-android-mvp
 * Activity to Show Notifications list from side menu
 */
public class NotificationActivity extends BaseActivity implements  MyNotificationsAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, NotificationView {

    // todo put RECYCLER_VISIBLE_THRESH_HOLD in your common constatnts file and remove from here
    private static final int RECYCLER_VISIBLE_THRESH_HOLD = 3;
    private static final String APP_NOTIFICATIONS_DATA = "app_notifications_data";

    private RecyclerView rvNotification;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayoutManager layoutManager;
    private MyNotificationsAdapter myNotificationsAdapter;
    private NotificationPresenter mNotificationPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        init();
        initRecycler();
        mNotificationPresenter = new NotificationPresenterImpl(this);

        if (savedInstanceState == null) {
            mNotificationPresenter.onAttach();
        } else {
            mNotificationPresenter.onRestoreState((NotificationData) savedInstanceState.getSerializable(APP_NOTIFICATIONS_DATA));
        }

        listeners();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable(APP_NOTIFICATIONS_DATA, mNotificationPresenter.getNotificationsData());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNotificationPresenter.onDetach();
    }

    /**
     * Initializations
     */
    private void init() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        rvNotification = findViewById(R.id.rvNotification);
        layoutManager = new LinearLayoutManager(NotificationActivity.this);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar();
    }

    /**
     * Listeners
     */
    private void listeners() {
        swipeRefresh.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        mNotificationPresenter.onSwipeRefresh();
    }

    @Override
    public void onLoadMore() {
        swipeRefresh.setRefreshing(false);
        mNotificationPresenter.onLoadMore();
    }

    @Override
    public void hideRecyclerLoader() {
        myNotificationsAdapter.dismissLoading();
    }

    @Override
    public void showErrorView(final String errorMessage) {
        myNotificationsAdapter.displayErrorMessage(errorMessage.concat(getString(R.string.tap_to_retry)));
        swipeRefresh.setRefreshing(false);
        swipeRefresh.setEnabled(false);
    }


    @Override
    public void showErrorView(final int errorMessageId) {
        showErrorView(getString(errorMessageId));
    }

    @Override
    public void showRetry() {
        hideRecyclerLoader();
        myNotificationsAdapter.displayRetryView();
        swipeRefresh.setRefreshing(false);
        swipeRefresh.setEnabled(true);
    }

    @Override
    public void showNoItemsView() {
        myNotificationsAdapter.displayNoDataString(getString(R.string.no_notifications));
        swipeRefresh.setEnabled(true);
    }

    @Override
    public void showItems(final ArrayList<Notification> notifications) {
        myNotificationsAdapter.addAll(notifications);
        swipeRefresh.setRefreshing(false);
        swipeRefresh.setEnabled(true);
        myNotificationsAdapter.setMore();
    }

    @Override
    public void showMoreItems(final ArrayList<Notification> notifications) {
        hideRecyclerLoader();
        myNotificationsAdapter.hideRetryView();
        myNotificationsAdapter.addItemMore(notifications);
        myNotificationsAdapter.setMore();
        swipeRefresh.setEnabled(true);
    }


    private void initRecycler() {
        myNotificationsAdapter = new MyNotificationsAdapter(this);
        rvNotification.setLayoutManager(layoutManager);
        rvNotification.setAdapter(myNotificationsAdapter);

        myNotificationsAdapter.setOnActionListener(new MyNotificationsAdapter.OnActionListener() {
            @Override
            public void onRetry() {
                mNotificationPresenter.onNoInternetClicked();
            }
        });

        rvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findLastCompletelyVisibleItemPosition() == (myNotificationsAdapter.getItemCount() - RECYCLER_VISIBLE_THRESH_HOLD)) {
                    myNotificationsAdapter.showLoading();
                }
            }
        });
    }
}
