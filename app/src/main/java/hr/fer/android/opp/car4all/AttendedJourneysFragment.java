package hr.fer.android.opp.car4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Message;
import hr.fer.android.opp.car4all.models.User;

public class AttendedJourneysFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attended_journeys, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();
        setupData();

        return view;
    }

    @BindView(R.id.attendedJourneysErrorMessage)
    TextView errorMessage;

    @BindView(R.id.attendedJourneysList)
    ListView journeysList;

    private void setupData(){
        errorMessage.setVisibility(View.GONE);

        try{
            List<Journey> journeys = user.getAttendedJourneys();

            if (journeys.isEmpty()){
                errorMessage.setVisibility(View.VISIBLE);
            }

            ArrayAdapter<Journey> adapter = new JourneyArrayAdadpter(getActivity(), journeys, CheckJourneyType.REVIEW);
            journeysList.setAdapter(adapter);

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

}
