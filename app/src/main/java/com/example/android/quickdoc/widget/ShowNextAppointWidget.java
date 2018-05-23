package com.example.android.quickdoc.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.quickdoc.AppointmentDetailsActivity;
import com.example.android.quickdoc.R;
import com.example.android.quickdoc.dataClasses.AppointmentTime;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.example.android.quickdoc.dataClasses.UserAppointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class ShowNextAppointWidget extends AppWidgetProvider {

    private static final String APPOINTMENT_TEXT_LIST = "APPOINTMENT_TEXT_LIST";
    private static final String USER_APPOINTMENT = "USER_APPOINTMENT";
    private static final String USER_APPOINTMENTS_COUNT = "USER_APPOINTMENTS_COUNT";
    private static final String CHILD_KEY = "CHILD_KEY";
    private static final int MAX_APPOINTMENS = 5;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        getAppointmentsFromDatabase(context, appWidgetManager, appWidgetId);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        IntentServiceWidget.startActionUpdateWidget(context);
    }

    public static void updateAllRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static void getAppointmentsFromDatabase(final Context context,
        final AppWidgetManager appWidgetManager, final int appWidgetId) {

        Log.i("denis", "getAppointmentsFromDatabase()");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        String firebaseUserId = FirebaseAuth.getInstance().getUid();

        if(firebaseUserId==null)
            return;

        //setting 2 calendar variables to use in firebase query
        Calendar today = Calendar.getInstance();

        Calendar lastDay = Calendar.getInstance();
        lastDay.add(Calendar.DAY_OF_YEAR, 60);

        //path example: /user_appointment/UserId/ - get just the next 5 appointments so Widget is not too big
        Query firebaseQuery = firebaseDatabase.getReference().child("user_appointments")
                .child(firebaseUserId).orderByChild("date").startAt(getStringDate(today))
                .limitToFirst(MAX_APPOINTMENS);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<UserAppointment> userAppointments = new ArrayList<>();
                ArrayList<String> childKeys = new ArrayList<>();

                if(dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        childKeys.add(childSnapshot.getKey());
                        userAppointments.add(childSnapshot.getValue(UserAppointment.class));
                        Log.i("denis", "if(dataSnapshot.exists()");
                    }

                    updateWidgetListview(context, userAppointments, childKeys, appWidgetManager, appWidgetId);

                } else{
                    Log.i("denis", "detaSnapshot exists false");
                    //TODO - adicionar um textview dizendo que nao tem consultas proximas no widget
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        firebaseQuery.addListenerForSingleValueEvent(valueEventListener);
    }

    private static void updateWidgetListview(Context context, ArrayList<UserAppointment> userAppointments
            , ArrayList<String> childKeys, AppWidgetManager appWidgetManager, int appWidgetId) {

        //first we prepare the texts to be displayed in the textviews of the Listview
        String appointmentText = "";
        int[] textViewIds = getTextViewIds();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.show_next_appoint_widget);
        int index = 0;
        Intent[] intentArray = new Intent[5];
        PendingIntent pendingIntent;

        for(UserAppointment userAppointment : userAppointments) {
            //Example of text: "Cardiologist - 25/03/2018 - 10:30"

            //set the text of textview
            appointmentText = SpecialtyNames.getSpecialtyName(context, userAppointment.getSpecialty())
                    +" - "+getAdaptedDate(userAppointment.getDate())
                    +" - "+ AppointmentTime.getTimeFromIndex(userAppointment.getTime());

            views.setViewVisibility(textViewIds[index], View.VISIBLE);
            views.setTextViewText(textViewIds[index], appointmentText);

            //create the intent, wrap a pending intent around it and create a onClickListner
            intentArray[index] = new Intent(context, AppointmentDetailsActivity.class);
            intentArray[index].putExtra(USER_APPOINTMENT, userAppointment);
            intentArray[index].putExtra(CHILD_KEY, childKeys.get(index));
            intentArray[index].setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intentArray[index], PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(textViewIds[index], pendingIntent);

            index++;
        }

        //set the remaining TextViews to GONE visibility
        for(; index<MAX_APPOINTMENS; index++){
            views.setViewVisibility(textViewIds[index], View.GONE);
        }

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.ll_widget_container);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static int[] getTextViewIds() {
        int[] textViewIds = new int[MAX_APPOINTMENS];
        textViewIds[0] = R.id.textview_appointment1;
        textViewIds[1] = R.id.textview_appointment2;
        textViewIds[2] = R.id.textview_appointment3;
        textViewIds[3] = R.id.textview_appointment4;
        textViewIds[4] = R.id.textview_appointment5;

        return textViewIds;
    }

    private static String getStringDate(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Log.i("denis", "Date format: "+dateFormat.format(date));
        return dateFormat.format(date);
    }

    private static String getAdaptedDate(String date) {

        if(Locale.getDefault().getLanguage().matches("pt")){
            String[] dateVector = date.split("-");
            String year = dateVector[0];
            String month = dateVector[1];
            String day = dateVector[2];
            return day+"/"+month+"/"+year;
        }

        return date;
    }
}

