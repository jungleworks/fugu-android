package com.skeleton.mvp.groupTasks;

/********************************
 Created by Amandeep Chauhan     *
 Date :- 05/08/2020              *
 ********************************/

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.R;
import com.skeleton.mvp.activity.MainActivity;
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

public class TaskCalendarFragment extends Fragment implements CalendarPrepareCallback {

    private MainActivity mainActivity;
    private MonthCalendar monthCalendar;
    private ListCalendar listCalendar;
    private List<Event> events = new ArrayList<>();
    private Calendar currentCalendar = Calendar.getInstance();
    private Boolean isLoadingShown = false;
    private int taskType = 3;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_task_calendar, container, false);

        monthCalendar = view.findViewById(R.id.monthCalendar);
        listCalendar = view.findViewById(R.id.listCalendar);

        Spinner spinnerTaskType = view.findViewById(R.id.spinnerTaskType);
        ImageView ivBack = view.findViewById(R.id.ivBack);

//        ivBack.setOnClickListener(v -> mainActivity.onBackPressed());
        ivBack.setVisibility(View.GONE);

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
            fetchAndUpdateTasks(null, isLoadingShown);
        });

        listCalendar.setOnDayChangeListener(calendar -> monthCalendar.setSelectedDay(calendar, false));

        listCalendar.setOnMonthChangeListener(calendar -> {
            monthCalendar.setSelectedDay(calendar, true);
            currentCalendar = (Calendar) calendar.clone();
            fetchAndUpdateTasks(null, isLoadingShown);
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
                fetchAndUpdateTasks(null, isLoadingShown);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onCalendarReady(CalendarController controller) {
        if (controller == monthCalendar) {
            monthCalendar.displayEvents(new ArrayList<>(events), month -> {
            });
        } else if (controller == listCalendar) {
            listCalendar.displayEvents(new ArrayList<>(events), period -> {

            });
        }
        fetchAndUpdateTasks(controller, isLoadingShown);
    }

    public void fetchAndUpdateTasks(CalendarController calendarController, final Boolean showLoading) {
        if (showLoading && isLoadingShown)
            mainActivity.showLoading();
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
                        TaskCalendarFragment.this.isLoadingShown = true;
                        if (showLoading && isLoadingShown) {
                            mainActivity.hideLoading();
                        }
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
                        if (showLoading && isLoadingShown)
                            mainActivity.hideLoading();
                        mainActivity.showErrorMessage(error.getStatusCode() + " " + error.getMessage());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (showLoading && isLoadingShown)
                            mainActivity.hideLoading();
                        mainActivity.showErrorMessage("API Failure");
                    }

                });

    }

    private void viewTask(Event event) {
        Intent intent = new Intent(mainActivity, TaskActivity.class);
        intent.putExtra("isViewMode", true);
        intent.putExtra("eventId", event.getEventId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
