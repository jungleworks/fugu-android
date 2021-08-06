package com.skeleton.mvp.fragment

/**
 * Created by rajatdhamija
 * 19/04/18.
 */

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import com.skeleton.mvp.R
import com.skeleton.mvp.adapter.PollUsersAdapter
import com.skeleton.mvp.model.User
import java.util.*


class PollUsersBottomSheetFragment : BottomSheetDialogFragment() {
    private var rvUsers: androidx.recyclerview.widget.RecyclerView? = null
    private var users: ArrayList<User>? = null
    private var ivCross: AppCompatImageView? = null
    private var cboption: CheckBox? = null
    private var radioOption: RadioButton? = null
    private var pollUserAdapter: PollUsersAdapter? = null
    private var label: String = ""
    private var isMultiSelect = false
    private var isChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.poll_user_dialog_fragment, container)
        rvUsers = view.findViewById(R.id.rvUsers)
        cboption = view.findViewById(R.id.cboption)
        radioOption = view.findViewById(R.id.radioOption)
        rvUsers?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        pollUserAdapter = PollUsersAdapter(context!!, users!!)
        rvUsers?.adapter = pollUserAdapter
        ivCross = view.findViewById(R.id.ivCross)

        if (isMultiSelect) {
            cboption?.visibility = View.VISIBLE
            radioOption?.visibility = View.GONE
            cboption?.isChecked = isChecked
            cboption?.text = label
        } else {
            radioOption?.visibility = View.VISIBLE
            cboption?.visibility = View.GONE
            radioOption?.isChecked = isChecked
            radioOption?.text = label
        }

        ivCross?.setOnClickListener {
            dismiss()
        }
        return view
    }

    companion object {

        fun newInstance(arg: Int, context: Context, users: ArrayList<User>, isMultiSelect: Boolean, label: String, isChecked: Boolean): PollUsersBottomSheetFragment {
            val frag = PollUsersBottomSheetFragment()
            val args = Bundle()
            frag.arguments = args
            frag.setUsers(users)
            frag.setIsMultiSelect(isMultiSelect)
            frag.setLabel(label)
            frag.setChecked(isChecked)
            return frag
        }
    }

    private fun setChecked(checked: Boolean) {
        this.isChecked = checked
    }

    private fun setLabel(label: String) {
        this.label = label
    }

    private fun setIsMultiSelect(multiSelect: Boolean) {
        this.isMultiSelect = multiSelect
    }

    private fun setUsers(users: ArrayList<User>) {
        this.users = users
    }

}
