package com.example.android.quickdoc;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quickdoc.dataClasses.DoctorDetails;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.example.android.quickdoc.dataClasses.UserAppointment;
import com.example.android.quickdoc.dataClasses.UserReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAppointmentActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    ValueEventListener valueEventListener;
    DatabaseReference databaseReference;
    UserAppointment userAppointment;
    DoctorDetails doctorDetails;
    String childAppointmentKey;
    String firebaseUID;

    private static final String USER_APPOINTMENT = "USER_APPOINTMENT";
    private static final String FIREBASE_CHILD_USER_REVIEW = "user_review";
    private static final String FIREBASE_CHILD_DOCTORS = "doctors";
    private static final String FIREBASE_CHILD_AVG_REVIEWS = "avaregeReviews";
    private static final String FIREBASE_CHILD_REVIEWS_COUNT = "reviewsCount";
    private static final String FIREBASE_CHILD_USER_APP = "user_appointments";
    private static final String FIREBASE_CHILD_REVIEWED = "reviewed";
    private static final String CHILD_KEY = "CHILD_KEY";

    //
    private static final String REVIEW_TITLE = "REVIEW_TITLE";
    private static final String REVIEW_TEXT = "REVIEW_TEXT";
    private static final String REVIEW_GRADE = "REVIEW_GRADE";

    private static final String PHOTOS_FOLDER = "photos";
    private static final String BIG_PHOTO_FILENAME_PREFIX = "doctor_big";
    private static final String PHOTO_EXTENSION = ".jpg";

    @BindView(R.id.imageView) ImageView imageViewDocPhoto;
    @BindView(R.id.tv_doctor_name) TextView textViewDocName;
    @BindView(R.id.tv_specialty) TextView textViewSpecialty;
    @BindView(R.id.tv_grade_title) TextView textViewGradeTitle;
    @BindView(R.id.tv_grade) TextView textViewGrade;
    @BindView(R.id.tv_minus) TextView textViewMinus;
    @BindView(R.id.tv_plus) TextView textViewPlus;
    @BindView(R.id.et_review_title) EditText editTextReviewTitle;
    @BindView(R.id.et_review_text) EditText editTextReviewText;
    @BindView(R.id.buttonRegisterReview) Button btRegisterReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_appointment);

        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();

        userAppointment = (UserAppointment) getIntent().getSerializableExtra(USER_APPOINTMENT);
        childAppointmentKey = getIntent().getStringExtra(CHILD_KEY);

        //getting the user ID of firebase. Will be used to store data
        firebaseUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setOnClickListeners();

        getDoctorDetails();

    }

    private void getDoctorDetails() {

        databaseReference = firebaseDatabase.getReference().child(FIREBASE_CHILD_DOCTORS).
                child(userAppointment.getSpecialty()).child(Integer.toString(userAppointment.getDoctorId()));

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                doctorDetails = dataSnapshot.getValue(DoctorDetails.class);
                setTextViews();
                setFirebaseDoctorPhoto(userAppointment.getSpecialty(), userAppointment.getDoctorId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    private void setTextViews() {
        textViewDocName.setText(doctorDetails.getName());
        textViewSpecialty.setText(SpecialtyNames.getSpecialtyName(this, userAppointment.getSpecialty()));
        textViewGradeTitle.setText(getString(R.string.grade));
        btRegisterReview.setEnabled(false);
    }

    private void setOnClickListeners() {

        textViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float grade = Float.valueOf((String) textViewGrade.getText());
                if(grade<5) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    textViewGrade.setText(df.format((float) (grade + 0.5)));
                }
                else
                    Toast.makeText(getApplicationContext(), "Max grade is 5", Toast.LENGTH_LONG).show();
            }
        });

        textViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float grade = Float.valueOf((String) textViewGrade.getText());
                if(grade>0) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    textViewGrade.setText(df.format((float) (grade - 0.5)));
                }
                else
                    Toast.makeText(getApplicationContext(), "Grade cannot be lower than 0", Toast.LENGTH_LONG).show();

            }
        });

        btRegisterReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==0){
                    btRegisterReview.setEnabled(false);
                }else{
                    btRegisterReview.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        editTextReviewTitle.addTextChangedListener(textWatcher);
    }

    /*This method displays a Dialog, so the user can confirm to schedule the appountment */
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message_confirm_review)
                .setPositiveButton(getString(R.string.yes),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        registerReviewOnDatabase();

                        Toast.makeText(getApplicationContext(), R.string.save_review_confirmation_dialog, Toast.LENGTH_LONG).show();

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

    private void registerReviewOnDatabase() {

        UserReview userReview = new UserReview(
                editTextReviewTitle.getText().toString(),
                editTextReviewText.getText().toString(),
                userAppointment.getDate(),
                userAppointment.getTime(),
                Float.valueOf((String) textViewGrade.getText())
        );

        databaseReference = firebaseDatabase.getReference()
                .child(FIREBASE_CHILD_USER_REVIEW)
                .child(userAppointment.getSpecialty())
                .child("doctor"+userAppointment.getDoctorId());

        databaseReference.push().setValue(userReview);

        updateDoctorAvarageReview();

        setAppointMentAsReviewed();
    }

    private void updateDoctorAvarageReview() {
        float newReviewAvarage;
        float newReviewGrade = Float.valueOf((String) textViewGrade.getText());

        newReviewAvarage = (doctorDetails.getAvaregeReviews()*doctorDetails.getReviewsCount()+newReviewGrade)/
                (doctorDetails.getReviewsCount()+1);

        databaseReference = firebaseDatabase.getReference().child(FIREBASE_CHILD_DOCTORS).
                child(userAppointment.getSpecialty()).child(Integer.toString(userAppointment.getDoctorId()));

        databaseReference.child(FIREBASE_CHILD_AVG_REVIEWS).setValue(newReviewAvarage);

        databaseReference.child(FIREBASE_CHILD_REVIEWS_COUNT).setValue(doctorDetails.getReviewsCount()+1);

    }

    private void setAppointMentAsReviewed() {

        databaseReference = firebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APP).child(firebaseUID)
                .child(childAppointmentKey).child(FIREBASE_CHILD_REVIEWED);

        databaseReference.setValue(true);
    }

    /** Gets the image from Firebase and set in the imageView using Picasso*/
    private void setFirebaseDoctorPhoto(String specialtyKey, int doctorId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference();

        String photoUrl = PHOTOS_FOLDER+"/"+specialtyKey +"/"
                + BIG_PHOTO_FILENAME_PREFIX +doctorId+PHOTO_EXTENSION;

        //Path example: photos/cardiologist/doctor_big2.jpg
        storageReference.child(photoUrl).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).placeholder(R.drawable.doctor_big_default).into(imageViewDocPhoto);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.get().load(R.drawable.doctor_big_default).into(imageViewDocPhoto);
                Log.i("denis", "Fail to load image");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if((databaseReference!=null)&&(valueEventListener!=null))
            databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(REVIEW_TITLE, editTextReviewTitle.getText().toString());
        outState.putString(REVIEW_TEXT, editTextReviewText.getText().toString());
        outState.putString(REVIEW_GRADE, textViewGrade.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        editTextReviewTitle.setText(savedInstanceState.getString(REVIEW_TITLE));
        editTextReviewText.setText(savedInstanceState.getString(REVIEW_TEXT));
        textViewGrade.setText(savedInstanceState.getString(REVIEW_GRADE));

    }
}
