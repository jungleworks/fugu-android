package com.skeleton.mvp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.*
import android.text.style.RelativeSizeSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.model.customAction.CustomAction
import com.skeleton.mvp.ui.profile.ProfileActivity
import com.skeleton.mvp.util.FormatStringUtil
import com.skeleton.mvp.util.ValidationUtil
import com.skeleton.mvp.utils.BetterLinkMovementMethod
import com.skeleton.mvp.utils.MyEmojiParser

class CustomActionsAdapter(context: Context, customActions: ArrayList<CustomAction>, muid: String, pos: Int) : androidx.recyclerview.widget.RecyclerView.Adapter<CustomActionsAdapter.MyViewHolder>() {
    var context: Context
    var customActions: ArrayList<CustomAction>
    var muid: String
    var pos: Int
    var mLastClickTime: Long = -1L

    init {
        this.context = context
        this.customActions = customActions
        this.muid = muid
        this.pos = pos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomActionsAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_action, parent, false))
    }

    override fun getItemCount(): Int {
        return customActions.size
    }

    override fun onBindViewHolder(holder: CustomActionsAdapter.MyViewHolder, position: Int) {
        val customAction = customActions.get(holder.adapterPosition)
        manipulateAndSetText(holder.tvQuestion, FormatStringUtil.FormatString.getFormattedString(customAction.title)[0], 1)
        val buttonsAdapter = ButtonsAdapter(context, customAction.buttons, muid, holder.adapterPosition, customAction.comment)
        if (customAction.defaultTextField != null) {
            buttonsAdapter.setData("", customAction.defaultTextField.minimumLength)
        }
        if (!customAction.isActionTaken) {
            holder.rvButtons.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(holder.itemView.context)
            holder.rvButtons.adapter = buttonsAdapter
            holder.rvButtons.visibility = View.VISIBLE

            if ((customAction.defaultTextField != null && TextUtils.isEmpty(customAction.comment)) || customAction.isShowTextField) {
                holder.rlTextField.visibility = View.VISIBLE
                if (customAction.defaultTextField != null && !TextUtils.isEmpty(customAction.defaultTextField.hint)) {
                    holder.etComment.hint = customAction.defaultTextField.hint
                } else {
                    holder.etComment.hint = "Please enter your comment ..."
                }
            } else {
                holder.rlTextField.visibility = View.GONE
            }

        } else {
            holder.rvButtons.visibility = View.GONE
            holder.rlTextField.visibility = View.GONE
        }

        holder.etComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                (context as ChatActivity).onTextChange(holder.adapterPosition, p0.toString(), muid)
            }

        })

        if (!TextUtils.isEmpty(customAction.remark)) {
            holder.tvRemark.visibility = View.VISIBLE
            manipulateAndSetText(holder.tvRemark, FormatStringUtil.FormatString.getFormattedString("*Remark:* " + customAction.remark)[0], 1)
        } else {
            holder.tvRemark.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(customAction.comment)) {
            holder.tvComment.visibility = View.VISIBLE
            manipulateAndSetText(holder.tvComment, FormatStringUtil.FormatString.getFormattedString("*Comment:* " + customAction.comment)[0], 1)
        } else {
            holder.tvComment.visibility = View.GONE
        }


    }

    private fun manipulateAndSetText(tvMessage: AppCompatTextView, message: String?, messageState: Int = 1) {
        var text: CharSequence = message.toString()
        text = text.toString().replace("\n", "<br>")
        val s = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text.toString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS) as Spannable
        } else {
            Html.fromHtml(text.toString()) as Spannable
        }
        for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
            s.setSpan(object : UnderlineSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.isUnderlineText = false
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0)
        }
        tvMessage.setLinkTextColor(context.resources.getColor(R.color.color_tag))
        tvMessage.setText(s)

        tvMessage.movementMethod = BetterLinkMovementMethod.getInstance()
        val replacements = MyEmojiParser.getUnicodeCandidates(s.toString())
        val messagee = MyEmojiParser.removeAllEmojis(s.toString())

        if (replacements.size in 1..2 && messagee.isEmpty()) {
            val ss1 = SpannableString(s)
            ss1.setSpan(RelativeSizeSpan(2f), 0, s.length, 0)
            tvMessage.text = ss1
        } else {
            if (messageState == 4) {
                val messagee = MyEmojiParser.removeAllEmojis(s.substring(0, s.length - 9))
                if (replacements.size in 1..2 && messagee.isEmpty()) {
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1f), s.length - 9, s.length, 0)
                    ss1.setSpan(RelativeSizeSpan(2f), 0, s.length - 9, 0)
                    tvMessage.text = ss1
                } else {
                    val ss1 = SpannableString(s)
                    ss1.setSpan(RelativeSizeSpan(1f), 0, s.length, 0)
                    tvMessage.text = ss1
                }
            } else {
                val ss1 = SpannableString(s)
                ss1.setSpan(RelativeSizeSpan(1f), 0, s.length, 0)
                tvMessage.text = ss1
            }
        }

        if (!tvMessage.text.toString().toLowerCase().contains("http")) {
            BetterLinkMovementMethod.linkifyHtmlNone(tvMessage).setOnLinkClickListener(urlClickListener)
        } else {
            BetterLinkMovementMethod.linkifyHtml(tvMessage).setOnLinkClickListener(urlClickListener)
        }
    }

    private val urlClickListener = BetterLinkMovementMethod.OnLinkClickListener { _, url ->
        if (url.toLowerCase().contains("http")) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        } else {
            try {
                openProfile(url, context)
            } catch (e: Exception) {

            }

        }
        true
    }

    private fun openProfile(url: String, activity: Context) {
        @Suppress("NAME_SHADOWING")
        var url = url

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        if (!TextUtils.isEmpty(url.trim { it <= ' ' })) {
            val mIntent = Intent(Intent(activity, ProfileActivity::class.java))
            if (!ValidationUtil.checkEmail(url)) {
                url = url.split("mention://".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            if (url == com.skeleton.mvp.data.db.CommonData.getCommonResponse().data.workspacesInfo[com.skeleton.mvp.data.db.CommonData.getCurrentSignedInPosition()].userId.toString()) {
                mIntent.putExtra("no_chat", "no_chat")
            }
            mIntent.putExtra("open_profile", url)
            if (url != "-1") {
                activity.startActivity(mIntent)
            }
        }
    }

    class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvQuestion: AppCompatTextView = itemView.findViewById(R.id.tvQuestion)
        val tvComment: AppCompatTextView = itemView.findViewById(R.id.tvComment)
        val tvRemark: AppCompatTextView = itemView.findViewById(R.id.tvRemark)
        val etComment: EditText = itemView.findViewById(R.id.etComment)
        val rlTextField: RelativeLayout = itemView.findViewById(R.id.rlTextField)
        val rvButtons: androidx.recyclerview.widget.RecyclerView = itemView.findViewById(R.id.rvButtons)
    }

    interface TextChange {
        fun onTextChange(pos: Int, text: String, muid: String)
    }

}