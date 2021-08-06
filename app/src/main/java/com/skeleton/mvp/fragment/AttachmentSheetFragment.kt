package com.skeleton.mvp.fragment

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.activity.FuguInnerChatActivity
import com.skeleton.mvp.constant.FuguAppConstant


class AttachmentSheetFragment : BottomSheetDialogFragment(), View.OnClickListener {

    val chatActivity: ChatActivity? = null
    var llCamera: LinearLayout? = null
    var llGallery: LinearLayout? = null
    var llVideo: LinearLayout? = null
    var llAudio: LinearLayout? = null
    var llFiles: LinearLayout? = null
    var llPoll: LinearLayout? = null
    var chatType = FuguAppConstant.ChatType.O2O
    var isThread = false

    override fun onClick(v: View?) {
        dismiss()
        if (activity is ChatActivity) {
            (activity as ChatActivity).openScreenFromSheet(v?.id)
        } else {
            (activity as FuguInnerChatActivity).openScreenFromSheet(v?.id)
        }
    }

    fun newInstance(arg: Int, chatType: Int, isThread: Boolean): AttachmentSheetFragment {
        val frag = AttachmentSheetFragment()
        val args = Bundle()
        frag.arguments = args
        frag.setChatTypee(chatType)
        frag.setIsThread(isThread)
        return frag
    }

    private fun setIsThread(thread: Boolean) {
        this.isThread = thread
    }

    private fun setChatTypee(chatType: Int) {
        this.chatType = chatType
    }


    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme
    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        BottomSheetDialog(requireContext(), theme)
        val view = inflater.inflate(R.layout.attachmnet_bottom_sheet, container, false)

        llCamera = view.findViewById(R.id.llCamera)
        llGallery = view.findViewById(R.id.llGallery)
        llAudio = view.findViewById(R.id.llAudio)
        llPoll = view.findViewById(R.id.llPoll)
        llFiles = view.findViewById(R.id.llFiles)
        llVideo = view.findViewById(R.id.llVideo)

        llCamera?.setOnClickListener(this)
        llGallery?.setOnClickListener(this)
        llAudio?.setOnClickListener(this)
        llPoll?.setOnClickListener(this)
        llFiles?.setOnClickListener(this)
        llVideo?.setOnClickListener(this)

        if ((chatType == FuguAppConstant.ChatType.O2O || chatType == FuguAppConstant.ChatType.BOT) || isThread) {
            llPoll?.visibility = View.GONE
        } else {
            llPoll?.visibility = View.VISIBLE
        }
        return view

    }
}
