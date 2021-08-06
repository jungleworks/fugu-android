package com.skeleton.mvp.ui.notificationrecycler;

import com.skeleton.mvp.data.model.getnotifications.NotificationsResponse;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;

/**
 * Created by bhavya on 07/12/17.
 * juggernaut-android-mvp
 */

public class NotificationInteractorImpl implements NotificationInteractor {

    @Override
    public void getNotifications(final String authorization,
                                 final String contentLanguage,
                                 final int limit,
                                 final int skip,
                                 final GetNotificationsListener getNotificationsListener) {

        RestClient.getApiInterface(true).getNotifications(authorization,
                contentLanguage,
                limit,
                skip)
                .enqueue(new ResponseResolver<NotificationsResponse>() {
                    @Override
                    public void onSuccess(final NotificationsResponse notificationsResponse) {
                        getNotificationsListener.onGetNotificationsSuccess(notificationsResponse);
                    }

                    @Override
                    public void onError(final ApiError error) {
                        getNotificationsListener.onGetNotificationsFailed(error, null);
                    }

                    @Override
                    public void onFailure(final Throwable throwable) {
                        getNotificationsListener.onGetNotificationsFailed(null, throwable);
                    }
                });
    }
}
