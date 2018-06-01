package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.User;

public class RateJourneyFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_journey, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();
        setupData();

        return view;
    }

    @BindView(R.id.rateJourneyPassengersList)
    ListView passengersList;

    @BindView(R.id.rateJourneyStartingPointField)
    TextView startingPointField;

    @BindView(R.id.rateJourneyDestinationField)
    TextView destinationField;

    @BindView(R.id.rateJourneyStartingDateTimeField)
    TextView startingDateTimeField;

    private void setupData(){
        startingPointField.setText(journey.getStartingPoint());
        destinationField.setText(journey.getDestination());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(journey.getStartingDate());
        startingDateTimeField.setText(String.format("%d.%d.%d. %s",
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                journey.getStartingTime()));

        List<User> passengers = journey.getPassengers();
        passengers.remove(user);
        ArrayAdapter<User> adapter = new PassengersArrayAdadpter(getActivity(), passengers);
        passengersList.setAdapter(adapter);
    }

}
