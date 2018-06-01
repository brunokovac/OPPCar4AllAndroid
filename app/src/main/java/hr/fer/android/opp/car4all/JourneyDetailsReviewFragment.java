package hr.fer.android.opp.car4all;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class JourneyDetailsReviewFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journey_details_review, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();

        setupData();

        return view;
    }

    @BindView(R.id.journeyDetailsReviewStartingPointField)
    TextView startingPointField;

    @BindView(R.id.journeyDetailsReviewDestinationField)
    TextView destinationField;

    @BindView(R.id.journeyDetailsReviewStartingDateTimeField)
    TextView startingDateTimeField;

    @BindView(R.id.journeyDetailsReviewPassingPlacesField)
    TextView passingPlacesField;

    @BindView(R.id.journeyDetailsReviewPassengersList)
    ListView passengersList;

    private void setupData(){
        startingPointField.setText(journey.getStartingPoint());
        destinationField.setText(journey.getDestination());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(journey.getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s", cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR), journey.getStartingTime()));

        try{
            for (String place : journey.getPassingPlaces()){
                passingPlacesField.append(String.format("%s%n", place));
            }

            Calendar cal2 = Calendar.getInstance(TimeZone.getDefault());
            Date currentDate = cal2.getTime();

            List<User> passengers = journey.getPassengers();
            passengers.remove(user);
            ArrayAdapter<User> adapter = new PassengersArrayAdadpter(getActivity(), passengers);
            passengersList.setAdapter(adapter);

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.journeyDetailsReviewChatTitle)
    void goToChat(){
        goToJourneyChat();
    }

    @OnClick(R.id.journeyDetailsReviewChatButton)
    void goToJourneyChat(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (user instanceof Moderator) {
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new JourneyChatFragment()).commit();
        }else{
            fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new JourneyChatFragment()).commit();
        }
    }

}
