package com.skeleton.mvp.ui.notificationrecycler;

import com.skeleton.mvp.data.model.getnotifications.NotificationsResponse;
import com.skeleton.mvp.data.network.ApiError;

/**
 * Created by bhavya on 07/12/17.
 * juggernaut-android-mvp
 */

public interface NotificationInteractor {

    /**
     * API hit for fetching notifications from server
     *
     * @param authorization            header to authorize user
     * @param contentLang              header the content language in which app is running
     * @param limit                    the number of list items required in response
     * @param skip                     the number of items to be skipped
     * @param getNotificationsListener listener to handle success and failure events of the API hit
     */
    void getNotifications(final String authorization,
                          final String contentLang,
                          final int limit,
                          final int skip,
                          final GetNotificationsListener getNotificationsListener);

    /**
     * SignIn listener
     */
    interface GetNotificationsListener {

        /**
         * On GetNotifications success
         *
         * @param notificationsResponse the parsed notifications response object
         */
        void onGetNotificationsSuccess(final NotificationsResponse notificationsResponse);

        /**
         * On GetNotifications failed
         *
         * @param apiError  the parsed api error if any
         * @param throwable the generated throwable if any
         */
        void onGetNotificationsFailed(final ApiError apiError, final Throwable throwable);
    }
}
