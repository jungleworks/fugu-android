package com.skeleton.mvp.ui.notificationrecycler;

import com.skeleton.mvp.data.model.getnotifications.Notification;
import com.skeleton.mvp.ui.base.BaseView;

import java.util.ArrayList;

/**
 * Created by bhavya on 07/12/17.
 * juggernaut-android-mvp
 */

public interface NotificationView extends BaseView {


    /**
     * Show No Internet Tap to Retry View
     * in case of No Internet Connection
     *
     * @param errorMessage the error message to be displayed on view
     */
    void showErrorView(final String errorMessage);

    /**
     * Show No Internet Tap to Retry View
     * in case of No Internet Connection
     *
     * @param errorMessageId the error message string id to be displayed on view
     */
    void showErrorView(final int errorMessageId);

    /**
     * Show No items available
     * when the list returned is empty
     */
    void showNoItemsView();

    /**
     * Show items in recycler view
     * when list with size greater than 0 is returned
     *
     * @param notifications list of notifications fetched from server,
     *                      returned by Notifications interactor on success
     */
    void showItems(final ArrayList<Notification> notifications);

    /**
     * show next page items
     * when OnLoadMore is executed for adapter,
     * basically showing next page items after page 1
     *
     * @param notifications list of notifications fetched from server,
     *                      returned by Notifications interactor on success
     */
    void showMoreItems(final ArrayList<Notification> notifications);

    /**
     * Show Retry View
     * in case of failure of loading next page data
     * that is page other than first page
     */
    void showRetry();

    /**
     * remove the bottom loader
     * from recycler view
     * once the data is loaded
     */
    void hideRecyclerLoader();
}
