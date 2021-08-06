package com.skeleton.mvp.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.skeleton.mvp.R
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.util.Log
import com.skeleton.mvp.utils.CallTouchListener


class TesingtActivity : BaseActivity(), CallTouchListener.OnCallItemTouchListener {
    override fun onItemTouch(swipeView: View) {

        when (swipeView.id) {
            R.id.ivAnswerCall -> {
                llAnswer?.visibility = View.INVISIBLE
                image?.clearAnimation()
            }
            R.id.ivRejectCall -> {
                llAnswer?.visibility = View.INVISIBLE
                llReply?.visibility = View.INVISIBLE
                llReject?.visibility = View.VISIBLE
                tvRejectCall?.visibility = View.VISIBLE
                tvReplyCall?.visibility = View.INVISIBLE
                tvAnswerCall?.visibility = View.INVISIBLE
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(2000, 1000) {
                    override fun onFinish() {
                        llAnswer?.visibility = View.VISIBLE
                        llReject?.visibility = View.INVISIBLE
                        llReply?.visibility = View.INVISIBLE
                        tvRejectCall?.visibility = View.INVISIBLE
                        tvReplyCall?.visibility = View.INVISIBLE
                        tvAnswerCall?.visibility = View.VISIBLE
                    }

                    override fun onTick(millisUntilFinished: Long) {
                    }

                }
                countDownTimer?.start()
            }

            R.id.ivReplyCall -> {
                llAnswer?.visibility = View.INVISIBLE
                llReply?.visibility = View.VISIBLE
                llReject?.visibility = View.INVISIBLE
                tvReplyCall?.visibility = View.VISIBLE
                tvAnswerCall?.visibility = View.INVISIBLE
                tvRejectCall?.visibility = View.INVISIBLE
                countDownTimer?.cancel()
                countDownTimer = object : CountDownTimer(2000, 1000) {
                    override fun onFinish() {
                        llAnswer?.visibility = View.VISIBLE
                        llReject?.visibility = View.INVISIBLE
                        llReply?.visibility = View.INVISIBLE
                        tvReplyCall?.visibility = View.INVISIBLE
                        tvAnswerCall?.visibility = View.VISIBLE
                        tvRejectCall?.visibility = View.INVISIBLE
                    }

                    override fun onTick(millisUntilFinished: Long) {
                    }

                }
                countDownTimer?.start()
            }
        }
    }

    override fun onItemTouchReleased(swipeView: View) {
        when (swipeView.id) {
            R.id.ivAnswerCall -> {
                llAnswer?.visibility = View.VISIBLE
                onShakeImage()
            }
        }
    }

    override fun onItemAnswered(swipeView: View) {
        when (swipeView.id) {
            R.id.ivAnswerCall -> {

            }
            R.id.ivReplyCall -> {



            }
            R.id.ivRejectCall -> {

            }
        }
    }

    var ivAnswerCall: AppCompatImageView? = null
    var ivRejectCall: AppCompatImageView? = null
    var ivReplyCall: AppCompatImageView? = null
    var llReject: LinearLayout? = null
    var llReply: LinearLayout? = null
    var llAnswer: LinearLayout? = null
    var answerRoot: LinearLayout? = null
    private val animTime = 150
    var answerImagesList = ArrayList<AppCompatImageView>()
    var rejectImagesList = ArrayList<AppCompatImageView>()
    var replyImagesList = ArrayList<AppCompatImageView>()

    var tvRejectCall: AppCompatTextView? = null
    var tvReplyCall: AppCompatTextView? = null
    var tvAnswerCall: AppCompatTextView? = null

    var image: ImageView? = null

    var countDownTimer: CountDownTimer? = null
    val set = AnimatorSet()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call_v2)

        ivAnswerCall = findViewById(R.id.ivAnswerCall)
        ivRejectCall = findViewById(R.id.ivRejectCall)
        ivReplyCall = findViewById(R.id.ivReplyCall)
        llReject = findViewById(R.id.llReject)
        llReply = findViewById(R.id.llReply)
        llAnswer = findViewById(R.id.llAnswer)

        tvReplyCall = findViewById(R.id.tvReply)
        tvRejectCall = findViewById(R.id.tvReject)
        tvAnswerCall = findViewById(R.id.tvAnswer)

        answerRoot = findViewById(R.id.answerRoot)

        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_one))
        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_two))
        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_three))
        answerImagesList.add(findViewById(R.id.pick_call_arrow_up_four))

        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_one))
        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_two))
        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_three))
        replyImagesList.add(findViewById(R.id.reply_call_arrow_up_four))

        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_one))
        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_two))
        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_three))
        rejectImagesList.add(findViewById(R.id.reject_call_arrow_up_four))

        startAcceptAnimation(answerImagesList)
        startRejectAnimation(rejectImagesList)
        startReplyAnimation(replyImagesList)

        ivAnswerCall?.setOnTouchListener(CallTouchListener(answerRoot, ivAnswerCall, this))
        ivReplyCall?.setOnTouchListener(CallTouchListener(answerRoot, ivReplyCall, this))
        ivRejectCall?.setOnTouchListener(CallTouchListener(answerRoot, ivRejectCall, this))
        onShakeImage()
    }

    private fun startAcceptAnimation(imagesList: ArrayList<AppCompatImageView>) {

        try {
            set.cancel()
        } catch (e: Exception) {

        }

        for (image in imagesList) {
            image.clearAnimation()
        }
        val arrowOneFadeIn = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 0f, 1f)
        arrowOneFadeIn.duration = animTime.toLong()
        val arrowTwoFadeIn = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 0f, 1f)
        arrowTwoFadeIn.setDuration(animTime.toLong()).startDelay = animTime.toLong()
        val arrowOneFadeOut = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 1f, 0f)
        arrowOneFadeOut.setDuration(animTime.toLong()).startDelay = 375
        val arrowThreeFadeIn = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 0f, 1f)
        arrowThreeFadeIn.setDuration(animTime.toLong()).startDelay = 300
        val arrowTwoFadeOut = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 1f, 0f)
        arrowTwoFadeOut.setDuration(animTime.toLong()).startDelay = 525
        val arrowFourFadeIn = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 0f, 1f)
        arrowFourFadeIn.setDuration(animTime.toLong()).startDelay = 450
        val arrowThreeFadeOut = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 1f, 0f)
        arrowThreeFadeOut.setDuration(animTime.toLong()).startDelay = 675
        val arrowFourFadeOut = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 1f, 0f)
        arrowFourFadeOut.setDuration(animTime.toLong()).startDelay = 825

//        onShakeImage()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                set.start()
            }
        })

        set.play(arrowOneFadeIn)
        set.play(arrowTwoFadeIn)
        set.play(arrowOneFadeOut)
        set.play(arrowThreeFadeIn)
        set.play(arrowTwoFadeOut)
        set.play(arrowFourFadeIn)
        set.play(arrowThreeFadeOut)
        set.play(arrowFourFadeOut)
        set.start()
    }

    private fun startReplyAnimation(imagesList: ArrayList<AppCompatImageView>) {

        try {
            set.cancel()
        } catch (e: Exception) {

        }

        for (image in imagesList) {
            image.clearAnimation()
        }
        val arrowOneFadeIn = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 0f, 1f)
        arrowOneFadeIn.duration = animTime.toLong()
        val arrowTwoFadeIn = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 0f, 1f)
        arrowTwoFadeIn.setDuration(animTime.toLong()).startDelay = animTime.toLong()
        val arrowOneFadeOut = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 1f, 0f)
        arrowOneFadeOut.setDuration(animTime.toLong()).startDelay = 375
        val arrowThreeFadeIn = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 0f, 1f)
        arrowThreeFadeIn.setDuration(animTime.toLong()).startDelay = 300
        val arrowTwoFadeOut = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 1f, 0f)
        arrowTwoFadeOut.setDuration(animTime.toLong()).startDelay = 525
        val arrowFourFadeIn = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 0f, 1f)
        arrowFourFadeIn.setDuration(animTime.toLong()).startDelay = 450
        val arrowThreeFadeOut = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 1f, 0f)
        arrowThreeFadeOut.setDuration(animTime.toLong()).startDelay = 675
        val arrowFourFadeOut = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 1f, 0f)
        arrowFourFadeOut.setDuration(animTime.toLong()).startDelay = 825

//        onShakeImage()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                set.start()
            }
        })

        set.play(arrowOneFadeIn)
        set.play(arrowTwoFadeIn)
        set.play(arrowOneFadeOut)
        set.play(arrowThreeFadeIn)
        set.play(arrowTwoFadeOut)
        set.play(arrowFourFadeIn)
        set.play(arrowThreeFadeOut)
        set.play(arrowFourFadeOut)
        set.start()
    }

    private fun startRejectAnimation(imagesList: ArrayList<AppCompatImageView>) {

        try {
            set.cancel()
        } catch (e: Exception) {

        }

        for (image in imagesList) {
            image.clearAnimation()
        }
        val arrowOneFadeIn = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 0f, 1f)
        arrowOneFadeIn.duration = animTime.toLong()
        val arrowTwoFadeIn = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 0f, 1f)
        arrowTwoFadeIn.setDuration(animTime.toLong()).startDelay = animTime.toLong()
        val arrowOneFadeOut = ObjectAnimator.ofFloat(imagesList[3], View.ALPHA, 1f, 0f)
        arrowOneFadeOut.setDuration(animTime.toLong()).startDelay = 375
        val arrowThreeFadeIn = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 0f, 1f)
        arrowThreeFadeIn.setDuration(animTime.toLong()).startDelay = 300
        val arrowTwoFadeOut = ObjectAnimator.ofFloat(imagesList[2], View.ALPHA, 1f, 0f)
        arrowTwoFadeOut.setDuration(animTime.toLong()).startDelay = 525
        val arrowFourFadeIn = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 0f, 1f)
        arrowFourFadeIn.setDuration(animTime.toLong()).startDelay = 450
        val arrowThreeFadeOut = ObjectAnimator.ofFloat(imagesList[1], View.ALPHA, 1f, 0f)
        arrowThreeFadeOut.setDuration(animTime.toLong()).startDelay = 675
        val arrowFourFadeOut = ObjectAnimator.ofFloat(imagesList[0], View.ALPHA, 1f, 0f)
        arrowFourFadeOut.setDuration(animTime.toLong()).startDelay = 825

//        onShakeImage()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                set.start()
            }
        })

        set.play(arrowOneFadeIn)
        set.play(arrowTwoFadeIn)
        set.play(arrowOneFadeOut)
        set.play(arrowThreeFadeIn)
        set.play(arrowTwoFadeOut)
        set.play(arrowFourFadeIn)
        set.play(arrowThreeFadeOut)
        set.play(arrowFourFadeOut)
        set.start()
    }

    private fun onShakeImage() {
        val shake: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.shake)
        val slideUp: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up_call)
        image = findViewById(R.id.ivAnswerCall)
        image?.startAnimation(slideUp)

        Handler().postDelayed({
            image?.clearAnimation()
            image?.startAnimation(shake)
        }, 1500)

        Handler().postDelayed({ onShakeImage() }, 2700)

    }
}
