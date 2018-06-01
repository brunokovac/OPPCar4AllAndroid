package hr.fer.android.opp.car4all;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import hr.fer.android.opp.car4all.models.JourneyRequest;

/**
 * Created by Bruno on 4.1.2018..
 */

public class RequestArrayAdadpter extends ArrayAdapter<JourneyRequest> {

    private Context context;
    private List<JourneyRequest> requests;

    public RequestArrayAdadpter(Context context, List<JourneyRequest> requests){
        super(context, -1, requests);
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.request_element, parent, false);

        ImageView profilePicture = (ImageView) rowView.findViewById(R.id.requestItemDriverProfilePicture);
        TextView driverUsernameField = (TextView) rowView.findViewById(R.id.requestItemDriverUsernameField);
        TextView startingPointField = (TextView) rowView.findViewById(R.id.requestItemStartingPointField);
        TextView destinationField = (TextView) rowView.findViewById(R.id.requestItemDestinationField);
        TextView startingDateTimeField = (TextView) rowView.findViewById(R.id.requestItemStartingDateTimeField);

        final JourneyRequest request = requests.get(position);

        Glide.with(context).load(Uri.parse(request.getJourney().getDriver().getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);

        driverUsernameField.setText(request.getJourney().getDriver().getUsername());
        startingPointField.setText(request.getJourney().getStartingPoint());
        destinationField.setText(request.getJourney().getDestination());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(request.getJourney().getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                request.getJourney().getStartingTime()));

        ((Button) rowView.findViewById(R.id.requestItemCancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelRequestDialog(request);
            }
        });

        return rowView;
    }

    private void showCancelRequestDialog(final JourneyRequest request){
        final JourneyRequest finalRequest = request;
        final EditText cancelMessageField = new EditText(context);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        if (request.isAccepted() && request.getJourney().isLocked()){
            alertDialogBuilder
                    .setTitle("Odustajanje od zahtjeva")
                    .setMessage(String.format("Ponuditelj putovanja je već zaključao putovanje. Jeste li sigurni da " +
                            "želite odustati od zahtjeva?%n%nAko želite odustati od putovanja, napišite razlog odustajanja."))
                    .setView(cancelMessageField);
        }else{
            alertDialogBuilder
                    .setTitle("Odustajanje od zahtjeva");
        }

        alertDialogBuilder
                        .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    finalRequest.cancel(cancelMessageField.getText().toString().trim());
                                    Toast.makeText(context, "Zahtjev je otkazan!", Toast.LENGTH_SHORT).show();

                                    RequestArrayAdadpter.this.remove(request);
                                    RequestArrayAdadpter.this.notifyDataSetChanged();
                                }catch (Exception exc){
                                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                                }

                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("NE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        alertDialogBuilder.show();
    }
}
