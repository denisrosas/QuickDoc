package com.example.android.quickdoc.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.quickdoc.AppointmentDetailsActivity;
import com.example.android.quickdoc.R;
import com.example.android.quickdoc.dataClasses.AppointmentTime;
import com.example.android.quickdoc.dataClasses.DateUtils;
import com.example.android.quickdoc.dataClasses.SpecialtyNames;
import com.example.android.quickdoc.dataClasses.UserAppointment;

import java.util.ArrayList;

public class NextAppointMentsListAdapter extends RecyclerView.Adapter<NextAppointMentsListAdapter.NextAppViewHolder>{

    private ArrayList<UserAppointment> userAppointmentsList;
    private ArrayList<String> childKeys;
    private Context context;

    private static final String USER_APPOINTMENT = "USER_APPOINTMENT";
    private static final String CHILD_KEY = "CHILD_KEY";

    public NextAppointMentsListAdapter(ArrayList<UserAppointment> userAppointmentsList, ArrayList<String> childKeys,Context context) {
        this.userAppointmentsList = userAppointmentsList;
        this.childKeys = childKeys;
        this.context = context;
    }

    @NonNull
    @Override
    public NextAppointMentsListAdapter.NextAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_appointment_item, parent, false);
        return new NextAppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NextAppointMentsListAdapter.NextAppViewHolder holder, final int position) {
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AppointmentDetailsActivity.class);
                intent.putExtra(USER_APPOINTMENT,userAppointmentsList.get(holder.getAdapterPosition()));
                intent.putExtra(CHILD_KEY, childKeys.get(holder.getAdapterPosition()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        String localDate = DateUtils.getAdaptedDate(userAppointmentsList.get(holder.getAdapterPosition()).getDate(),context);
        holder.date.setText(localDate);

        holder.time.setText(AppointmentTime.getTimeFromIndex(userAppointmentsList.get(holder.getAdapterPosition()).getTime()));

        holder.specialty.setText(SpecialtyNames.getSpecialtyName(context, userAppointmentsList.get(position).getSpecialty()));

    }


    @Override
    public int getItemCount() {
        return userAppointmentsList.size();
    }

    class NextAppViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout constraintLayout;
        TextView date;
        TextView time;
        TextView specialty;

        NextAppViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraint_layout_next_app);
            date = itemView.findViewById(R.id.tv_app_date);
            time = itemView.findViewById(R.id.tv_app_time);
            specialty = itemView.findViewById(R.id.tv_specialty);

        }
    }
}
