package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

public class FindJourneyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_journey, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private int day;
    private int month;
    private int year;

    @BindView(R.id.findJourneysStartingDateField)
    TextView startingDateField;

    @OnClick(R.id.findJourneysDateButton)
    void selectStartingate(){
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());


        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                FindJourneyFragment.this.day = dayOfMonth;
                FindJourneyFragment.this.month = monthOfYear;
                FindJourneyFragment.this.year = year;
                startingDateField.setText(String.format("%d.%d.%d.", day, month + 1, year));
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        dialog.show();
    }

    @BindView(R.id.findJourneysStartingPointField)
    EditText startingPointField;

    @BindView(R.id.findJourneysDestinationField)
    EditText destinationField;

    @BindView(R.id.findJourneysErrorMessage)
    TextView errorMessage;

    @BindView(R.id.findJourneysList)
    ListView journeysList;

    @OnClick(R.id.findJourneysGoButton)
    void findJourneys(){
        errorMessage.setVisibility(View.GONE);

        if (startingPointField.getText().toString().trim().isEmpty() || destinationField.getText().toString().trim().isEmpty()
                || startingDateField.getText().toString().trim().isEmpty()){
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        String start = startingPointField.getText().toString().trim().toUpperCase();
        String destination = destinationField.getText().toString().trim().toUpperCase();

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(year, month, day);
        Date startDate = cal.getTime();

        try{
            User activeUser = (User) ((MyApplication) getActivity().getApplication()).getPerson();
            List<Journey> journeys = DAOProvider.getDao().getSpecificJourneys(start, destination, startDate, activeUser);

            if (journeys.isEmpty()){
                errorMessage.setText("Nisu pronađena odgovarajuća putovanja!");
                errorMessage.setVisibility(View.VISIBLE);
            }

            ArrayAdapter<Journey> adapter = new JourneyArrayAdadpter(getActivity(), journeys, CheckJourneyType.REQUEST);
            journeysList.setAdapter(adapter);

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

}
