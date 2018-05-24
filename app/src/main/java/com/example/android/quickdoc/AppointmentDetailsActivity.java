package com.example.android.quickdoc;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.quickdoc.dataClasses.AppointmentTime;
import com.example.android.quickdoc.dataClasses.DoctorDetails;
import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.example.android.quickdoc.dataClasses.UserAppointment;
import com.example.android.quickdoc.widget.IntentServiceWidget;
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
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentDetailsActivity extends AppCompatActivity {

    private static final String FIREBASE_CHILD_DOCTORS = "doctors";
    private static final String PHOTOS_FOLDER = "photos";
    private static final String BIG_PHOTO_FILENAME_PREFIX = "doctor_big";
    private static final String PHOTO_EXTENSION = ".jpg";
    private static final String USER_APPOINTMENT = "USER_APPOINTMENT";
    private static final String FIREBASE_CHILD_FULLDAY = "fullday";
    private static final String FIREBASE_CHILD_AGENDA = "agenda";
    private static final String FIREBASE_CHILD_USER_APPOINT = "user_appointments";
    private static final String CHILD_KEY = "CHILD_KEY";
    private static final String ACTION_UPDATE = "com.example.android.quickdoc.action.UPDATE_WIDGETS";

    DoctorDetailsToUser doctorDetails;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    UserAppointment userAppointment;
    ValueEventListener valueEventListener;
    String firebaseUID;
    String childAppointmentKey;

    @BindView(R.id.tv_doctor_name)
    TextView textViewDocName;
    @BindView(R.id.tv_specialty) TextView textViewSpecialty;
    @BindView(R.id.tv_avarage_reviews) TextView textViewAvrgReviews;
    @BindView(R.id.tv_date) TextView textViewDate;
    @BindView(R.id.tv_appoint_time) TextView textViewTime;
    @BindView(R.id.tv_accepted_health_care) TextView textViewAccHealthCare;
    @BindView(R.id.tv_address) TextView textViewDocAddress;
    @BindView(R.id.tv_address_text) TextView textViewAddressText;
    @BindView(R.id.tv_distance) TextView textViewDistance;
    @BindView(R.id.tv_phone_number) TextView textViewPhone;

    @BindView(R.id.iv_doctor_photo) ImageView imageViewDocPhoto;
    @BindView(R.id.iv_amil) ImageView imageViewAmil;
    @BindView(R.id.iv_bradesco) ImageView imageViewBradesco;
    @BindView(R.id.iv_hapvida) ImageView imageViewHapvida;
    @BindView(R.id.iv_prevent) ImageView imageViewPrevent;
    @BindView(R.id.iv_sulamerica) ImageView imageViewSulamerica;
    @BindView(R.id.iv_unimed) ImageView imageViewUnimed;
    @BindView(R.id.iv_directions) ImageView imageViewDirections;

    @BindView(R.id.button_cancel_appointment) Button buttonCancelAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        //Starting Butter Knife
        ButterKnife.bind(this);

        //start Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();

        //getting the user ID of firebase. Will be used to store data
        firebaseUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get variable from intent
        userAppointment = (UserAppointment) getIntent().getSerializableExtra(USER_APPOINTMENT);
        childAppointmentKey = getIntent().getStringExtra(CHILD_KEY);

        //
        setOnClickListeners(doctorDetails, userAppointment.getSpecialty());

        //Get Doctor Details from Firebase
        startFirebaseListener(userAppointment);
    }

    public void setOnClickListeners(final DoctorDetailsToUser doctorDetailsToUser, final String specialtyKey){

        //Open map application when address is pressed
        textViewAddressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMapsActivity(doctorDetailsToUser);
            }
        });

        imageViewDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callMapsActivity(doctorDetailsToUser);
            }
        });

        //Open Dialer when the phone number is pressed
        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // It open the dialer app and allow user to call the number manually
                Intent intent = new Intent(Intent.ACTION_DIAL);
                // Send phone number to intent as data
                intent.setData(Uri.parse("tel:" + doctorDetailsToUser.getPhoneNumber()));
                // Start the dialer app activity with number
                startActivity(intent);
            }
        });

        //if the button is pressed, go to activity to select date/time of the appointment
        buttonCancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });
    }

    private void callMapsActivity(final DoctorDetailsToUser doctorDetailsToUser) {
        double latitude = doctorDetailsToUser.getAddressLat();
        double longitude = doctorDetailsToUser.getAddressLng();
        String label;

        if(Locale.getDefault().getLanguage().matches("pt")) {
            label = getString(R.string.office)+"do(a) "+doctorDetailsToUser.getName();
        } else {
            label = doctorDetailsToUser.getName()+"'s "+getString(R.string.office);
        }
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void startFirebaseListener(final UserAppointment userAppointment) {

        //example "doctors/cardiologist/doctor3"
        databaseReference = firebaseDatabase.getReference().child(FIREBASE_CHILD_DOCTORS)
                .child(userAppointment.getSpecialty()).child(""+userAppointment.getDoctorId());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    doctorDetails = new DoctorDetailsToUser(dataSnapshot.getValue(DoctorDetails.class));
                    doctorDetails.setDoctorId(userAppointment.getDoctorId());
                    doctorDetails.setWaitingDays(new Random().nextInt(60));
                    doctorDetails.setDistanceToDoctor(calculateDistance(doctorDetails.getAddressLat(), doctorDetails.getAddressLng()));

                    setFirebaseDoctorPhoto(userAppointment.getSpecialty(), userAppointment.getDoctorId());

                    setTextViews(doctorDetails, userAppointment);

                    hideUnsupportedHealthPlanes(doctorDetails);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    private float calculateDistance(float lat, float lng) {
        /** If has permission, returns the distance to the doctor in Kms
         * */
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
                && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)){

            return -1;

        } else {
            Location locationUser = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
            Location locationDoctor = new Location("Doctor's Office");
            locationDoctor.setLatitude(lat);
            locationDoctor.setLongitude(lng);
            if(locationUser!=null)
                return (locationUser.distanceTo(locationDoctor) / 1000); //meters to km

            return -1;
        }


    }

    private void hideUnsupportedHealthPlanes(DoctorDetailsToUser doctorDetailsToUser) {

        if(!doctorDetailsToUser.isAcceptsAmil())
            imageViewAmil.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsBradescoSaude())
            imageViewBradesco.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsHapVida())
            imageViewHapvida.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsPreventSenior())
            imageViewPrevent.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsSulamerica())
            imageViewSulamerica.setVisibility(View.GONE);

        if(!doctorDetailsToUser.isAcceptsUnimed())
            imageViewUnimed.setVisibility(View.GONE);

    }

    private void setTextViews(DoctorDetailsToUser doctorDetailsToUser, UserAppointment userAppointment) {

        textViewDocName.setText(doctorDetailsToUser.getName());
        textViewSpecialty.setText(SpecialtyNames.getSpecialtyName(this, userAppointment.getSpecialty()));

        DecimalFormat df = new DecimalFormat("0.0");
        textViewAvrgReviews.setText(df.format(doctorDetailsToUser.getAvaregeReviews()));

        textViewDate.setText(getAdaptedDate(userAppointment.getDate()));
        textViewTime.setText(AppointmentTime.getTimeFromIndex(userAppointment.getTime()));

        textViewAccHealthCare.setText(getString(R.string.accepted_health_care));
        textViewDocAddress.setText(getString(R.string.address));
        textViewAddressText.setText(doctorDetailsToUser.getAddressExtended());

        //if we can't get the location, then getDistanceToDoctor will return -1
        String discanteKm;
        if(doctorDetailsToUser.getDistanceToDoctor()>=0)
            discanteKm = getString(R.string.distance)+": "+df.format(doctorDetailsToUser.getDistanceToDoctor())+" "+getString(R.string.km);
        else
            discanteKm = "-";

        textViewDistance.setText(discanteKm);

        textViewPhone.setText(doctorDetailsToUser.getPhoneNumber());

        buttonCancelAppointment.setText(getString(R.string.cancel_appoiontment));
    }

    /*This method displays a Dialog, so the user can confirm to schedule the appountment */
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message_confirm_cancel)
                .setPositiveButton(getString(R.string.yes),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        deleteAppointmentOnDatabase();

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

    private void deleteAppointmentOnDatabase() {

        //remove the event listener
        if((databaseReference !=null)&&(valueEventListener!=null))
            databaseReference.removeEventListener(valueEventListener);

        //than make the time free in the agenda
        //example path agenda/cardiologist/doctor0/2018-05-06/3
        databaseReference = firebaseDatabase.getReference().child(FIREBASE_CHILD_AGENDA)
                .child(userAppointment.getSpecialty()).child("doctor"+userAppointment.getDoctorId())
                .child(userAppointment.getDate());

        databaseReference.child(""+userAppointment.getTime()).removeValue();

        //and set fullday to false, as we know tehre is at least one time available
        databaseReference.child(FIREBASE_CHILD_FULLDAY).setValue(false);

        //remove the appointment from the user appointment tree
        //example path: /user_appointments/firebaseUID/uniquekey
        databaseReference = firebaseDatabase.getReference().child(FIREBASE_CHILD_USER_APPOINT)
                .child(firebaseUID).child(childAppointmentKey);

        databaseReference.removeValue();

        //And last create an intent to update any Widgets that are in the home screen
        Intent intent = new Intent(getApplicationContext(), IntentServiceWidget.class);
        intent.setAction(ACTION_UPDATE);
        getApplicationContext().startService(intent);

    }

    private String getAdaptedDate(String date) {

        if(Locale.getDefault().getLanguage().matches("pt")){
            String[] dateVector = date.split("-");
            String year = dateVector[0];
            String month = dateVector[1];
            String day = dateVector[2];
            return day+"/"+month+"/"+year;
        }

        return date;
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
}
