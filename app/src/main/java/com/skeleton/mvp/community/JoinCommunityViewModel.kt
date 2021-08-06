package com.skeleton.mvp.community

/********************************
Created by Amandeep Chauhan     *
Date :- 29/06/2020              *
 ********************************/

import android.view.View
import androidx.lifecycle.ViewModel

class JoinCommunityViewModel : ViewModel() {
    var membersCount: String? = null
    var email: String? = null
    var communityListener: CommunityListener? = null

    fun onJoin(v: View) {
        communityListener?.onJoin()
    }

    fun onGetInvite(v: View) {
        communityListener?.onGetInvite()
    }

    fun onSignIn(v: View) {
        communityListener?.onSignIn()
    }

    fun onBackPressed(v: View) {
        communityListener?.onBackPressed()
    }
}