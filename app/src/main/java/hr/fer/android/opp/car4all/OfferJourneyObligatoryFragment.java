package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 18.12.2017..
 */

public class OfferJourneyObligatoryFragment extends Fragment {

    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_journey_obligatory, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();

        return view;
    }

    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    @BindView(R.id.startingDateField)
    EditText startingDateField;

    @BindView(R.id.startingTimeField)
    EditText startingTimeField;

    @BindView(R.id.dateError)
    TextView dateError;

    @OnClick(R.id.startingDateButton)
    void selectStartingDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                OfferJourneyObligatoryFragment.this.day = dayOfMonth;
                OfferJourneyObligatoryFragment.this.month = monthOfYear;
                OfferJourneyObligatoryFragment.this.year = year;
                startingDateField.setText(String.format("%d.%d.%d.", day, month + 1, year));
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        dialog.show();
    }

    @OnClick(R.id.startingTimeButton)
    void selectStartingTime(){
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                OfferJourneyObligatoryFragment.this.hour = hourOfDay;
                OfferJourneyObligatoryFragment.this.minute = minute;
                startingTimeField.setText(String.format("%d:%02dh", hour, minute));
            }
        }, 10, 10, true);

        dialog.show();
    }

    @BindView(R.id.offerJourneyStartingPointField)
    EditText startingPointField;

    @BindView(R.id.offerJourneyDestinationField)
    EditText destinationField;

    @BindView(R.id.offerJourneySpacesField)
    EditText spacesField;

    @BindView(R.id.highwaySwitch)
    Switch highwaySwitch;

    @BindView(R.id.offerJourneyPriceField)
    EditText priceField;

    private boolean isDataCorrect() {
        boolean correct = true;

        Drawable d = getResources().getDrawable(R.drawable.error);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        dateError.setVisibility(View.GONE);

        String startingPoint = startingPointField.getText().toString().trim();
        if (startingPoint.isEmpty()) {
            startingPointField.setError("Polazna točka mora biti upisana!", d);
            correct = false;
        }

        String destination = destinationField.getText().toString().trim();
        if (destination.isEmpty()) {
            startingPointField.setError("Odredište mora biti upisano!", d);
            correct = false;
        }

        String startingDate = startingDateField.getText().toString().trim();
        String startingTime = startingTimeField.getText().toString().trim();
        if (startingDate.isEmpty() || startingTime.isEmpty()){
            dateError.setText("Datum i vrijeme polaska moraju biti definirani!");
            dateError.setVisibility(View.VISIBLE);
            correct = false;
        } else if (!isDateCorrect()){
            dateError.setText("Datum i vrijeme polaska moraju biti kasniji od trenutnog vremena!");
            dateError.setVisibility(View.VISIBLE);
            correct = false;
        }

        try {
            int spaces = Integer.parseInt(spacesField.getText().toString().trim());

            if (spaces <= 0){
                spacesField.setError("Neispravan broj slobodnih mjesta!", d);
                correct = false;
            }
        }catch (Exception exc){
            spacesField.setError("Neispravan broj slobodnih mjesta!", d);
            correct = false;
        }

        try {
            int price = Integer.parseInt(priceField.getText().toString().trim());

            if (price <= 0){
                priceField.setError("Neispravna cijena!", d);
                correct = false;
            }
        }catch (Exception exc){
            priceField.setError("Neispravna cijena!", d);
            correct = false;
        }

        return correct;
    }

    private boolean isDateCorrect(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        Date currentDate = cal.getTime();

        cal.set(year, month, day, hour, minute);
        Date chosenDate = cal.getTime();

        return chosenDate.compareTo(currentDate) >= 0;
    }

    private void processData(){
        Journey journey = new Journey();

        journey.setDriver(user);
        journey.setStartingPoint(startingPointField.getText().toString().trim());
        journey.setDestination(destinationField.getText().toString().trim());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(year, month, day);
        journey.setStartingDate(cal.getTime());
        journey.setStartingTime(startingTimeField.getText().toString().trim());

        journey.setNumberOfSeats(Integer.parseInt(spacesField.getText().toString().trim()));
        journey.setByHighway(highwaySwitch.isChecked());
        journey.setPrice(Integer.parseInt(priceField.getText().toString().trim()));

        ((MyApplication) getActivity().getApplication()).setJourney(journey);
    }

    @OnClick(R.id.offerJourneyOptional1)
    void goToOptionalScreen(){
        if (isDataCorrect()){
            processData();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            if (user instanceof Moderator) {
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new OfferJourneyOptionalFragment()).commit();
            }else{
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new OfferJourneyOptionalFragment()).commit();
            }
        }
    }

    @OnClick(R.id.offerJourneyEnd1)
    void goToEndScreen(){
        if (isDataCorrect()){
            processData();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            if (user instanceof Moderator) {
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new OfferJourneyEndFragment()).commit();
            }else{
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new OfferJourneyEndFragment()).commit();
            }
        }
    }

}
