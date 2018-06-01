package hr.fer.android.opp.car4all;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAO;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Message;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class JourneyDetailsRequestFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journey_details_request, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();

        setupData();

        return view;
    }

    @BindView(R.id.journeyDetailsStartingPointField)
    TextView startingPointField;

    @BindView(R.id.journeyDetailsDestinationField)
    TextView destinationField;

    @BindView(R.id.journeyDetailsStartingDateTimeField)
    TextView startingDateTimeField;

    @BindView(R.id.journeyDetailsReturnTitle)
    TextView returnTitle;

    @BindView(R.id.journeyDetailsReturnDateTimeField)
    TextView returnDateTimeField;

    @BindView(R.id.journeyDetailsPassingPlacesField)
    TextView passingPlacesField;

    @BindView(R.id.journeyDetailsNumberOfSeatsField)
    TextView numberOfSeatsField;

    @BindView(R.id.journeyDetailsHighwayField)
    TextView highwayField;

    @BindView(R.id.journeyDetailsPriceField)
    TextView priceField;

    @BindView(R.id.journeyDetailsCommentField)
    TextView commentField;

    @BindView(R.id.journeyDetailsDriverInfo)
    LinearLayout driverInfo;

    @BindView(R.id.journeyDetailsDriverPicture)
    ImageView driverPicture;

    @BindView(R.id.journeyDetailsDriverUsernameField)
    TextView driverUsernameField;

    @BindView(R.id.journeyDetailsSendRequestButton)
    Button requestButton;

    private void setupData(){
        if (user.getPersonID() == journey.getDriver().getPersonID()){
            requestButton.setVisibility(View.GONE);
        }

        startingPointField.setText(journey.getStartingPoint());
        destinationField.setText(journey.getDestination());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(journey.getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s", cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), journey.getStartingTime()));

        if (journey.isRoundTrip()){
            returnTitle.setVisibility(View.VISIBLE);
            cal.setTime(journey.getReturnDate());
            returnDateTimeField.setText(String.format("%d.%d.%d. %s", cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), journey.getReturnTime()));
            returnDateTimeField.setVisibility(View.VISIBLE);
        }

        for (String place : journey.getPassingPlaces()){
            passingPlacesField.append(String.format("%s%n", place));
        }

        numberOfSeatsField.setText(String.format("%s %d", numberOfSeatsField.getText().toString(), journey.getNumberOfSeats()));
        highwayField.setText(String.format("%s %s", highwayField.getText().toString(), journey.isByHighway() ? "DA" : "NE"));
        priceField.setText(String.format("%s %d", priceField.getText().toString(), journey.getPrice()));
        commentField.setText(journey.getComment());

        Glide.with(this).load(Uri.parse(journey.getDriver().getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(driverPicture);
        driverUsernameField.setText(journey.getDriver().getUsername());
    }

    @OnClick(R.id.journeyDetailsChatTitle)
    void goToChat(){
        goToJourneyChat();
    }

    @OnClick(R.id.journeyDetailsChatButton)
    void goToJourneyChat(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (user instanceof Moderator) {
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new JourneyChatFragment()).commit();
        }else{
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new JourneyChatFragment()).commit();
        }
    }

    @OnClick(R.id.journeyDetailsDriverInfo)
    void showDriverInformationAndReviews(){
        try{
            ((MyApplication) getActivity().getApplication()).setPersonBeingChecked(DAOProvider.getDao().getPersonForUsername(journey.getDriver().getUsername()));
        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (user instanceof Moderator) {
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new UserInfoFragment()).commit();
        }else{
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new UserInfoFragment()).commit();
        }
    }

    @OnClick(R.id.journeyDetailsSendRequestButton)
    void sendJourneyRequest(){
        try{
            boolean sent = user.sendJourneyRequest(journey);

            if (sent){
                Toast.makeText(getActivity(), "Vaš zahtjev je poslan!", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }else{
                Toast.makeText(getActivity(), "Zahtjev je već ranije poslan!", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

}
