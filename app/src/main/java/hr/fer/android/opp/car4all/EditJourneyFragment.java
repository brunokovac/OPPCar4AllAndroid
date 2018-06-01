package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 18.12.2017..
 */

public class EditJourneyFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_journey, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();

        setupData();

        return view;
    }

    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    @BindView(R.id.editStartingTimeField)
    EditText startingTimeField;

    @BindView(R.id.editTimeError)
    TextView timeError;

    @OnClick(R.id.editStartingTimeButton)
    void selectStartingTime(){
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                EditJourneyFragment.this.hour = hourOfDay;
                EditJourneyFragment.this.minute = minute;
                startingTimeField.setText(String.format("%d:%02dh", hour, minute));
            }
        }, 10, 10, true);

        dialog.show();
    }

    @BindView(R.id.editJourneyInfo)
    TextView journeyInfoField;

    @BindView(R.id.editJourneySpacesField)
    EditText spacesField;

    @BindView(R.id.editJourneyHighwaySwitch)
    Switch highwaySwitch;

    @BindView(R.id.editJourneyPriceField)
    EditText priceField;

    private boolean isDataCorrect() {
        boolean correct = true;

        Drawable d = getResources().getDrawable(R.drawable.error);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

        timeError.setVisibility(View.GONE);

        String startingTime = startingTimeField.getText().toString().trim();
        if (startingTime.isEmpty()) {
            timeError.setText("Vrijeme polaska mora biti definirano!");
            timeError.setVisibility(View.VISIBLE);
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

        if (roundTripSwitch.isChecked()){
            if (returnDateField.getText().toString().trim().isEmpty() || returnTimeField.getText().toString().trim().isEmpty()){
                returnDateError.setText("Datum i vrijeme povratka moraju biti definirani!");
                returnDateError.setVisibility(View.VISIBLE);
                correct = false;
            }
        }

        if (!isStartingDateCorrect()){
            startingTimeField.setError("Termin polaska mora biti kasniji od trenutnog vremena!");
            correct = false;
        }

        if (roundTripSwitch.isChecked()) {
            if (!areDatesCorrect()) {
                returnDateError.setText("Termin povratka mora biti kasniji od termina polaska!");
                returnDateError.setVisibility(View.VISIBLE);
                correct = false;
            }
        }

        return correct;
    }

    private boolean isStartingDateCorrect(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentDate = cal.getTime();

        cal.setTime(journey.getStartingDate());
        String stringTime = startingTimeField.getText().toString();
        String[] time = stringTime.substring(0, stringTime.length() - 1).split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        Date startingDate = cal.getTime();

        return startingDate.compareTo(currentDate) > 0;
    }

    private boolean areDatesCorrect(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        Date currentDate = cal.getTime();

        cal.setTime(journey.getStartingDate());
        String stringTime = startingTimeField.getText().toString();
        String[] time = stringTime.substring(0, stringTime.length() - 1).split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        Date startingDate = cal.getTime();

        String stringDate = returnDateField.getText().toString();
        String[] date = stringDate.split("\\.");
        String stringTime2 = returnTimeField.getText().toString();
        String[] time2 = stringTime2.substring(0, stringTime2.length() - 1).split(":");
        cal.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]),
                Integer.parseInt(time2[0]), Integer.parseInt(time2[1]));
        Date returnDate = cal.getTime();

        return startingDate.compareTo(currentDate) >= 0 && returnDate.compareTo(startingDate)>=0;
    }



    private void processData(){
        journey.setStartingTime(startingTimeField.getText().toString().trim());

        journey.setNumberOfSeats(Integer.parseInt(spacesField.getText().toString().trim()));
        journey.setByHighway(highwaySwitch.isChecked());
        journey.setPrice(Integer.parseInt(priceField.getText().toString().trim()));

        if (roundTripSwitch.isChecked()) {
            journey.setRoundTrip(true);

            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            String stringDate = returnDateField.getText().toString();
            String[] date = stringDate.split("\\.");
            cal.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
            journey.setReturnDate(cal.getTime());
            journey.setReturnTime(returnTimeField.getText().toString().trim());
        }else{
            journey.setRoundTrip(false);
            journey.setReturnDate(new Date(0));
            journey.setReturnTime("");
        }

        String passingPlaces = passingPlacesField.getText().toString().trim();
        if (!passingPlaces.isEmpty()){
            journey.setPassingPlaces(new ArrayList<String>());
            String[] places = passingPlaces.split("\n");
            for (String place : places){
                journey.addPassingPlace(place.trim());
            }
        }

        String comment = commentField.getText().toString().trim();
        if (!comment.isEmpty()){
            journey.setComment(comment);
        }
    }

    @BindView(R.id.editJourneyRoundTripSwitch)
    Switch roundTripSwitch;

    @BindView(R.id.editJourneyReturnDateTimeForm)
    LinearLayout returnDateTimeForm;

    @OnCheckedChanged(R.id.editJourneyRoundTripSwitch)
    void showReturnDateTimeForm(){
        if (roundTripSwitch.isChecked()){
            returnDateTimeForm.setVisibility(View.VISIBLE);
        }else{
            returnDateTimeForm.setVisibility(View.GONE);
        }
    }

    @BindView(R.id.editJourneyReturnDateField)
    EditText returnDateField;

    @BindView(R.id.editJourneyReturnTimeField)
    EditText returnTimeField;

    @OnClick(R.id.editJourneyReturnDateButton)
    void selectReturnDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                EditJourneyFragment.this.day = dayOfMonth;
                EditJourneyFragment.this.month = monthOfYear;
                EditJourneyFragment.this.year = year;
                returnDateField.setText(String.format("%d.%d.%d.", day, month + 1, year));
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        dialog.show();
    }

    @OnClick(R.id.editJourneyReturnTimeButton)
    void selectReturnTime(){
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                EditJourneyFragment.this.hour = hourOfDay;
                EditJourneyFragment.this.minute = minute;
                returnTimeField.setText(String.format("%d:%02dh", hour, minute));
            }
        }, 10, 10, true);

        dialog.show();
    }

    @BindView(R.id.editJourneyReturnDateError)
    TextView returnDateError;

    @BindView(R.id.editJourneyPassingPlacesField)
    EditText passingPlacesField;

    @BindView(R.id.editJourneyCommentField)
    EditText commentField;


    private void setupData(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(journey.getStartingDate());
        journeyInfoField.setText(String.format("%n%s%n%s%n%d.%d.%d.%n", journey.getStartingPoint(), journey.getDestination(),
                cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));

        startingTimeField.setText(journey.getStartingTime());
        spacesField.setText(String.valueOf(journey.getNumberOfSeats()));
        highwaySwitch.setChecked(journey.isByHighway());
        priceField.setText(String.valueOf(journey.getPrice()));

        if (journey.isRoundTrip()){
            roundTripSwitch.performClick();

            cal.setTime(journey.getReturnDate());
            returnDateField.setText(String.format("%d.%d.%d.",
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
            returnTimeField.setText(journey.getReturnTime());
        }

        for (String place : journey.getPassingPlaces()){
            passingPlacesField.append(String.format("%s%n", place));
        }

        commentField.setText(journey.getComment());
    }

    @OnClick(R.id.editJourneyDiscardButton)
    void discardChanges(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.editJourneySaveButton)
    void saveChanges(){
        if (isDataCorrect()){
            processData();

            try{
                DAOProvider.getDao().editJourney(journey);
                Toast.makeText(getActivity(), "Promjene su spremljene!", Toast.LENGTH_SHORT).show();
            }catch (Exception exc){
                Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                return;
            }

            getActivity().onBackPressed();
        }
    }

}
