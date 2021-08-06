package com.skeleton.mvp.util;

/**
 * Created by rajatdhamija
 * 5/1/18.
 */

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * The type Recycler item click listener.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener mListener;

    /**
     * The interface On item click listener.
     */
    public interface OnItemClickListener {
        /**
         * On item click.
         *
         * @param view     the view
         * @param position the position
         */
        void onItemClick(final View view, final int position);
    }

    /**
     * The M gesture detector.
     */
    private GestureDetector mGestureDetector;

    /**
     * Instantiates a new Recycler item click listener.
     *
     * @param context  the context
     * @param listener the listener
     */
    public RecyclerItemClickListener(final Context context, final OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(final MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView view, final MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(final RecyclerView view, final MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {

    }
}