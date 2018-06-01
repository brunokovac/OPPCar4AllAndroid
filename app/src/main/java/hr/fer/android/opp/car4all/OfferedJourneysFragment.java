package hr.fer.android.opp.car4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.User;

public class OfferedJourneysFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offered_journeys, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();
        setupData();

        return view;
    }

    @BindView(R.id.offeredJourneysErrorMessage)
    TextView errorMessage;

    @BindView(R.id.offeredJourneysList)
    ListView journeysList;

    private void setupData(){
        errorMessage.setVisibility(View.GONE);

        try{
            List<Journey> journeys = user.getOfferedJourneys();

            if (journeys.isEmpty()){
                errorMessage.setVisibility(View.VISIBLE);
            }

            ArrayAdapter<Journey> adapter = new JourneyArrayAdadpter(getActivity(), journeys, CheckJourneyType.OFFERED);
            journeysList.setAdapter(adapter);

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

}
