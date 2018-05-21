package com.example.android.quickdoc.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.quickdoc.R;

import java.util.Objects;


public class IntentServiceWidget extends IntentService{

    private static final String ACTION_UPDATE = "com.example.android.quickdoc.action.UPDATE_WIDGETS";

    public IntentServiceWidget(){
        super("IntentServiceWidget");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent!=null) {
            String intentAction = intent.getAction();

            if (Objects.equals(intentAction, ACTION_UPDATE)) {
                handleActionUpdateWidgets();
                Log.i("denis", "Appointments - onHandleIntent - ACTION_UPDATE");
            }
        }

    }

    public static void startActionUpdateWidget(Context context) {
        Intent intent = new Intent(context, IntentServiceWidget.class);
        intent.setAction(ACTION_UPDATE);
        try{
            Log.i("denis", "startActionUpdateWidget() - startService");
            context.startService(intent);
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void handleActionUpdateWidgets() {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ShowNextAppointWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ll_widget_container);
        ShowNextAppointWidget.updateAllRecipeWidgets(this, appWidgetManager, appWidgetIds);
    }

}
