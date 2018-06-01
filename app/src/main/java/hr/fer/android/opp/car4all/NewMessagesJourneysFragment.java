package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class NewMessagesJourneysFragment extends Fragment {

    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_messages_journeys, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        setupData();

        return view;
    }

    @BindView(R.id.newMessagesJourneysErrorMessage)
    TextView errorMessage;

    @BindView(R.id.newMessagesJourneysList)
    ListView journeysList;

    private void setupData(){
        List<Journey> newMessagesJourneys = ((MyApplication) getActivity().getApplication()).getNewMessagesJourneys();

        if (newMessagesJourneys.isEmpty()){
            errorMessage.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<Journey> adapter = new JourneyArrayAdadpter(getActivity(), newMessagesJourneys, CheckJourneyType.REQUEST);
        journeysList.setAdapter(adapter);
    }

}
