package hr.fer.android.opp.car4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class RateLastJourneyFragment extends Fragment {

    @BindView(R.id.rateLastJourneyErrorMessage)
    TextView errorMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_last_journey, container, false);
        ButterKnife.bind(this, view);

        User user = (User) ((MyApplication) getActivity().getApplication()).getPerson();

        List<Journey> journeys = null;
        try{
            journeys = user.getAttendedJourneys();
        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }

        if (!journeys.isEmpty()){
            ((MyApplication) getActivity().getApplication()).setJourney(journeys.get(0));

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            if (user instanceof Moderator) {
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new RateJourneyFragment()).commit();
            }else{
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new RateJourneyFragment()).commit();
            }
        }else{
            errorMessage.setVisibility(View.VISIBLE);
        }

        return view;
    }

}
