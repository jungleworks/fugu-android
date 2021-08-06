package io.github.memfis19.cadar.data.entity;

import java.io.Serializable;
import java.util.Date;

import io.github.memfis19.cadar.data.entity.property.EventProperties;
import io.github.memfis19.cadar.internal.utils.SyncUtils;

/**
 * Created by memfis on 3/25/15.
 * Representation of the main entity for events in stp calendar.
 */
public interface Event extends Serializable {

    Long getEventId();

    String getEventTitle();

    String getEventDescription();

    Long getEventAssignerId();

    Date getOriginalEventStartDate();

    Date getEventStartDate();

    /***
     * Sets the new start date for new generating event.
     *
     * @param startEventDate
     */
    void setEventStartDate(Date startEventDate);

    Date getEventEndDate();

    /***
     * Sets the new end date for new generating event.
     *
     * @param endEventDate
     */
    void setEventEndDate(Date endEventDate);
//
//    Date getEventRemindDate();
//
//    /***
//     * Sets the new remind date for new generating event.
//     *
//     * @param endEventDate
//     */
//    void setEventRemindDate(Date endEventDate);

    /***
     * Return calendarId field generated with system calendar after sync it
     *
     * @return
     * @see SyncUtils
     */
    Long getCalendarId();

    /***
     * Sets calendarId field generated with system calendar after sync it
     *
     * @param calendarId
     * @see SyncUtils
     */
    void setCalendarId(Long calendarId);

    /***
     * @return repeat period for current entity instance
     * @see EventProperties.RepeatPeriod
     */
    @EventProperties.RepeatPeriod
    int getEventRepeatPeriod();

    /***
     * @return event icon url
     */
    String getEventIconUrl();

    /***
     * @return boolean which indicates is event editable
     */
    Boolean isEditable();

    /***
     * @return boolean which indicates is event length All day long
     */
    Boolean isAllDayEvent();
}

