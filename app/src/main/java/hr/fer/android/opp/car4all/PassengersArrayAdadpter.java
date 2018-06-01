package hr.fer.android.opp.car4all;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 4.1.2018..
 */

public class PassengersArrayAdadpter extends ArrayAdapter<User> {

    private Context context;
    private List<User> passengers;

    public PassengersArrayAdadpter(Context context, List<User> passengers){
        super(context, -1, passengers);
        this.context = context;
        this.passengers = passengers;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.journey_passenger_element, parent, false);

        ImageView profilePicture = (ImageView) rowView.findViewById(R.id.passengerItemProfilePicture);
        TextView usernameField = (TextView) rowView.findViewById(R.id.passengerItemUsername);

        Glide.with(context).load(Uri.parse(passengers.get(position).getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);
        usernameField.setText(passengers.get(position).getUsername());

        Journey journey = ((MyApplication) (((AppCompatActivity)context).getApplication())).getJourney();
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        if (journey.getStartingDate().compareTo(cal.getTime()) < 0) {
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((MyApplication) (((AppCompatActivity) context).getApplication())).setPersonBeingChecked(passengers.get(position));

                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                        Person person = ((MyApplication) (((AppCompatActivity) context).getApplication())).getPerson();
                        if (person instanceof Moderator) {
                            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new ReviewUserFragment()).commit();
                        }else{
                            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new ReviewUserFragment()).commit();
                        }

                    } catch (Exception e) {
                        Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return rowView;
    }
}
