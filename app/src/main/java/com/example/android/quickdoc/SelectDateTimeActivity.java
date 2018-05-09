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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDateTimeActivity extends AppCompatActivity {

    //Firebase Database
    private FirebaseDatabase mFirebaseDatabase;

    //Reference and Event Listener of the specialties DB tree
    private DatabaseReference mDocDayScheduleDBReference;
    private ValueEventListener valueEventListener;
    private static final String FIREBASE_CHILD_AGENDA = "agenda";
    private static final String FIREBASE_CHILD_USER_APPOINT = "user_appointments";
    private Map.Entry<String, ArrayList<String>> firebaseChildMapEntry;

    private static final String DOCTOR_DETAILS = "DOCTOR_DETAILS";
    private static final String SPECIALTY_KEY = "SPECIALTY_KEY";
    private static final String SELECTED_HORARY = "SELECTED_HORARY";
    private static final int APPOINTMENT_LIST_SIZE = 16;

    Calendar currentDate;
    String specialtyKey;
    DoctorDetailsToUser doctorDetailsToUser;
    String doctorId;
    boolean noAppointmentsOnDay;
    String firebaseUID;

    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.tv_day) TextView tvDay;
    @BindView(R.id.tv_month) TextView tvMonth;
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

        //you can comment the line below
        currentDate = Calendar.getInstance();
        //currentDate.add(Calendar.DAY_OF_YEAR, -2);

        setDayMonthTextViews();

        //getting the user ID of firebase. Will be used to store data
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUID = firebaseAuth.getCurrentUser().getUid();

        //start Firebase Database and attach event listener
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        String date = convertCalendarToString(currentDate);
        firebaseAttachChildEventList(date);

        setOnClickListeners();
    }

    /** Set the tv_day and tv_month TextViews with the correct Day and Month */
    private void setDayMonthTextViews() {
        tvDay.setText(String.valueOf(currentDate.get(Calendar.DAY_OF_MONTH)));
        tvMonth.setText(Months.getMonthName(currentDate.get(Calendar.MONTH), this));
    }

    private void setOnClickListeners() {

        //if user clicks the left arrow, show the previous day
        ivPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_YEAR, 1);

                //if the previous day is tomorrow
                if(convertCalendarToString(currentDate).matches(convertCalendarToString(tomorrow))) {
                    Toast.makeText(getApplicationContext(), R.string.alert_prev_day_of_tomorrow, Toast.LENGTH_LONG).show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);

                    //set currentDay to the previous day
                    currentDate.add(Calendar.DAY_OF_YEAR, -1);

                    setDayMonthTextViews();

                    if(valueEventListener!=null)
                        mDocDayScheduleDBReference.removeEventListener(valueEventListener);

                    //remove all radio Buttons
                    radioGroup.removeAllViews();

                    //attach a child value event listener for the new date
                    firebaseAttachChildEventList(convertCalendarToString(currentDate));
                }
            }
        });

        ivNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar ninetyDaysFromToday = Calendar.getInstance();
                ninetyDaysFromToday.add(Calendar.DAY_OF_YEAR, 90);

                //if the previous day is tomorrow
                if(convertCalendarToString(currentDate).matches(convertCalendarToString(ninetyDaysFromToday))) {
                    Toast.makeText(getApplicationContext(), R.string.alert_next_day_too_far, Toast.LENGTH_LONG).show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);

                    //set currentDay to the previous day
                    currentDate.add(Calendar.DAY_OF_YEAR, 1);

                    setDayMonthTextViews();

                    if(valueEventListener!=null)
                        mDocDayScheduleDBReference.removeEventListener(valueEventListener);

                    //remove all radio Buttons
                    radioGroup.removeAllViews();

                    //attach a child value event listener for the new date
                    firebaseAttachChildEventList(convertCalendarToString(currentDate));
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

    /*

     */
    private void firebaseAttachChildEventList(String firebaseDate) {
         mDocDayScheduleDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
         .child(specialtyKey).child(doctorId).child(firebaseDate);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, ArrayList<String>>> typeIndicator = new GenericTypeIndicator<Map<String, ArrayList<String>>>(){};
                ArrayList<String> docDayScheduleChild;

                if(dataSnapshot.exists()){
                    Map<String, ArrayList<String>> hashMap = dataSnapshot.getValue(typeIndicator);

                    if (hashMap != null) {
                        for(Map.Entry<String, ArrayList<String>> mapEntry : hashMap.entrySet()){

                            //store daily schedule and it's key in global variables
                            firebaseChildMapEntry = mapEntry;
                            docDayScheduleChild = mapEntry.getValue();

                            noAppointmentsOnDay=true;

                            //create boolean vector and set all to available appointments to true
                            boolean[] availableAppointments = new boolean[APPOINTMENT_LIST_SIZE];
                            for(int index = 0; index< docDayScheduleChild.size(); index++){
                                availableAppointments[index] = docDayScheduleChild.get(index).equals("");
                                noAppointmentsOnDay=false;
                            }

                            //call method to display all available times in RadioGroup
                            updateDateMonthAppList(availableAppointments);
                        }
                    }
                } else{
                    //if it's not found in Database, than all times are available. Set all to true
                    Log.i("denis", "No appointments on this day!");
                    noAppointmentsOnDay=true;

                    //create boolean vector and set all appointments to true
                    boolean[] availableAppointments = new boolean[APPOINTMENT_LIST_SIZE];
                    for(int index = 0; index<APPOINTMENT_LIST_SIZE; index++){  availableAppointments[index] = true;  }

                    //call method to display all available times in RadioGroup
                    updateDateMonthAppList(availableAppointments);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDocDayScheduleDBReference.addValueEventListener(valueEventListener);
    }

    /* Creates a list of Radio Buttons. One Radio button for every available schedule
    * */
    private void updateDateMonthAppList(boolean[] availableAppointments){
        RadioButton radioButton;
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        boolean noHoraryAvailable = true;

        for(int index=0; index<availableAppointments.length; index++) {
            if(availableAppointments[index]) {
                noHoraryAvailable=false;
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
        progressBar.setVisibility(View.GONE);
    }

    /*This method displays a Dialog, so the user can confirm to schedule the appountment */
    private void showConfirmationDialog(final int selectedHorary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getConfirmationMessage())
                .setPositiveButton(getString(R.string.yes),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //Store the new appointment in the Database
                        if(noAppointmentsOnDay){
                            addNewAppointmentArray(selectedHorary);
                        } else{
                            addAppointmentOnDatabase(selectedHorary);
                            Log.i("denis", "selectedHorary: "+selectedHorary);
                        }

                        //Call MainActivity
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

    private void addNewAppointmentArray(int selectedHorary) {

        //remove the event listener
        if((mDocDayScheduleDBReference!=null)&&(valueEventListener!=null))
            mDocDayScheduleDBReference.removeEventListener(valueEventListener);

        //set the new reference to point at. Example: agenda/cardiologist/doctor0/2018-05-06
        mDocDayScheduleDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
                .child(specialtyKey).child(doctorId).child(convertCalendarToString(currentDate));

        //create new array and put the UID in the selected horary
        ArrayList<String> docDayNewSchedule = createArrayListEmptyPositions();
        docDayNewSchedule.set(selectedHorary, firebaseUID);

        //save new daily schedule to database with one new appointment
        mDocDayScheduleDBReference.push().setValue(docDayNewSchedule);

        //
        mDocDayScheduleDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APPOINT)
                .child(firebaseUID);

        int appDoctorId = Integer.parseInt(doctorId.substring(doctorId.length()-1));

        UserAppointment userAppointment = new UserAppointment(convertCalendarToString(currentDate),
                selectedHorary, specialtyKey, appDoctorId);

        mDocDayScheduleDBReference.push().setValue(userAppointment);
    }

    private void addAppointmentOnDatabase(int selectedHorary) {

        //remove the event listener
        if((mDocDayScheduleDBReference!=null)&&(valueEventListener!=null))
        mDocDayScheduleDBReference.removeEventListener(valueEventListener);

        //example path agenda/cardiologist/doctor0/2018-05-06
        mDocDayScheduleDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
                .child(specialtyKey).child(doctorId).child(convertCalendarToString(currentDate));

        //get and update Daily Schedule before push to database
        ArrayList<String> docDayNewSchedule = firebaseChildMapEntry.getValue();

        //set the position of the horary to the userID
        docDayNewSchedule.set(selectedHorary, firebaseUID);

        //remove the previopus arraylist (because I just couldnt update it)
        mDocDayScheduleDBReference.removeValue();
        //push the new arraylist
        mDocDayScheduleDBReference.push().setValue(docDayNewSchedule);

        //
        mDocDayScheduleDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APPOINT)
                .child(firebaseUID);

        int appDoctorId = Integer.parseInt(doctorId.substring(doctorId.length()-1));

        UserAppointment userAppointment = new UserAppointment(convertCalendarToString(currentDate),
                selectedHorary, specialtyKey, appDoctorId);

        mDocDayScheduleDBReference.push().setValue(userAppointment);
    }

    /**This method returns an Arraylist of string of size APPOINTMENT_LIST_SIZE with all position as "" */
    private ArrayList<String> createArrayListEmptyPositions() {
        ArrayList<String> arrayList = new ArrayList<>();

        for(int i=0; i< APPOINTMENT_LIST_SIZE; i++){
            arrayList.add("");
        }

        return arrayList;
    }

    private String getConfirmationMessage() {
        return getString(R.string.confirm_appointment_dialog)+" "+doctorDetailsToUser.getName()+" - "+
           SpecialtyNames.getSpecialtyName(this, specialtyKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDocDayScheduleDBReference.removeEventListener(valueEventListener);
        mDocDayScheduleDBReference = null;
        valueEventListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        firebaseAttachChildEventList(convertCalendarToString(currentDate));
    }

    //Save and restore the state of the RadioGroup
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_HORARY,radioGroup.getCheckedRadioButtonId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selectedHoraryRadioId = savedInstanceState.getInt(SELECTED_HORARY);
        RadioButton radioButton = radioGroup.findViewById(selectedHoraryRadioId);
        radioButton.setChecked(true);
    }
}
