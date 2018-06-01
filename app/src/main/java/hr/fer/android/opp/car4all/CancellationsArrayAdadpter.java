package hr.fer.android.opp.car4all;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Cancellation;
import hr.fer.android.opp.car4all.models.JourneyRequest;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;

/**
 * Created by Bruno on 4.1.2018..
 */

public class CancellationsArrayAdadpter extends ArrayAdapter<Cancellation> {

    private Context context;
    private List<Cancellation> cancellations;

    public CancellationsArrayAdadpter(Context context, List<Cancellation> cancellations){
        super(context, -1, cancellations);
        this.context = context;
        this.cancellations = cancellations;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.cancellation_element, parent, false);

        ImageView profilePicture = (ImageView) rowView.findViewById(R.id.cancellationItemUserProfilePicture);
        TextView usernameField = (TextView) rowView.findViewById(R.id.cancellationItemUsernameField);
        TextView startingPointField = (TextView) rowView.findViewById(R.id.cancellationItemStartingPointField);
        TextView destinationField = (TextView) rowView.findViewById(R.id.cancellationItemDestinationField);
        TextView startingDateTimeField = (TextView) rowView.findViewById(R.id.cancellationItemStartingDateTimeField);
        TextView cancelMessageField = (TextView) rowView.findViewById(R.id.cancellationItemCancelMessageField);

        final Cancellation cancellation = cancellations.get(position);

        Glide.with(context).load(Uri.parse(cancellation.getUser().getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);

        usernameField.setText(cancellation.getUser().getUsername());
        startingPointField.setText(cancellation.getJourney().getStartingPoint());
        destinationField.setText(cancellation.getJourney().getDestination());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(cancellation.getJourney().getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                cancellation.getJourney().getStartingTime()));

        cancelMessageField.setText(cancellation.getCancelMessage());

        ((Button) rowView.findViewById(R.id.cancellationItemDismissButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DAOProvider.getDao().checkCancellation(cancellation);
                    CancellationsArrayAdadpter.this.remove(CancellationsArrayAdadpter.this.getItem(position));
                    CancellationsArrayAdadpter.this.notifyDataSetChanged();

                    Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                    if (person instanceof Moderator) {
                        ((ModeratorHomeActivity) ((AppCompatActivity) context)).checkForNewCancellations();
                    }else{
                        ((UserHomeActivity) ((AppCompatActivity) context)).checkForNewCancellations();
                    }

                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.cancellationItemReviewUserButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setPersonBeingChecked(cancellation.getUser());
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setCancellationBeingChecked(cancellation);

                ReviewUserFragment fragment = new ReviewUserFragment();
                fragment.setResultOfCancellation(true);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                if (person instanceof Moderator) {
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, fragment).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, fragment).commit();
                }
            }
        });

        return rowView;
    }


}
