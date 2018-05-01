package com.example.android.quickdoc.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.quickdoc.R;
import com.example.android.quickdoc.externalClasses.CropCircleTransformation;
import com.example.android.quickdoc.dataClasses.DoctorDetailsToUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class AvailableDoctorsListAdapter extends RecyclerView.Adapter<AvailableDoctorsListAdapter.doctorDetailViewHolder> {

    private ArrayList<DoctorDetailsToUser> doctorDetailsList;
    private int specialty;

    public AvailableDoctorsListAdapter(ArrayList<DoctorDetailsToUser> doctorDetailsList, int specialty, Context context) {
        this.doctorDetailsList = doctorDetailsList;
        this.specialty = specialty;
        this.context = context;
    }

    private Context context;
    private static final String PHOTOS_FOLDER = "photos";
    private static final String SPECIALTY_FOLDER = "specialty";
    private static final String SMALL_PHOTO_FILENAME_PREFIX = "doctor_small_";
    private static final String PHOTO_EXTENSION = ".jpg";

    @NonNull
    @Override
    public doctorDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.doctor_details_item, parent, false);
        return new doctorDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull doctorDetailViewHolder holder, int position) {

        int doctorId = doctorDetailsList.get(position).getDoctorId();
        setFirebaseDoctorPhoto(holder.smallPhoto, doctorId);

        //set Doctors name
        holder.doctorName.setText(doctorDetailsList.get(position).getName());

        //Set Dotors avarage reviews
        NumberFormat oneDecimalFormat = new DecimalFormat("0.0");
        holder.doctorReviews.setText(oneDecimalFormat.format(Float.toString(doctorDetailsList.get(position).getAvaregeReviews())));

        //Set TextView distance to doctor
        String distanceString = oneDecimalFormat.format(doctorDetailsList.get(position).getDistanceToDoctor())
                +" "+context.getString(R.string.km);
        holder.doctorDistance.setText(distanceString);

        //Set Doctors days to next appointment
        String waitingDaysString = doctorDetailsList.get(position).getWaitingDays()+" "+context.getString(R.string.days);
        holder.doctorWaitingDays.setText(waitingDaysString);
    }

    /** Gets the image from Firebase and set in the imageView using Picasso*/
    private void setFirebaseDoctorPhoto(final ImageView smallPhotoView, int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference();

        //Path example: photos/specialty3/doctor_small_2.jpg
        storageReference.child(PHOTOS_FOLDER+"/"+SPECIALTY_FOLDER+specialty+"/"
            +SMALL_PHOTO_FILENAME_PREFIX+position+PHOTO_EXTENSION).getDownloadUrl()
            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).transform(new CropCircleTransformation()).into(smallPhotoView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("denis", "Fail to load image");
                }
            });
    }

    @Override
    public int getItemCount() {
        return doctorDetailsList.size();
    }

    class doctorDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView smallPhoto;
        TextView doctorName;
        TextView doctorReviews;
        TextView doctorDistance;
        TextView doctorWaitingDays;


        doctorDetailViewHolder(View itemView) {
            super(itemView);
            smallPhoto = itemView.findViewById(R.id.doctor_photo);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorReviews = itemView.findViewById(R.id.doctor_avg_reviews);
            doctorDistance = itemView.findViewById(R.id.doctor_distance);
            doctorWaitingDays = itemView.findViewById(R.id.waiting_days);
        }
    }
}
