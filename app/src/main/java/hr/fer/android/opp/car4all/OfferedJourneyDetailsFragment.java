package hr.fer.android.opp.car4all;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class OfferedJourneyDetailsFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offered_journey_details, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();

        setupData();

        return view;
    }

    @BindView(R.id.offeredJourneyDetailsStartingPointField)
    TextView startingPointField;

    @BindView(R.id.offeredJourneyDetailsDestinationField)
    TextView destinationField;

    @BindView(R.id.offeredJourneyDetailsStartingDateTimeField)
    TextView startingDateTimeField;

    @BindView(R.id.offeredJourneyDetailsReturnTitle)
    TextView returnTitle;

    @BindView(R.id.offeredJourneyDetailsReturnDateTimeField)
    TextView returnDateTimeField;

    @BindView(R.id.offeredJourneyDetailsPassingPlacesField)
    TextView passingPlacesField;

    @BindView(R.id.offeredJourneyDetailsNumberOfSeatsField)
    TextView numberOfSeatsField;

    @BindView(R.id.offeredJourneyDetailsHighwayField)
    TextView highwayField;

    @BindView(R.id.offeredJourneyDetailsPriceField)
    TextView priceField;

    @BindView(R.id.offeredJourneyDetailsCommentField)
    TextView commentField;

    private void setupData(){
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
        }

        for (String place : journey.getPassingPlaces()){
            passingPlacesField.append(String.format("%s%n", place));
        }

        numberOfSeatsField.setText(String.format("%s %d", numberOfSeatsField.getText().toString(), journey.getNumberOfSeats()));
        highwayField.setText(String.format("%s %s", highwayField.getText().toString(), journey.isByHighway() ? "DA" : "NE"));
        priceField.setText(String.format("%s %d", priceField.getText().toString(), journey.getPrice()));
        commentField.setText(journey.getComment());
    }

    @OnClick(R.id.offeredJourneyDetailsChatTitle)
    void goToChat(){
        goToJourneyChat();
    }

    @OnClick(R.id.offeredJourneyDetailsChatButton)
    void goToJourneyChat(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (user instanceof Moderator) {
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new JourneyChatFragment()).commit();
        }else{
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new JourneyChatFragment()).commit();
        }
    }

}
