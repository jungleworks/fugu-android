package com.skeleton.mvp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.skeleton.mvp.R
import com.skeleton.mvp.constant.FuguAppConstant.REQ_CODE_INVITE_GUEST
import com.skeleton.mvp.ui.AppConstants
import com.skeleton.mvp.ui.base.BaseActivity
import com.skeleton.mvp.ui.fcinvite.InviteOnboardActivity

class InviteChooserActivity : BaseActivity() {

    private var cvInviteGuests: androidx.cardview.widget.CardView? = null
    private var cvInviteMembers: androidx.cardview.widget.CardView? = null
    private var ivBack: AppCompatImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_chooser)

        cvInviteMembers = findViewById(R.id.cvInviteMembers)
        cvInviteGuests = findViewById(R.id.cvInviteGuests)
        ivBack = findViewById(R.id.ivBack)

        ivBack?.setOnClickListener {
            onBackPressed()
        }

        cvInviteMembers?.setOnClickListener {
            val intent = Intent(this, InviteOnboardActivity::class.java)
            intent.putExtra(AppConstants.EXTRA_ALREADY_MEMBER, AppConstants.EXTRA_ALREADY_MEMBER)
            startActivity(intent)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

        cvInviteGuests?.setOnClickListener {
            val intent = Intent(this, InviteGuestsActivity::class.java)
            startActivityForResult(intent, REQ_CODE_INVITE_GUEST)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE_INVITE_GUEST) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
