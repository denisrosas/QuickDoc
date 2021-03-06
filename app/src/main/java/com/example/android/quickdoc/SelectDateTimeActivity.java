package com.example.android.quickdoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quickdoc.dataClasses.AppointmentTime;
import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.example.android.quickdoc.dataClasses.Months;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.example.android.quickdoc.dataClasses.UserAppointment;
import com.example.android.quickdoc.dataClasses.WeekDays;
import com.example.android.quickdoc.widget.IntentServiceWidget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDateTimeActivity extends AppCompatActivity {

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the specialties DB tree
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private static final String FIREBASE_CHILD_AGENDA = "agenda";
    private static final String FIREBASE_CHILD_USER_APPOINT = "user_appointments";
    private static final String FIREBASE_CHILD_FULLDAY = "fullday";

    private static final String DOCTOR_DETAILS = "DOCTOR_DETAILS";
    private static final String SPECIALTY_KEY = "SPECIALTY_KEY";
    private static final String ACTION_UPDATE = "com.example.android.quickdoc.action.UPDATE_WIDGETS";
    private static final int APPOINTMENT_LIST_SIZE = 16;
    private static final int MAX_WAITING_DAYS_SCHED = 90;

    Calendar currentDate = Calendar.getInstance();
    Calendar startingDate;
    String specialtyKey;
    DoctorDetailsToUser doctorDetailsToUser;
    String doctorId;
    String firebaseUID;
    boolean lastAppOfDay = false;

    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.tv_day) TextView tvDay;
    @BindView(R.id.tv_month) TextView tvMonth;
    @BindView(R.id.tv_week_day) TextView tvWeekDay;
    @BindView(R.id.iv_prev_day) ImageView ivPrevDay;
    @BindView(R.id.iv_next_day) ImageView ivNextDay;
    @BindView(R.id.bt_schedule_appointment) Button buttonSchedAppont;
    @BindView(R.id.progressBarSelectDate) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_time);

        ButterKnife.bind(this);

        //Get information from Intent
        doctorDetailsToUser = (DoctorDetailsToUser) getIntent().getSerializableExtra(DOCTOR_DETAILS);
        specialtyKey = getIntent().getStringExtra(SPECIALTY_KEY);
        doctorId = "doctor"+doctorDetailsToUser.getDoctorId();

        //start currentdate as tomorrow
        setToNextWeekDay();
        //startingDate will be a reference as the first available date.
        startingDate = Calendar.getInstance();
        startingDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        setDayMonthTextViews();

        //getting the user ID of firebase. Will be used to store data
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUID = firebaseAuth.getCurrentUser().getUid();

        //start Firebase Database and attach event listener
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        String date = convertCalendarToString(currentDate);
        firebaseAttachValueEventList(date);

        setOnClickListeners();
    }

    /** Set the tv_day and tv_month TextViews with the correct Day and Month */
    private void setDayMonthTextViews() {
        tvDay.setText(String.valueOf(currentDate.get(Calendar.DAY_OF_MONTH)));
        tvMonth.setText(Months.getMonthName(currentDate.get(Calendar.MONTH), this));
        tvWeekDay.setText(WeekDays.getMonthName(currentDate.get(Calendar.DAY_OF_WEEK), this));
    }

    private void setOnClickListeners() {

        //if user clicks the left arrow, show the previous day
        ivPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar intededDate = Calendar.getInstance();
                intededDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                intededDate.add(Calendar.DAY_OF_YEAR, -1);

                while ((intededDate.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY)
                    ||(intededDate.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)){
                    intededDate.add(Calendar.DAY_OF_YEAR, -1);
                }

                //if the previous day is tomorrow
                int result = intededDate.compareTo(startingDate);
                if(result<1) {
                    Toast.makeText(getApplicationContext(), R.string.alert_prev_day_of_tomorrow, Toast.LENGTH_LONG).show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);

                    //set currentDay to the previous week day
                    setToPrevWeekDay();

                    setDayMonthTextViews();

                    if((valueEventListener!=null)&&(databaseReference!=null))
                        databaseReference.removeEventListener(valueEventListener);

                    //remove all radio Buttons
                    radioGroup.removeAllViews();

                    //attach a child value event listener for the new date
                    firebaseAttachValueEventList(convertCalendarToString(currentDate));
                }
            }
        });

        ivNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar ninetyDaysFromToday = Calendar.getInstance();
                ninetyDaysFromToday.add(Calendar.DAY_OF_YEAR, MAX_WAITING_DAYS_SCHED);

                //if the previous day is tomorrow
                if(convertCalendarToString(currentDate).matches(convertCalendarToString(ninetyDaysFromToday))) {
                    Toast.makeText(getApplicationContext(), R.string.alert_next_day_too_far, Toast.LENGTH_LONG).show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);

                    //set currentDay to the previous day
                    setToNextWeekDay();

                    setDayMonthTextViews();

                    if(valueEventListener!=null)
                        databaseReference.removeEventListener(valueEventListener);

                    //remove all radio Buttons
                    radioGroup.removeAllViews();

                    //attach a child value event listener for the new date
                    firebaseAttachValueEventList(convertCalendarToString(currentDate));
                }
            }
        });

        buttonSchedAppont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());

                if(radioButton!=null)
                    showConfirmationDialog((int)radioButton.getTag());
            }
        });
    }

    private String convertCalendarToString(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Log.i("denis", "Data: "+dateFormat.format(date));
        return dateFormat.format(date);
    }

    private void setToPrevWeekDay() {

        currentDate.add(Calendar.DAY_OF_YEAR, -1);

        switch (currentDate.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                currentDate.add(Calendar.DAY_OF_YEAR, -2);
                break;
            case Calendar.SATURDAY:
                currentDate.add(Calendar.DAY_OF_YEAR, -1);
                break;
            default:
                break;
        }
    }

    private void setToNextWeekDay() {

        currentDate.add(Calendar.DAY_OF_YEAR, 1);

        switch (currentDate.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SATURDAY:
                currentDate.add(Calendar.DAY_OF_YEAR, 2);
                break;
            case Calendar.SUNDAY:
                currentDate.add(Calendar.DAY_OF_YEAR, 1);
                break;
            default:
                break;
        }
    }

    /*

     */
    private void firebaseAttachValueEventList(String firebaseDate) {
         databaseReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
         .child(specialtyKey).child(doctorId).child(firebaseDate);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("denis", "onDataChanged()");

                //create boolean vector representing the available appointments of the day
                boolean[] availableAppointments = new boolean[APPOINTMENT_LIST_SIZE];

                if(dataSnapshot.exists()){

                    for(int index = 0; index< APPOINTMENT_LIST_SIZE; index++){
                        //if the child exists, means that the horary is reserved. If not, set position to true
                        availableAppointments[index] = !dataSnapshot.child("" + index).exists();
                    }

                } else{
                    //if it's not found in Database, than all times are available. Set all to true
                    Log.i("denis", "No appointments on this day!");

                    //day schedule is free. Set all horaries to true (available)
                    for(int index = 0; index<APPOINTMENT_LIST_SIZE; index++){
                        availableAppointments[index] = true;
                    }
                }

                //call method to display all available times in RadioGroup
                updateDateMonthAppList(availableAppointments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

    /* Creates a list of Radio Buttons. One Radio button for every available schedule
    * */
    private void updateDateMonthAppList(boolean[] availableAppointments){

        Log.i("denis", "updateDateMonthAppList()");

        RadioButton radioButton;
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        boolean noHoraryAvailable = true;
        int availableAppointCount = 0;

        for(int index=0; index<availableAppointments.length; index++) {
            if(availableAppointments[index]) {
                noHoraryAvailable=false;
                availableAppointCount++;
                radioButton = new RadioButton(this);
                radioButton.setText(AppointmentTime.getTimeFromIndex(index));
                radioButton.setTag(index);
                radioGroup.addView(radioButton, params);
            }
        }

        if(noHoraryAvailable){
            buttonSchedAppont.setClickable(false);
            Toast.makeText(this, "No horaries Avaliable for this date", Toast.LENGTH_LONG).show();
        } else {
            buttonSchedAppont.setClickable(true);
        }

        if(availableAppointCount==1)
            lastAppOfDay=true;

        progressBar.setVisibility(View.GONE);
    }

    /*This method displays a Dialog, so the user can confirm to schedule the appountment */
    private void showConfirmationDialog(final int selectedHorary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getConfirmationMessage(selectedHorary))
                .setPositiveButton(getString(R.string.yes),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    addAppointmentOnDatabase(selectedHorary);
                    Log.i("denis", "selectedHorary: "+selectedHorary);

                    //Call MainActivity - Clear all Activity stack
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void addAppointmentOnDatabase(int selectedHorary) {

        //remove the event listener
        if((databaseReference !=null)&&(valueEventListener!=null))
        databaseReference.removeEventListener(valueEventListener);

        //First we need to save the appointment in the doctor's agenda

        //example path agenda/cardiologist/doctor0/2018-05-06
        databaseReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
                .child(specialtyKey).child(doctorId).child(convertCalendarToString(currentDate)).child(""+selectedHorary);

        databaseReference.setValue(firebaseUID);

        //Second we save the fullday child as true, if this was the last appointment available in the day
        databaseReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
                .child(specialtyKey).child(doctorId).child(convertCalendarToString(currentDate)).child(FIREBASE_CHILD_FULLDAY);

        if(lastAppOfDay)
            databaseReference.setValue(true);
        else
            databaseReference.setValue(false);

        //Third we need to save in the user appointments tree
        databaseReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APPOINT)
                .child(firebaseUID);

        //getting last character of doctorId (Ex: "doctor2") and transform to int
        int appDoctorId = Integer.parseInt(doctorId.substring(doctorId.length()-1));

        UserAppointment userAppointment = new UserAppointment(convertCalendarToString(currentDate),
                selectedHorary, specialtyKey, appDoctorId, false, false);

        databaseReference.push().setValue(userAppointment);

        //And last create an intent to update any Widgets that are in the home screen
        Intent intent = new Intent(getApplicationContext(), IntentServiceWidget.class);
        intent.setAction(ACTION_UPDATE);
        getApplicationContext().startService(intent);
    }

    private String getConfirmationMessage(int horary) {
        String date;
        if(Locale.getDefault().getLanguage().matches("pt")){
            String[] dateVector = convertCalendarToString(currentDate).split("-");
            String year = dateVector[0];
            String month = dateVector[1];
            String day = dateVector[2];
            date = day+"/"+month+"/"+year;
        } else{
            date = convertCalendarToString(currentDate).replace("-", "/");
        }

        return getString(R.string.confirm_appointment_dialog)+" "+doctorDetailsToUser.getName()+" - "+
           SpecialtyNames.getSpecialtyName(this, specialtyKey)+" "+
                getString(R.string.on)+" "+date +getString(R.string.at)+" "+
                AppointmentTime.getTimeFromIndex(horary) ;
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(valueEventListener);
        databaseReference = null;
        valueEventListener = null;
    }

}
