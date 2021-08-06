package com.skeleton.mvp.customLayout;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.skeleton.mvp.R;


/**
 * Created by rajatdhamija on 16/10/17.
 */

public class ChatRelativeLayout extends RelativeLayout {
    private AppCompatTextView parentTextView;
    private View childView;

    private TypedArray typedArray;

    private int childWidth;
    private int childHeight;

    public ChatRelativeLayout(Context context) {
        super(context);
    }

    public ChatRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChatRelativeLayout, 0, 0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            parentTextView = this.findViewById(typedArray.getResourceId(R.styleable.ChatRelativeLayout_parent, -1));
            childView = this.findViewById(typedArray.getResourceId(R.styleable.ChatRelativeLayout_child, -1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (parentTextView == null || childView == null || widthSize <= 0) {
            return;
        }

        /**
         *  total available width ---->done
         *  get parent width and height ---> done
         *  get child width and height ----> done
         *  get parent line count ----> done
         *  get last line width if lines more than 0<----->else 0 --->done
         *  get default values for height and width as given in xml file ---> done
         *
         *
         *  Checking for no. of lines and date setting
         *  case 1 : more than one line and date in same line
         *  case 2 : one line and date in next line
         *  case 3: more than one line and date in next line
         *  default : one line and date in same line (default case)
         *
         *  set paddings for parent and child layouts onLayout
         *  If parent and child views are not null else exit
         */
        int availableWidth = widthSize - getPaddingLeft() - getPaddingRight();

        LayoutParams parentLayoutParams = (LayoutParams) parentTextView.getLayoutParams();
        int parentWidth = parentTextView.getMeasuredWidth() + parentLayoutParams.leftMargin + parentLayoutParams.rightMargin;
        int parentHeight = parentTextView.getMeasuredHeight() + parentLayoutParams.topMargin + parentLayoutParams.bottomMargin;

        LayoutParams childLayoutParams = (LayoutParams) childView.getLayoutParams();
        childWidth = childView.getMeasuredWidth() + childLayoutParams.leftMargin + childLayoutParams.rightMargin;
        childHeight = childView.getMeasuredHeight() + childLayoutParams.topMargin + childLayoutParams.bottomMargin;

        int parentLineCount = parentTextView.getLineCount();

//        float parentLastLineWitdh;
//        float parentLastLineWidthLatest = 0;
//        if (parentLineCount > 0) {
//            for (int i = 0; i < parentLineCount; i++) {
//                parentLastLineWitdh = parentTextView.getLayout().getLineWidth(i);
//                if (parentLastLineWitdh > parentLastLineWidthLatest && ((Math.abs(parentLastLineWidthLatest - parentLastLineWitdh) < 30) || parentLastLineWidthLatest == 0)) {
//                    parentLastLineWidthLatest = parentLastLineWitdh;
//                }
//            }
//        }

        float parentLastLineWitdh = parentTextView.getLayout().getLineWidth(parentLineCount - 1);
        float parentLastLineWidthLatest = 0;

        if (parentLineCount > 0) {
            for (int i = 0; i < parentLineCount; i++) {
                parentLastLineWidthLatest = parentTextView.getLayout().getLineWidth(i);

                if (parentLastLineWidthLatest > parentLastLineWitdh && (Math.abs(parentLastLineWidthLatest - parentLastLineWitdh) < 150)) {
                    parentLastLineWitdh = parentLastLineWidthLatest;
                }
            }
        } else {
            parentLastLineWitdh = 0;
        }


        widthSize = getPaddingLeft() + getPaddingRight();
        int heightSize = getPaddingTop() + getPaddingBottom();

        if (parentLineCount > 1 && (parentLastLineWitdh + childWidth) < parentTextView.getMeasuredWidth()) {
            widthSize += parentWidth;
            heightSize += parentHeight;
        } else if (parentLineCount == 1 && (parentWidth + childWidth) >= availableWidth) {
            widthSize += parentTextView.getMeasuredWidth();
            heightSize += parentHeight + childHeight;
        } else if (parentLineCount > 1 && (parentLastLineWitdh + childWidth) >= availableWidth) {
            widthSize += parentWidth;
            heightSize += parentHeight + childHeight;
        } else {
            widthSize += parentWidth + childWidth;
            heightSize += parentHeight;
        }

        this.setMeasuredDimension(widthSize, heightSize);
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (parentTextView != null && childView != null) {
            parentTextView.layout(
                    getPaddingLeft(),
                    getPaddingTop(),
                    parentTextView.getWidth() + getPaddingLeft(),
                    parentTextView.getHeight() + getPaddingTop());

            childView.layout(
                    right - left - childWidth - getPaddingRight(),
                    bottom - top - getPaddingBottom() - childHeight,
                    right - left - getPaddingRight(),
                    bottom - top - getPaddingBottom());
        }
    }
}