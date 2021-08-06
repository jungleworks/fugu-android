package com.skeleton.mvp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.skeleton.mvp.R;

public class AnimationTestActivity extends AppCompatActivity {

    private int animTime = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_test);



        ObjectAnimator arrowOneFadeIn = ObjectAnimator.ofFloat(findViewById(R.id.img4), View.ALPHA, 0f, 1f);
        arrowOneFadeIn.setDuration(animTime);
        ObjectAnimator arrowTwoFadeIn = ObjectAnimator.ofFloat(findViewById(R.id.img3), View.ALPHA, 0f, 1f);
        arrowTwoFadeIn.setDuration(animTime).setStartDelay(animTime);
        ObjectAnimator arrowOneFadeOut = ObjectAnimator.ofFloat(findViewById(R.id.img4), View.ALPHA, 1f, 0f);
        arrowOneFadeOut.setDuration(animTime).setStartDelay(375);
        ObjectAnimator arrowThreeFadeIn = ObjectAnimator.ofFloat(findViewById(R.id.img2), View.ALPHA, 0f, 1f);
        arrowThreeFadeIn.setDuration(animTime).setStartDelay(300);
        ObjectAnimator arrowTwoFadeOut = ObjectAnimator.ofFloat(findViewById(R.id.img3), View.ALPHA, 1f, 0f);
        arrowTwoFadeOut.setDuration(animTime).setStartDelay(525);
        ObjectAnimator arrowFourFadeIn = ObjectAnimator.ofFloat(findViewById(R.id.img1), View.ALPHA, 0f, 1f);
        arrowFourFadeIn.setDuration(animTime).setStartDelay(450);
        ObjectAnimator arrowThreeFadeOut = ObjectAnimator.ofFloat(findViewById(R.id.img2), View.ALPHA, 1f, 0f);
        arrowThreeFadeOut.setDuration(animTime).setStartDelay(675);
        ObjectAnimator arrowFourFadeOut = ObjectAnimator.ofFloat(findViewById(R.id.img1), View.ALPHA, 1f, 0f);
        arrowFourFadeOut.setDuration(animTime).setStartDelay(825);

        onShakeImage();
        final AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                set.start();
            }
        });

        set.play(arrowOneFadeIn);
        set.play(arrowTwoFadeIn);
        set.play(arrowOneFadeOut);
        set.play(arrowThreeFadeIn);
        set.play(arrowTwoFadeOut);
        set.play(arrowFourFadeIn);
        set.play(arrowThreeFadeOut);
        set.play(arrowFourFadeOut);
        set.start();
    }

    public void onShakeImage() {
        Animation shake,slideUp;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_call);


        ImageView image;
        image = findViewById(R.id.myView);
        image.startAnimation(slideUp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image.clearAnimation();
                image.startAnimation(shake);
            }
        }, 1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onShakeImage();
            }
        }, 2700);

    }
}
