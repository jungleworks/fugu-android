package com.skeleton.mvp.ui.notificationrecycler.adapter;

import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skeleton.mvp.R;
import com.skeleton.mvp.data.model.getnotifications.Notification;

import java.util.ArrayList;


/**
 * Created by bhavya on 15/11/17.
 * MyNotificationsAdapter is used by MyNotificationsActivity
 */
public class MyNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_PROG = 1;
    private static final int VIEW_RETRY = 2;
    private static final int VIEW_ERROR = 3;
    private static final int VIEW_NO_DATA = 4;

    private ArrayList<Notification> notifications;
    private String errorMessage;
    private String noDataString;
    private OnLoadMoreListener onLoadMoreListener;
    private OnActionListener mOnActionListener;
    private boolean isMoreLoading = true;


    /**
     * interface to listen to when recycler view is scrolled
     */
    public interface OnLoadMoreListener {

        /**
         * override onLoadMore() in listener
         * to specify the action required when recycler view is scrolled
         * to load more data
         */
        void onLoadMore();
    }


    /**
     * Instantiates a new MyNotificationsAdapter
     *
     * @param onLoadMoreListener the activity that listens to the load more callbacks
     */
    public MyNotificationsAdapter(final OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        this.notifications = new ArrayList<>();
    }

    public void setOnActionListener(final OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    /**
     * Call back implemented in HomeActivity to initiate API hit or perform
     * action on tap of Accept and Reject Buttons
     */
    public interface OnActionListener {

        /**
         * method to be executed to retry loading data
         */
        void onRetry();

    }

    @Override
    public int getItemViewType(final int position) {
        return notifications.get(position).getItemType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View mView;

        switch (viewType) {
            case VIEW_ITEM:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notification_list, parent, false);
                return new MyNotificationsViewHolder(mView);
            case VIEW_PROG:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_progress, parent, false);
                return new ProgressViewHolder(mView);
            case VIEW_RETRY:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_retry, parent, false);
                return new RetryViewHolder(mView);
            case VIEW_ERROR:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_error, parent, false);
                return new ErrorViewHolder(mView);
            case VIEW_NO_DATA:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_no_data, parent, false);
                return new NoDataViewHolder(mView);
            default:
                mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notification_list, parent, false);
                return new MyNotificationsViewHolder(mView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyNotificationsViewHolder) {

            final MyNotificationsViewHolder myNotificationsViewHolder = (MyNotificationsViewHolder) holder;
            final Notification currentNotification = notifications.get(myNotificationsViewHolder.getAdapterPosition());

            myNotificationsViewHolder.tvNotificationTitle.setText(currentNotification.getNotificationType());
            myNotificationsViewHolder.tvNotificationText.setText(currentNotification.getMessage());
        } else if (holder instanceof RetryViewHolder) {
            final RetryViewHolder retryViewHolder = (RetryViewHolder) holder;

            retryViewHolder.ivRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (mOnActionListener != null) {
                        mOnActionListener.onRetry();
                    }
                }
            });
        } else if (holder instanceof ErrorViewHolder) {
            final ErrorViewHolder errorViewHolder = (ErrorViewHolder) holder;
            errorViewHolder.tvNoInternet.setText(errorMessage);
            errorViewHolder.tvNoInternet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (mOnActionListener != null) {
                        mOnActionListener.onRetry();
                    }
                }
            });
        } else if (holder instanceof NoDataViewHolder) {
            final NoDataViewHolder noDataViewHolder = (NoDataViewHolder) holder;
            noDataViewHolder.tvNoNotifications.setText(noDataString);
        }
    }

    /**
     * show progress loader at bottom while recycler view
     * is loading more items on scroll
     */
    public void showLoading() {
        if (isMoreLoading && notifications != null && onLoadMoreListener != null) {
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    notifications.add(new Notification());
                    notifications.get(notifications.size() - 1).setItemType(VIEW_PROG);
                    notifyItemInserted(notifications.size() - 1);
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    /**
     * setter to set if loading more items is required
     */
    public void setMore() {
        this.isMoreLoading = true;
    }

    /**
     * dismiss progress loader shown at bottom
     * after the onLoadMore() method has performed its task
     * and new data is added to recycler view.
     * This function removes the progress loader item added at end of recycler view list
     */
    public void dismissLoading() {
        if (notifications != null && notifications.size() > 0
                && notifications.get(notifications.size() - 1).getItemType() == VIEW_PROG) {
            notifications.remove(notifications.size() - 1);
            notifyItemRemoved(notifications.size());
        }
    }

    /**
     * add all items to notification list, called
     * to initialize the list a fresh
     *
     * @param allNotifications notification list
     */
    public void addAll(final ArrayList<Notification> allNotifications) {
        notifications.clear();
        notifications.addAll(allNotifications);
        notifyDataSetChanged();
    }

    /**
     * add more items to notification list on scroll
     *
     * @param moreNotifications notification list
     */
    public void addItemMore(final ArrayList<Notification> moreNotifications) {
        int sizeInit = notifications.size();
        notifications.addAll(moreNotifications);
        notifyItemRangeChanged(sizeInit, notifications.size());
    }

    @Override
    public int getItemCount() {
        return notifications == null ? 0 : notifications.size();
    }

    /**
     * In case of an error
     * clear the recycler view list and add a null value item
     * notify adapter that data set has changed
     * show error message to user
     *
     * @param message error message to be displayed
     */
    public void displayErrorMessage(final String message) {
        errorMessage = message;
        notifications.clear();
        notifications.add(new Notification());
        notifications.get(notifications.size() - 1).setItemType(VIEW_ERROR);
        notifyDataSetChanged();
    }

    /**
     * In case the list of data received from server
     * is empty
     * show no data message to user
     *
     * @param message no data message to be displayed
     */
    public void displayNoDataString(final String message) {
        noDataString = message;
        notifications.clear();
        notifications.add(new Notification());
        notifications.get(notifications.size() - 1).setItemType(VIEW_NO_DATA);
        notifyDataSetChanged();
    }


    /**
     *
     */
    public void displayRetryView() {
        if (notifications.get(notifications.size() - 1).getItemType() != VIEW_RETRY) {
            notifications.add(new Notification());
            notifications.get(notifications.size() - 1).setItemType(VIEW_RETRY);
            notifyItemInserted(notifications.size() - 1);
        }
    }

    /**
     *
     */
    public void hideRetryView() {
        if (notifications != null && notifications.size() > 0
                && notifications.get(notifications.size() - 1).getItemType() == VIEW_RETRY) {
            notifications.remove(notifications.size() - 1);
            notifyItemRemoved(notifications.size());
        }
    }

    /**
     * view holder class
     */
    private final class MyNotificationsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNotificationTitle, tvNotificationText;

        /**
         * Instantiates a new view holder.
         *
         * @param itemView itemView
         */
        MyNotificationsViewHolder(final View itemView) {
            super(itemView);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationText = itemView.findViewById(R.id.tvNotificationText);
        }
    }

    /**
     * Bottom Progress bar view Holder
     */
    private final class ProgressViewHolder extends RecyclerView.ViewHolder {

        /**
         * @param progressView view
         */
        private ProgressViewHolder(final View progressView) {
            super(progressView);
        }
    }

    /**
     * view holder class
     */
    private final class RetryViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivRetry;

        /**
         * Instantiates a new view holder.
         *
         * @param itemView itemView
         */
        RetryViewHolder(final View itemView) {
            super(itemView);
            ivRetry = itemView.findViewById(R.id.ivRetry);
        }
    }

    /**
     * view holder class
     */
    private final class ErrorViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNoInternet;

        /**
         * Instantiates a new view holder.
         *
         * @param itemView itemView
         */
        ErrorViewHolder(final View itemView) {
            super(itemView);
            tvNoInternet = itemView.findViewById(R.id.tvNoInternet);
        }
    }

    /**
     * view holder class
     */
    private final class NoDataViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNoNotifications;

        /**
         * Instantiates a new view holder.
         *
         * @param itemView itemView
         */
        NoDataViewHolder(final View itemView) {
            super(itemView);
            tvNoNotifications = itemView.findViewById(R.id.tvNoNotifications);
        }
    }
}
