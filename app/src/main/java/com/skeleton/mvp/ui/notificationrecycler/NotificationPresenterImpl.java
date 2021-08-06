package com.skeleton.mvp.ui.notificationrecycler;

import androidx.annotation.NonNull;

import com.skeleton.mvp.data.model.getnotifications.Notification;
import com.skeleton.mvp.data.model.getnotifications.NotificationData;
import com.skeleton.mvp.data.model.getnotifications.NotificationsResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.ui.base.BasePresenterImpl;

import java.util.ArrayList;


/**
 * Created by bhavya on 07/12/17.
 * juggernaut-android-mvp
 */

class NotificationPresenterImpl extends BasePresenterImpl implements NotificationPresenter {

    // todo add these static final variables in your common constant file
    private static final int LIMIT = 10;
    private static final String AUTHORIZATION = "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySUQi"
            + "OiI1YTJmZDZlYzM2YTQ5NzJhODEzMTU4ZDUiLCJyb2xlIjoiZHJpdmVyIiwic2Vzc2lvbklEIjoiNWE0MjM5YjYzNTk0NT"
            + "A3NGNhNjNmNzJmIiwiZGF0ZSI6IjIwMTctMTItMjZUMTE6NTk6NTAuNTg1WiIsImlhdCI6MTUxNDI4OTU5MH0.oGve9fnHJl9h"
            + "4suDZn2zkos-oWSeI4HhBFSbVx-dY6I";
    private static final String CONTENT_LANGUAGE = "en";


    private NotificationView mNotificationView;
    private NotificationInteractor mNotificationInteractor;
    private int skip = 0, totalCount = -1;
    private boolean showLoading = true;
    private ArrayList<Notification> notifications = new ArrayList<>();

    /**
     * Constructor
     *
     * @param mainView the associated Main view
     */
    NotificationPresenterImpl(@NonNull final NotificationView mainView) {
        mNotificationView = mainView;
        mNotificationInteractor = new NotificationInteractorImpl();
    }

    @Override
    public void onAttach() {
        super.onAttach();
        getNotifications();
    }

    @Override
    public void onRestoreState(final NotificationData notificationData) {
        super.onAttach();
        notifications = notificationData.getNotifications();
        totalCount = notificationData.getCount();
        skip = notifications.size();

        displayData();
    }


    @Override
    public void onNoInternetClicked() {
        getNotifications();
    }

    @Override
    public void onSwipeRefresh() {
        if (mNotificationView.isNetworkConnected()) {
            skip = 0;
            totalCount = -1;
            showLoading = false;
            getNotifications();
        } else {
            mNotificationView.showRetry();
        }
    }

    @Override
    public void onLoadMore() {
        getNotifications();
    }

    @Override
    public NotificationData getNotificationsData() {
        return new NotificationData(totalCount, notifications);
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }


    private void getNotifications() {

        if (totalCount != -1 && totalCount <= skip) {
            mNotificationView.hideRecyclerLoader();
            return;
        }

        if (showLoading) {
            mNotificationView.showLoading();
        }

        mNotificationInteractor.getNotifications(AUTHORIZATION,
                CONTENT_LANGUAGE, LIMIT, skip, new NotificationInteractor.GetNotificationsListener() {
                    @Override
                    public void onGetNotificationsSuccess(final NotificationsResponse notificationsResponse) {

                        if (showLoading) {
                            mNotificationView.hideLoading();
                            showLoading = false;
                        }

                        if (skip == 0) {
                            notifications.clear();
                        }

                        notifications.addAll(notificationsResponse.getNotificationData().getNotifications());

                        displayData();

                        totalCount = notificationsResponse.getNotificationData().getCount();
                        skip = notifications.size();

                    }

                    @Override
                    public void onGetNotificationsFailed(final ApiError apiError, final Throwable throwable) {

                        if (isViewAttached()) {

                            if (showLoading) {
                                mNotificationView.hideLoading();

                                if (apiError != null) {
                                    mNotificationView.showErrorView(apiError.getMessage());
                                } else {
                                    // resolve error through throwable
                                    mNotificationView.showErrorView(parseThrowableMessage(throwable));
                                }
                            } else {
                                mNotificationView.showRetry();
                            }
                        }
                    }
                });
    }

    /**
     * Method to handle view states and display data
     * according to the response from server
     */
    private void displayData() {
        if (notifications.size() == 0) {
            mNotificationView.showNoItemsView();
        } else {
            if (skip == 0) {
                mNotificationView.showItems(notifications);
            } else {
                mNotificationView.showMoreItems(new ArrayList<>(notifications.subList(skip, notifications.size())));
            }
        }
    }
}
