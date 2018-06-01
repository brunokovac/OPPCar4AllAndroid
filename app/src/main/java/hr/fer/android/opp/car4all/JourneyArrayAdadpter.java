package hr.fer.android.opp.car4all;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;

/**
 * Created by Bruno on 4.1.2018..
 */

public class JourneyArrayAdadpter extends ArrayAdapter<Journey> {

    private Context context;
    private List<Journey> journeys;
    private CheckJourneyType type;

    public JourneyArrayAdadpter(Context context, List<Journey> journeys, CheckJourneyType type){
        super(context, -1, journeys);
        this.context = context;
        this.journeys = journeys;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.offered_journey_list_element, parent, false);

        TextView startingPointField = (TextView) rowView.findViewById(R.id.journeyItemStartingPoint);
        final TextView destinationField = (TextView) rowView.findViewById(R.id.journeyItemDestination);
        TextView startingDateTimeField = (TextView) rowView.findViewById(R.id.journeyItemDateTime);
        TextView priceField = (TextView) rowView.findViewById(R.id.journeyItemPrice);

        final Journey journey = journeys.get(position);

        startingPointField.setText(journey.getStartingPoint());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(journey.getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                journey.getStartingTime()));

        destinationField.setText(journeys.get(position).getDestination());
        priceField.setText(String.format("%dkn", journey.getPrice()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ((MyApplication) (((AppCompatActivity)context).getApplication())).setJourney(
                            DAOProvider.getDao().getJourneyForID(journey.getJourneyID()));

                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    Fragment fragment = null;

                    switch (type){
                        case REQUEST:
                            fragment = new JourneyDetailsRequestFragment();
                            break;
                        case REVIEW:
                            fragment = new JourneyDetailsReviewFragment();
                            break;
                        case OFFERED:
                            fragment = new OfferedJourneyDetailsFragment();
                            break;
                        default:
                            break;
                    }

                    Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                    if (person instanceof Moderator) {
                        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, fragment).commit();
                    }else{
                        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, fragment).commit();
                    }
                }catch(Exception e){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (type == CheckJourneyType.OFFERED){
            ((GridLayout) rowView.findViewById(R.id.journeyItemButtons)).setVisibility(View.VISIBLE);
            setupButtons(rowView, journey);
        }

        return rowView;
    }

    private void setupButtons(View rowView, final Journey journey){
        ((Button) rowView.findViewById(R.id.journeyItemEditButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setJourney(journey);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                if (person instanceof Moderator) {
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new EditJourneyFragment()).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new EditJourneyFragment()).commit();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.journeyItemRequestsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setJourney(journey);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                if (person instanceof Moderator) {
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new RequestsForOfferedJourneyFragment()).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new RequestsForOfferedJourneyFragment()).commit();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.journeyItemCancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelJourneyDialog(journey);
            }
        });

        ((Button) rowView.findViewById(R.id.journeyItemLockButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (journey.isLocked()){
                    Toast.makeText(context, "Putovanje je već zaključano!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try{
                    journey.lock();
                    Toast.makeText(context, "Putovanje je zaključano!", Toast.LENGTH_SHORT).show();

                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showCancelJourneyDialog(final Journey journey){
        final Journey finalJourney = journey;
        final EditText cancelMessageField = new EditText(context);

        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(context)
                        .setTitle("Otkazivanje putovanja")
                        .setMessage("Koji je razlog otkazivanja putovanja?")
                        .setView(cancelMessageField)
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Otkaži", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    finalJourney.cancel(cancelMessageField.getText().toString().trim());
                                    Toast.makeText(context, "Putovanje je otkazano!", Toast.LENGTH_SHORT).show();

                                    JourneyArrayAdadpter.this.remove(journey);
                                    JourneyArrayAdadpter.this.notifyDataSetChanged();
                                }catch (Exception exc){
                                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                                }

                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.show();
    }
}
