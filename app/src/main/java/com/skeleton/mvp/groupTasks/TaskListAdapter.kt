package com.skeleton.mvp.groupTasks

/********************************
Created by Amandeep Chauhan     *
Date :- 05/08/2020              *
 ********************************/

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.skeleton.mvp.R
import com.skeleton.mvp.data.db.CommonData
import com.skeleton.mvp.data.model.groupTasks.TaskDetail
import com.skeleton.mvp.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class TaskListAdapter(private var eventList: ArrayList<TaskDetail>, private val mContext: Context) : RecyclerView.Adapter<TaskViewHolder>() {

    private val formatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    val dateUtils: DateUtils = DateUtils.getInstance()
    val userId = CommonData.getCommonResponse().data.workspacesInfo[CommonData.getCurrentSignedInPosition()].userId.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_list_row_item, parent, false))
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskDetail = eventList[holder.adapterPosition]
        holder.tvTaskTitle.text = taskDetail.title
        holder.tvTaskDescription.text = taskDetail.description
        if (taskDetail.assignerID == userId) {
            holder.ivTaskStatusIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_task_completed))
            holder.tvTaskStatus.text = "Assigned by You"
        } else if (taskDetail.isCompleted == 1) {
            holder.ivTaskStatusIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_task_completed))
            val date = getFormattedDate(taskDetail.submissionDatetime)
            holder.tvTaskStatus.text = if (date != null) "Completed On $date" else "Completed"
        } else {
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_YEAR, 1)
            val taskEndTime = Calendar.getInstance()
            taskEndTime.time = dateUtils.getLocalDateObject(taskDetail.endDateTime)
            if (tomorrow.get(Calendar.DAY_OF_YEAR) == taskEndTime.get(Calendar.DAY_OF_YEAR)
                    && tomorrow.get(Calendar.YEAR) == taskEndTime.get(Calendar.YEAR)) {
                holder.ivTaskStatusIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_task_deadline))
                holder.tvTaskStatus.text = "Deadline Tomorrow"
            } else if (Calendar.getInstance().timeInMillis > getTaskTimeInMillis(taskDetail.endDateTime)) {
                holder.ivTaskStatusIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_task_expired))
                holder.tvTaskStatus.text = "Task Expired"
            } else {
                holder.ivTaskStatusIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_task_pending))
                val date = getFormattedDate(taskDetail.endDateTime)
                holder.tvTaskStatus.text = if (date != null) "Deadline $date" else "Pending"
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, TaskActivity::class.java)
            intent.putExtra("isViewMode", true)
            intent.putExtra("eventId", taskDetail.taskID)
            mContext.startActivity(intent)
        }
    }

    fun updateList(eventList: ArrayList<TaskDetail>) {
        this.eventList = eventList
        notifyDataSetChanged()
    }

    private fun getFormattedDate(submissionDatetime: String): String? {
        return formatter.format(dateUtils.getLocalDateObject(submissionDatetime))
    }

    private fun getTaskTimeInMillis(submissionDatetime: String): Long {
        return dateUtils.getLocalDateObject(submissionDatetime).time
    }

}

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val ivTaskStatusIcon: AppCompatImageView = itemView.findViewById(R.id.ivTaskStatusIcon)
    val tvTaskTitle: AppCompatTextView = itemView.findViewById(R.id.tvTaskTitle)
    val tvTaskStatus: AppCompatTextView = itemView.findViewById(R.id.tvTaskStatus)
    val tvTaskDescription: AppCompatTextView = itemView.findViewById(R.id.tvTaskDescription)
}
