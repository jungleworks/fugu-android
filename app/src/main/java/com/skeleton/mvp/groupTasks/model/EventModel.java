package com.skeleton.mvp.groupTasks.model;

/********************************
 Created by Amandeep Chauhan     *
 Date :- 05/08/2020              *
 ********************************/

import com.skeleton.mvp.calendarView.DateUtils;
import com.skeleton.mvp.data.model.groupTasks.TaskDetail;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.data.entity.property.EventProperties;

public class EventModel implements Event, Serializable {

    private long id = System.currentTimeMillis();
    private long assignerId = -1L;
    private String title = "Title of task #" + id;
    private String description = "This is the description of the task.";
    private Date originalStartDate = new Date();
    private Date startDate = (Date) originalStartDate.clone();
    private Date endDate = new Date();
//    private Date remindDate = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public EventModel() {
    }

    public EventModel(TaskDetail task) {
        this.id = task.getTaskID();
        this.assignerId = task.getAssignerID();
        this.title = task.getTitle();
        this.description = task.getDescription();
        DateUtils utils = DateUtils.getInstance();
        try {
            this.originalStartDate = sdf.parse(utils.convertToLocal(task.getStartDateTime()));
            this.startDate = sdf.parse(utils.convertToLocal(task.getStartDateTime()));
            this.endDate = sdf.parse(utils.convertToLocal(task.getEndDateTime()));
//            this.remindDate = sdf.parse(utils.convertToLocal(task.getReminder()));
        } catch (Exception ignore) {
        }
    }

    public EventModel(Event event) {
        this.id = event.getEventId();
        this.assignerId = event.getEventAssignerId();
        this.title = event.getEventTitle();
        this.description = event.getEventDescription();
        this.originalStartDate = event.getOriginalEventStartDate();
        this.startDate = event.getEventStartDate();
        this.endDate = event.getEventEndDate();
//        this.remindDate = event.getEventRemindDate();
    }

    @Override
    public Long getEventId() {
        return id;
    }

    @Override
    public String getEventTitle() {
        return title;
    }

    @Override
    public String getEventDescription() {
        return description;
    }

    @Override
    public Long getEventAssignerId() {
        return assignerId;
    }

    @Override
    public Date getOriginalEventStartDate() {
        return originalStartDate;
    }

    @Override
    public Date getEventStartDate() {
        return startDate;
    }

    @Override
    public void setEventStartDate(Date startEventDate) {
        this.startDate = startEventDate;
    }

    @Override
    public Date getEventEndDate() {
        return endDate;
    }

    @Override
    public void setEventEndDate(Date endEventDate) {
        this.endDate = endEventDate;
    }

//    @Override
//    public Date getEventRemindDate() {
//        return remindDate;
//    }
//
//    @Override
//    public void setEventRemindDate(Date endRemindDate) {
//        this.remindDate = endRemindDate;
//    }

    @Override
    public Long getCalendarId() {
        return 0l;
    }

    @Override
    public void setCalendarId(Long calendarId) {

    }

    @Override
    public int getEventRepeatPeriod() {
        return EventProperties.EVERY_WEEK;
    }

    @Override
    public String getEventIconUrl() {
        return "";
    }

    @Override
    public Boolean isEditable() {
//        WorkspacesInfo workspaceInfo = CommonData.getCommonResponse().getData().getWorkspacesInfo().get(CommonData.getCurrentSignedInPosition());
//        return getEventAssignerId().compareTo(Long.valueOf(workspaceInfo.getUserId())) == 0;
        return false;
    }

    @Override
    public Boolean isAllDayEvent() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getEventId() == 0) ? 0 : Long.valueOf(this.getEventId()).hashCode());
        result = prime * result + ((this.getEventStartDate() == null) ? 0 : this.getEventStartDate().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (((Object) this).getClass() != obj.getClass())
            return false;

        EventModel other = (EventModel) obj;

        if (this.getEventId() == 0) {
            if (other.getEventId() != 0)
                return false;
        } else if (getEventId() != other.getEventId())
            return false;

        if (this.getEventStartDate() == null) {
            return other.getEventStartDate() == null;
        } else return getEventStartDate().equals(other.getEventStartDate());

    }
}
