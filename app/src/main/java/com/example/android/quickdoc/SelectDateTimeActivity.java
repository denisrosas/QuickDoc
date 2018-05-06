package com.example.android.quickdoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.android.quickdoc.dataClasses.Months;
import com.google.firebase.database.ChildEventListener;
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
    private ChildEventListener mDocDayChildEventListener;
    private static final String FIREBASE_CHILD_AGENDA = "agenda";

    private static final String DOCTOR_ID = "DOCTOR_ID";
    private static final String SPECIALTY_KEY = "SPECIALTY_KEY";

    Calendar currentDate = Calendar.getInstance();
    String specialtyKey;
    String doctorId;

    @BindView(R.id.radioGroup) RadioGroup radioGroup;
    @BindView(R.id.tv_day) TextView tvDay;
    @BindView(R.id.tv_month) TextView tvMonth;
    @BindView(R.id.iv_prev_day) ImageView ivPrevDay;
    @BindView(R.id.iv_next_day) ImageView ivNextDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_time);

        ButterKnife.bind(this);

        //example: doctor2
        doctorId = "doctor"+getIntent().getIntExtra(DOCTOR_ID, -1);
        specialtyKey = getIntent().getStringExtra(SPECIALTY_KEY);

        setDayMonthTextViews();

        //start Firebase Database and attach event listener
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAttachChildEventList(convertCalendarToString());

        setOnClickListeners();

    }

    private void setDayMonthTextViews() {
        tvDay.setText(Integer.toString(currentDate.get(Calendar.DAY_OF_MONTH)));
        tvMonth.setText(Months.getMonthName(currentDate.get(Calendar.MONTH), this));
    }

    private void setOnClickListeners() {

        ivPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate.add(Calendar.DAY_OF_YEAR, -1);
                setDayMonthTextViews();

            }
        });

        ivNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate.add(Calendar.DAY_OF_YEAR, 1);
                setDayMonthTextViews();
            }
        });
    }

    private void firebaseAttachChildEventList(String firebaseDate) {
         mDocDayScheduleDBReference = mFirebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
         .child(specialtyKey).child(doctorId).child(firebaseDate);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, ArrayList<String>>> typeIndicator = new GenericTypeIndicator<Map<String, ArrayList<String>>>(){};
                ArrayList<String> scheduleList;

                if(dataSnapshot.exists()){
                    Map<String, ArrayList<String>> hashMap = dataSnapshot.getValue(typeIndicator);

                    for(Map.Entry<String, ArrayList<String>> mapEntry : hashMap.entrySet()){

                        scheduleList = mapEntry.getValue();
                        for(String userId : scheduleList){
                            Log.i("denis", "userId= "+userId);
                        }
                    }
                } else{
                    Log.i("denis", "No appointments on this day!");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDocDayScheduleDBReference.addValueEventListener(valueEventListener);

    }

    private void updateDateMonthAppList(){

    }

    private String convertCalendarToString() {
        Date date = currentDate.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Log.i("denis", "Data: "+dateFormat.format(date));
        return dateFormat.format(date);
    }



}
