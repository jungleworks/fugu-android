package com.skeleton.mvp.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.view.ViewTreeObserver
import android.widget.*
import com.skeleton.mvp.R
import com.skeleton.mvp.activity.PollDetailsActivity
import com.skeleton.mvp.fragment.PollUsersBottomSheetFragment
import com.skeleton.mvp.model.User
import org.json.JSONObject


class PollOptionsAdapter(context: Context, optionsList: ArrayList<Option>,
                         isMultipleSelect: Boolean, userId: Long, totalVotes: Int, userName: String, userImage: String, isExpired: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<PollOptionsAdapter.MyViewHolder>() {
    private var context: Context
    private var optionsList: ArrayList<Option>
    private var isMultipleSelect: Boolean
    private var userId: Long
    private var totalVotes: Int
    private var userImage: String
    private var userName: String
    private var isExpired = false

    init {
        this.context = context
        this.optionsList = optionsList
        this.isMultipleSelect = isMultipleSelect
        this.userId = userId
        this.totalVotes = totalVotes
        this.userImage = userImage
        this.userName = userName
        this.isExpired = isExpired
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollOptionsAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_poll_option, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PollOptionsAdapter.MyViewHolder, pos: Int) {
        val position = holder.adapterPosition
        val option = optionsList[position]
        if (isMultipleSelect) {
            holder.rlCheckBox.visibility = View.VISIBLE
            holder.rlRadioButton.visibility = View.GONE
            holder.cbOption.text = option.label
            if (option.voteMap?.size == 1) {
                holder.cbVotes.text = option.voteMap.size.toString() + " vote"
            } else {
                holder.cbVotes.text = option.voteMap?.size.toString() + " votes"
            }
            if (option.voteMap!![userId] != null) {
                holder.cbOption.isChecked = true
                holder.llCheckBox.setBackgroundResource(R.drawable.rectangle_border_radio_blue)

            } else {
                holder.cbOption.isChecked = false
                holder.llCheckBox.setBackgroundResource(R.drawable.rectangle_border_radio_gray)

            }
            try {
                setFilleColorCheckBox(option, holder)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            holder.cbVotes.setOnClickListener {
                if (option.users.size > 0) {
                    val manager = (context as PollDetailsActivity).supportFragmentManager
                    val ft = manager.beginTransaction()

                    val newFragment = PollUsersBottomSheetFragment.newInstance(0, context, option.users as ArrayList<User>, true, option.label!!, holder.cbOption.isChecked)
                    newFragment.show(ft, "PollUsersFragmentDialog")
                } else {
                    Toast.makeText(context, "No votes yet!", Toast.LENGTH_LONG).show()
                }
            }
            if (!isExpired) {
                holder.cbOption.setOnClickListener {
                    val jsonObject = JSONObject()
                    jsonObject.put("puid", option.puid)
                    holder.cbOption.isChecked = !holder.cbOption.isChecked
                    if (!holder.cbOption.isChecked) {
                        totalVotes += 1
                        val user = User()
                        user.userId = userId
                        user.userImage = userImage
                        user.fullName = userName
                        option.voteMap.put(userId, user)
                        option.users.add(user)
                        optionsList[position] = option
                        jsonObject.put("is_voted", true)
                    } else {
                        totalVotes -= 1
                        option.voteMap.remove(userId)
                        for (j in 0..option.users.size - 1) {
                            if (option.users[j].userId.compareTo(userId) == 0) {
                                option.users.removeAt(j)
                                break
                            }
                        }
                        optionsList[position] = option
                        jsonObject.put("is_voted", false)

                    }
                    (context as PollDetailsActivity).onTotalVotesChanged(totalVotes, option.voteMap, holder.adapterPosition, jsonObject, optionsList)

                }
            } else {
                holder.cbOption.isFocusableInTouchMode = false
                holder.cbOption.isClickable = false
            }
        } else {
            if (option.voteMap!![userId] != null) {
                holder.radioOption.isChecked = true
                holder.llRadio.setBackgroundResource(R.drawable.rectangle_border_radio_blue)

            } else {
                holder.radioOption.isChecked = false
                holder.llRadio.setBackgroundResource(R.drawable.rectangle_border_radio_gray)

            }
            holder.rlCheckBox.visibility = View.GONE
            holder.rlRadioButton.visibility = View.VISIBLE
            holder.radioOption.text = option.label
            if (option.voteMap?.size == 1) {
                holder.radioVotes.text = option.voteMap.size.toString() + " vote"
            } else {
                holder.radioVotes.text = option.voteMap?.size.toString() + " votes"
            }
            try {
                setFilleColorCheckRadio(option, holder)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.radioVotes.setOnClickListener {
                if (option.users.size > 0) {
                    val manager = (context as PollDetailsActivity).supportFragmentManager
                    val ft = manager.beginTransaction()

                    val newFragment = PollUsersBottomSheetFragment.newInstance(0, context, option.users as ArrayList<User>, false, option.label!!, holder.radioOption.isChecked)
                    newFragment.show(ft, "PollUsersFragmentDialog")
                } else {
                    Toast.makeText(context, "No votes yet!", Toast.LENGTH_LONG).show()
                }
            }
            if (!isExpired) {
                holder.radioOption.setOnClickListener {
                    val jsonObject = JSONObject()
                    jsonObject.put("puid", option.puid)
                    jsonObject.put("is_voted", true)
                    if (holder.radioOption.isChecked) {
                        for (i in 0..optionsList.size - 1) {
                            val option = optionsList[i]
                            if (option.voteMap!![userId] != null) {
                                option.voteMap.remove(userId)
                                for (j in 0..option.users.size - 1) {
                                    if (option.users[j].userId.compareTo(userId) == 0) {
                                        option.users.removeAt(j)
                                        break
                                    }
                                }
                                optionsList[i] = option
                                totalVotes -= 1
                                break
                            }
                        }

                        holder.radioOption.isChecked = true
                        totalVotes += 1
                        val user = User()
                        user.userId = userId
                        user.userImage = userImage
                        user.fullName = userName
                        option.users.add(user)
                        option.voteMap?.put(userId, user)
                        optionsList[position] = option
                    }
                    (context as PollDetailsActivity).onTotalVotesChanged(totalVotes, option.voteMap, holder.adapterPosition, jsonObject, optionsList)
                }
            } else {
                holder.radioOption.isFocusableInTouchMode = false
                holder.radioOption.isClickable = false
            }
        }
    }

    private fun setFilleColorCheckRadio(option: Option, holder: MyViewHolder) {
        if (option.voteMap?.size!! > 0) {
            val params = holder.radioView.getLayoutParams()
            val viewTreeObserver = holder.rlRadioButton.getViewTreeObserver()
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        holder.rlRadioButton.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        val width = holder.rlRadioButton.width * (option.voteMap.size) / totalVotes
                        val heeight = holder.rlRadioButton.height - 20
                        params.width = width
                        params.height = heeight
                        holder.radioView.layoutParams = params
                        holder.radioView.invalidate()
                        holder.radioView.visibility = View.VISIBLE
                    }
                })
            }

        } else {
            holder.radioView.visibility = View.GONE
        }
    }

    private fun setFilleColorCheckBox(option: Option, holder: MyViewHolder) {
        if (option.voteMap?.size!! > 0) {
            val params = holder.cbView.getLayoutParams()

            val viewTreeObserver = holder.rlCheckBox.getViewTreeObserver()
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        holder.rlCheckBox.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                        val width = holder.rlCheckBox.width * (option.voteMap.size) / totalVotes
                        val heeight = holder.rlCheckBox.height - 20
                        params.width = width
                        params.height = heeight
                        holder.cbView.layoutParams = params
                        holder.cbView.invalidate()
                        holder.cbView.visibility = View.VISIBLE
                    }
                })
            }

        } else {
            holder.cbView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    inner class MyViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var rlCheckBox: RelativeLayout = itemView.findViewById(R.id.rlCheckBox)
        var rlRadioButton: RelativeLayout = itemView.findViewById(R.id.rlRadioButton)
        var llCheckBox: LinearLayout = itemView.findViewById(R.id.llCheckBox)
        var llRadio: LinearLayout = itemView.findViewById(R.id.llRadio)
        var cbOption: CheckBox = itemView.findViewById(R.id.cbOption)
        var radioOption: RadioButton = itemView.findViewById(R.id.radioOption)
        var cbVotes: AppCompatTextView = itemView.findViewById(R.id.cbVotes)
        var radioVotes: AppCompatTextView = itemView.findViewById(R.id.radioVotes)
        var cbView: View = itemView.findViewById(R.id.cbView)
        var radioView: View = itemView.findViewById(R.id.radioView)
    }

    private fun dpToPx(dpParam: Int): Int {
        val d = context.getResources().getDisplayMetrics().density
        return (dpParam * d).toInt()
    }

    interface OnTotalVotesChanged {
        fun onTotalVotesChanged(totalVotes: Int, voteMap: HashMap<Long, User>, pos: Int, jsonObject: JSONObject, optionsList: ArrayList<Option>)
    }

    data class Option(val puid: String?, val label: String?, val voteMap: HashMap<Long, User>?, val users: ArrayList<User>)
}
