package com.skeleton.mvp.groupTasks;

/********************************
 Created by Amandeep Chauhan     *
 Date :- 05/08/2020              *
 ********************************/

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.data.db.CommonData;
import com.skeleton.mvp.data.model.fcCommon.WorkspacesInfo;
import com.skeleton.mvp.data.model.groupTasks.GetTaskResponse;
import com.skeleton.mvp.data.model.groupTasks.TaskDetail;
import com.skeleton.mvp.data.network.ApiError;
import com.skeleton.mvp.data.network.ResponseResolver;
import com.skeleton.mvp.data.network.RestClient;
import com.skeleton.mvp.groupTasks.model.EventModel;
import com.skeleton.mvp.retrofit.CommonParams;
import com.skeleton.mvp.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.cadar.CalendarController;
import io.github.memfis19.cadar.data.entity.Event;
import io.github.memfis19.cadar.event.CalendarPrepareCallback;
import io.github.memfis19.cadar.event.OnEventClickListener;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.MonthDayDecorator;
import io.github.memfis19.cadar.internal.ui.month.adapter.decorator.factory.MonthDayDecoratorFactory;
import io.github.memfis19.cadar.internal.utils.DateUtils;
import io.github.memfis19.cadar.settings.ListCalendarConfiguration;
import io.github.memfis19.cadar.settings.MonthCalendarConfiguration;
import io.github.memfis19.cadar.view.ListCalendar;
import io.github.memfis19.cadar.view.MonthCalendar;

public class TaskCalendarActivity extends BaseActivity implements CalendarPrepareCallback {

    private MonthCalendar monthCalendar;
    private ListCalendar listCalendar;
    private List<Event> events = new ArrayList<>();
    private Calendar currentCalendar = Calendar.getInstance();
    private int taskType = 3;
    private int retryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_calendar);

        monthCalendar = findViewById(R.id.monthCalendar);
        listCalendar = findViewById(R.id.listCalendar);

        Spinner spinnerTaskType = findViewById(R.id.spinnerTaskType);
        ImageView ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> onBackPressed());

        MonthCalendarConfiguration.Builder builder = new MonthCalendarConfiguration.Builder();
        ListCalendarConfiguration.Builder listBuilder = new ListCalendarConfiguration.Builder();

        MonthDayDecoratorFactory monthDayDecoratorFactory = MonthDayDecoratorImpl::new;

        monthCalendar.setCalendarPrepareCallback(this);
        listCalendar.setCalendarPrepareCallback(this);

        builder.setDisplayPeriod(Calendar.MONTH, 8);
        builder.setMonthDayLayout(R.layout.month_calendar_event_layout, monthDayDecoratorFactory);
        listBuilder.setDisplayPeriod(Calendar.MONTH, 8);
//        builder.setEventsProcessor(new MonthCustomProcessor());
//        listBuilder.setEventsProcessor(new ListCustomEventProcessor());

        monthCalendar.prepareCalendar(builder.build());
        listCalendar.prepareCalendar(listBuilder.build());

        monthCalendar.setOnDayChangeListener(calendar -> listCalendar.setSelectedDay(DateUtils.setTimeToMidnight((Calendar) calendar.clone()), false));
        monthCalendar.setOnMonthChangeListener(calendar -> {
            listCalendar.setSelectedDay(DateUtils.setTimeToMonthStart((Calendar) calendar.clone()), false);
            currentCalendar = (Calendar) calendar.clone();
            fetchAndUpdateTasks(null);
        });

        listCalendar.setOnDayChangeListener(calendar -> monthCalendar.setSelectedDay(calendar, false));

        listCalendar.setOnMonthChangeListener(calendar -> {
            monthCalendar.setSelectedDay(calendar, true);
            currentCalendar = (Calendar) calendar.clone();
            fetchAndUpdateTasks(null);
        });

        listCalendar.setOnEventClickListener(new OnEventClickListener() {
            @Override
            public void onEventClick(Event event, int position) {
                viewTask(event);
            }

            @Override
            public void onEventLongClick(Event event, int position) {

            }

            @Override
            public void onSyncClick(Event event, int position) {

            }
        });

        spinnerTaskType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        taskType = 3;
                        break;
                    case 1:
                        taskType = 1;
                        break;
                    case 2:
                        taskType = 0;
                        break;
                    case 3:
                        taskType = 2;
                        break;
                }
                fetchAndUpdateTasks(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onCalendarReady(CalendarController controller) {
        fetchAndUpdateTasks(controller);
    }

    void fetchAndUpdateTasks(CalendarController calendarController) {
        showLoading();
        String accessToken = CommonData.getCommonResponse().data.userInfo.accessToken;
        com.skeleton.mvp.data.model.fcCommon.Data commonResponseData = CommonData.getCommonResponse().data;
        List<WorkspacesInfo> workspacesInfo = commonResponseData.getWorkspacesInfo();
        int currentPosition = CommonData.getCurrentSignedInPosition();
        CommonParams.Builder commonParams = new CommonParams.Builder()
                .add(FuguAppConstant.USER_ID, workspacesInfo.get(currentPosition).getUserId())
                .add("month", currentCalendar.get(Calendar.MONTH) + 1)
                .add("year", currentCalendar.get(Calendar.YEAR))
                .add("workspace_id", workspacesInfo.get(currentPosition).getWorkspaceId());
        if (taskType != 3)
            commonParams.add("is_completed", taskType);
        RestClient.getApiInterface(true).getAssignedTasks(accessToken, FuguAppConstant.ANDROID_USER, BuildConfig.VERSION_CODE, commonParams.build().getMap())
                .enqueue(new ResponseResolver<GetTaskResponse>() {
                    @Override
                    public void onSuccess(GetTaskResponse getTaskResponse) {
                        hideLoading();
                        try {
                            if (getTaskResponse.getData() != null) {
                                events.clear();
                                for (TaskDetail task : getTaskResponse.getData()) {
                                    events.add(new EventModel(task));
                                }
                            }
                            if (calendarController == null) {
                                monthCalendar.displayEvents(new ArrayList<>(events), month -> {
                                });
                                listCalendar.displayEvents(new ArrayList<>(events), period -> {
                                });
                            } else if (calendarController == monthCalendar) {
                                monthCalendar.displayEvents(new ArrayList<>(events), month -> {

                                });
                            } else if (calendarController == listCalendar) {
                                listCalendar.displayEvents(new ArrayList<>(events), period -> {

                                });
                            }
                        } catch (Exception ignored) {

                        }
                    }

                    @Override
                    public void onError(ApiError error) {
                        hideLoading();
                        showErrorMessage(error.getMessage());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        hideLoading();
                        if (retryCount < 3)
                            showErrorMessage("Connection Failure", () -> {
                                retryCount++;
                                fetchAndUpdateTasks(null);
                            }, () -> {

                            }, "Retry", "Cancel");
                    }


                });

    }

    private void viewTask(Event event) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("isViewMode", true);
        intent.putExtra("eventId", event.getEventId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        monthCalendar.releaseCalendar();
        listCalendar.releaseCalendar();
    }

    private class MonthDayDecoratorImpl implements MonthDayDecorator {

        private TextView day;

        public MonthDayDecoratorImpl(View parent) {
            day = parent.findViewById(R.id.month_view_item_content);
        }

        @Override
        public void onBindDayView(View view, Calendar monthDay, Calendar month, List<Event> eventList, boolean isSelected, boolean isToday) {
            if (!DateUtils.isSameMonth(month, monthDay)) {
                view.setVisibility(View.GONE);
                return;
            } else view.setVisibility(View.VISIBLE);

            day.setText(String.valueOf(monthDay.get(Calendar.DAY_OF_MONTH)));

            view.setBackgroundColor(Color.TRANSPARENT);

            if (isSelected) {
                day.setBackgroundResource(io.github.memfis19.cadar.R.drawable.event_selected_background);
            } else if (isToday) {
                day.setBackgroundResource(io.github.memfis19.cadar.R.drawable.event_today_background);
            } else {
                day.setBackgroundColor(Color.TRANSPARENT);
            }

            if (eventList == null || eventList.isEmpty()) {
                day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.event_indicator);
            }
        }
    }

}
