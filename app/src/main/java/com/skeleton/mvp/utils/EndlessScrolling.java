package com.skeleton.mvp.utils;

/**
 * Created by rajatdhamija on 11/06/18.
 */

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The type Endless scroll listener.
 */
public abstract class EndlessScrolling extends RecyclerView.OnScrollListener {
    /**
     * The constant TAG.
     */
    private static final String TAG = EndlessScrollListener.class.getSimpleName();

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 0;

    private LinearLayoutManager mLinearLayoutManager;

    /**
     * Instantiates a new Endless scroll listener.
     *
     * @param linearLayoutManager the linear layout manager
     */
    public EndlessScrolling(final LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public void setCurrentPage(final int currentPage) {
        this.currentPage = currentPage;
        this.previousTotal = 0;
    }

    @Override
    public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (totalItemCount < previousTotal) {
            this.currentPage = 0;
            previousTotal = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount) {
            // End has been reached

            // Do something
            currentPage++;

            onLoadMore(currentPage);

            loading = true;
        }

        if (scrolledDistance > visibleThreshold && controlsVisible) {
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -visibleThreshold && !controlsVisible) {
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }

    }


    /**
     * On load more.
     *
     * @param currentPages the current page
     */

    public abstract void onLoadMore(final int currentPages);

    /**
     * On hide.
     */
    public abstract void onHide();

    /**
     * On show.
     */
    public abstract void onShow();
}
