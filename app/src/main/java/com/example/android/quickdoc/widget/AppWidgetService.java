package com.example.android.quickdoc.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.quickdoc.AppointmentDetailsActivity;
import com.example.android.quickdoc.R;
import com.example.android.quickdoc.dataClasses.UserAppointment;

import java.util.ArrayList;

public class AppWidgetService extends RemoteViewsService {

    private static final String APPOINTMENT_TEXTS = "APPOINTMENT_TEXTS";
    private static final String USER_APPOINTMENT = "USER_APPOINTMENT";
    private static final String USER_APPOINTMENTS_COUNT = "USER_APPOINTMENTS_COUNT";
    private static final String CHILD_KEYS = "CHILD_KEYS";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        ArrayList<String> childKeys = intent.getStringArrayListExtra(CHILD_KEYS);
        ArrayList<String> appointmentTextList = intent.getStringArrayListExtra(APPOINTMENT_TEXTS);
        ArrayList<UserAppointment> userAppointments = new ArrayList<>();

        int userAppointmentCount = intent.getIntExtra(USER_APPOINTMENTS_COUNT, 0);
        Log.i("denis", "AppWidgetService -> onGetViewFactor ");

        for (int index = 0; index < userAppointmentCount; index++) {
            userAppointments.add((UserAppointment) intent.getSerializableExtra(USER_APPOINTMENT + index));
        }

        return new AppointmentListProvider(this.getApplicationContext(), childKeys, appointmentTextList, userAppointments);
    }
}

class AppointmentListProvider implements RemoteViewsService.RemoteViewsFactory{

    private static final String USER_APPOINTMENT = "USER_APPOINTMENT";
    private static final String CHILD_KEY = "CHILD_KEY";

    private Context context;
    private ArrayList<String> childKeys;
    private ArrayList<String> appointmentTextList;
    private ArrayList<UserAppointment> userAppointments;

    public AppointmentListProvider(Context context, ArrayList<String> childKeys, ArrayList<String> appointmentTextList, ArrayList<UserAppointment> userAppointments) {
        this.context = context;
        this.childKeys = childKeys;
        this.appointmentTextList = appointmentTextList;
        this.userAppointments = userAppointments;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.i("denis", "onDataSetChanged()");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.i("denis", "AppointmentListProvider - getCount(): " + appointmentTextList.size());
        return appointmentTextList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_next_appointment_item);
        remoteViews.setTextViewText(R.id.tv_appointment_datetime, appointmentTextList.get(position));

        //creating the intent to show the apointment details
        Intent intent  = new Intent(context, AppointmentDetailsActivity.class);

        //prevent to create multiple intances of list ingredients activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(USER_APPOINTMENT, userAppointments.get(position));
        intent.putExtra(CHILD_KEY, childKeys.get(position));
        remoteViews.setOnClickFillInIntent(R.id.tv_appointment_datetime, intent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

