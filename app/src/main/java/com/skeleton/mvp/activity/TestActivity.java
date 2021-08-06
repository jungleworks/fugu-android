package com.skeleton.mvp.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.skeleton.mvp.R;

import ua.zabelnikov.swipelayout.layout.frame.SwipeableLayout;
import ua.zabelnikov.swipelayout.layout.listener.LayoutShiftListener;
import ua.zabelnikov.swipelayout.layout.listener.OnLayoutPercentageChangeListener;

public class TestActivity extends AppCompatActivity {

    private SwipeableLayout swipeableLayout;
    private View colorFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        swipeableLayout = (SwipeableLayout) findViewById(R.id.swipeableLayout);
        colorFrame = findViewById(R.id.colorContainer);

        swipeableLayout.setOnLayoutPercentageChangeListener(new OnLayoutPercentageChangeListener() {
            private float lastAlpha = 1.0f;

            @Override
            public void percentageY(float percentage) {
                float alphaCorrector = percentage / 3;
                AlphaAnimation alphaAnimation = new AlphaAnimation(lastAlpha, 1 - alphaCorrector);
                alphaAnimation.setDuration(300);
                colorFrame.startAnimation(alphaAnimation);
                lastAlpha = 1 - alphaCorrector;
            }
        });
//        swipeableLayout.setOnSwipedListener(new OnLayoutSwipedListener() {
//            @Override
//            public void onLayoutSwiped() {
//                TestActivity.this.finish();
//            }
//        });

        swipeableLayout.setLayoutShiftListener(new LayoutShiftListener() {
            @Override
            public void onLayoutShifted(float positionX, float positionY, boolean isTouched) {
                Log.e("Swipelayout", "isTouched = " + isTouched);
            }
        });
    }
}

