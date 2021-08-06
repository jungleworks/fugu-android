package com.skeleton.mvp.fragment

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.app.AlertDialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.skeleton.mvp.R
import com.skeleton.mvp.activity.ChatActivity
import com.skeleton.mvp.adapter.FuguAttachmentAdapter
import com.skeleton.mvp.constant.FuguAppConstant
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.utils.DateUtils

import java.util.ArrayList
import java.util.Arrays
import java.util.Date

import com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_DELIVERED
import com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_READ
import com.skeleton.mvp.constant.FuguAppConstant.MESSAGE_SENT


class GalleryBottomSheetFragment : DialogFragment(), View.OnClickListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.gallery_bottom_sheet, container, false)
        val rvAttachment = view.findViewById(R.id.rvAttachment) as androidx.recyclerview.widget.RecyclerView
        rvAttachment.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 3)
        val fuguAttachmentAdaper = FuguAttachmentAdapter(activity, true, false)
        rvAttachment.adapter = fuguAttachmentAdaper
        return view
    }

    override fun onClick(v: View) {}

    companion object {

        fun newInstance(arg: Int): GalleryBottomSheetFragment {
            val frag = GalleryBottomSheetFragment()
            val args = Bundle()
            frag.arguments = args
            return frag
        }
    }

}
