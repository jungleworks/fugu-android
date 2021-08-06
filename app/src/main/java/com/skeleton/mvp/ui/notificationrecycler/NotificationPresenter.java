package com.skeleton.mvp.ui.notificationrecycler;

import com.skeleton.mvp.data.model.getnotifications.NotificationData;
import com.skeleton.mvp.ui.base.BasePresenter;

/**
 * Created by bhavya on 07/12/17.
 * juggernaut-android-mvp
 */

public interface NotificationPresenter extends BasePresenter {


    /**
     * Performs No Internet, tap to Retry
     * action
     */
    void onNoInternetClicked();

    /**
     * Called when swipe refersh onRefresh() is executed
     * perform on refresh action here
     */
    void onSwipeRefresh();

    /**
     * Called when recycler view onLoadMore() is executed
     * fetch next page data and notify recycler once done
     */
    void onLoadMore();

    /**
     * Returns the Notifications data list maintained by presenter
     * @return notifications , array list of notifications
     */
    NotificationData getNotificationsData();

    /**
     * Returns the total count of notifications maintained by presenter
     * @return totalCount
     */
    int getTotalCount();

    /**
     * Method to get the data saved during onSaveInstanceState() in view
     * and pass it to presenter to restore state
     * @param notificationData notification data fetched from server
     */
    void onRestoreState(final NotificationData notificationData);

}
