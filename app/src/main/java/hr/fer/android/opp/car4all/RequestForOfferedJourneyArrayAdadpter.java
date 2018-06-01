package hr.fer.android.opp.car4all;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.JourneyRequest;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;

/**
 * Created by Bruno on 4.1.2018..
 */

public class RequestForOfferedJourneyArrayAdadpter extends ArrayAdapter<JourneyRequest> {

    private Context context;
    private List<JourneyRequest> requests;

    public RequestForOfferedJourneyArrayAdadpter(Context context, List<JourneyRequest> requests){
        super(context, -1, requests);
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.request_for_offered_journey_element, parent, false);

        final ImageView profilePicture = (ImageView) rowView.findViewById(R.id.requestForOfferedJourneyItemUserProfilePicture);
        TextView userUsernameField = (TextView) rowView.findViewById(R.id.requestForOfferedJourneyItemUserUsernameField);
        TextView startingPointField = (TextView) rowView.findViewById(R.id.requestForOfferedJourneyItemStartingPointField);
        TextView destinationField = (TextView) rowView.findViewById(R.id.requestForOfferedJourneyItemDestinationField);
        TextView startingDateTimeField = (TextView) rowView.findViewById(R.id.requestForOfferedJourneyItemStartingDateTimeField);

        final JourneyRequest request = requests.get(position);

        Glide.with(context).load(Uri.parse(request.getSender().getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);

        userUsernameField.setText(request.getSender().getUsername());
        startingPointField.setText(request.getJourney().getStartingPoint());
        destinationField.setText(request.getJourney().getDestination());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(request.getJourney().getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                request.getJourney().getStartingTime()));

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ((MyApplication) ((AppCompatActivity) context).getApplication()).setPersonBeingChecked(
                            DAOProvider.getDao().getPersonForUsername(request.getSender().getUsername()));
                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                    return;
                }

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                Person person = ((MyApplication) ((AppCompatActivity) context).getApplication()).getPerson();
                if (person instanceof Moderator) {
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new UserInfoFragment()).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new UserInfoFragment()).commit();
                }
            }
        });

        userUsernameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicture.performClick();
            }
        });

        ((Button) rowView.findViewById(R.id.requestForOfferedJourneyItemDeclineButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    request.declineRequest();
                    Toast.makeText(context, "Zahtjev je odbijen!", Toast.LENGTH_SHORT).show();

                    RequestForOfferedJourneyArrayAdadpter.this.remove(request);
                    RequestForOfferedJourneyArrayAdadpter.this.notifyDataSetChanged();
                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.requestForOfferedJourneyItemAcceptButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    request.acceptRequest();
                    Toast.makeText(context, "Zahtjev je prihvaćen!", Toast.LENGTH_SHORT).show();

                    RequestForOfferedJourneyArrayAdadpter.this.remove(request);
                    RequestForOfferedJourneyArrayAdadpter.this.notifyDataSetChanged();
                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }

}
