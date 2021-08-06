package com.skeleton.mvp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.PollExpirySelectAdapter


class PollRecyclerDialogFragment : DialogFragment() {
    private var rvOptions: androidx.recyclerview.widget.RecyclerView? = null
    private var numberList = ArrayList<String>()
    private var maxValue: Int = 0
    private var selectedValue: Int = 0
    private var minValue: Int = 0
    private var contextt: Context? = null
    private var type: String = ""
    private lateinit var pollExpirySelectAdapter: PollExpirySelectAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.poll_dialog_fragment, container)
        rvOptions = view.findViewById(R.id.rvOptions)
        if (type.equals("Days")) {
            for (i in minValue..maxValue) {
                numberList.add(i.toString())
            }
        } else {
            for (i in minValue..maxValue) {
                if (i <= 9) {
                    numberList.add("0" + i)
                } else {
                    numberList.add(i.toString())
                }
            }
        }
        pollExpirySelectAdapter = PollExpirySelectAdapter(contextt!!, numberList, selectedValue, type)
        rvOptions?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(contextt)
        rvOptions?.adapter = pollExpirySelectAdapter
        val decoration = androidx.recyclerview.widget.DividerItemDecoration(contextt, VERTICAL)
        rvOptions?.addItemDecoration(decoration)
        return view
    }

    override fun onStart() {
        super.onStart()
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    companion object {

        fun newInstance(arg: Int, context: Context, maxValue: Int, selectedValue: Int, type: String, minValue: Int): PollRecyclerDialogFragment {
            val frag = PollRecyclerDialogFragment()
            val args = Bundle()
            frag.arguments = args
            frag.setMaxValue(maxValue)
            frag.setSelectedValue(selectedValue)
            frag.setType(type)
            frag.setContext(context)
            frag.setMinValue(minValue)
            return frag

        }
    }

    private fun setMinValue(minValue: Int) {
        this.minValue = minValue
    }

    private fun setContext(context: Context) {
        this.contextt = context
    }

    private fun setType(type: String) {
        this.type = type
    }

    private fun setSelectedValue(selectedValue: Int) {
        this.selectedValue = selectedValue
    }

    private fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
    }

}
