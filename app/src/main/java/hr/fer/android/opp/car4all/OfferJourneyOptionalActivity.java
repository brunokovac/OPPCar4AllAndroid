package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.models.Journey;

/**
 * Created by Bruno on 18.12.2017..
 */

public class OfferJourneyOptionalActivity extends AppCompatActivity {

    private Journey journey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_journey_optional);

        ButterKnife.bind(this);

        journey = ((MyApplication) getApplication()).getJourney();
    }

    @BindView(R.id.roundTripSwitch)
    Switch roundTripSwitch;

    @BindView(R.id.returnDateTimeForm)
    LinearLayout returnDateTimeForm;

    @OnCheckedChanged(R.id.roundTripSwitch)
    void showReturnDateTimeForm(){
        if (roundTripSwitch.isChecked()){
            returnDateTimeForm.setVisibility(View.VISIBLE);
        }else{
            returnDateTimeForm.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.offerJourneyObligatory2)
    void goToObligatoryScreen(){
        onBackPressed();
    }

    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    @BindView(R.id.offerJourneyReturnDateField)
    EditText returnDateField;

    @BindView(R.id.offerJourneyReturnTimeField)
    EditText returnTimeField;

    @OnClick(R.id.offerJourneyReturnDateButton)
    void selectReturnDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                OfferJourneyOptionalActivity.this.day = dayOfMonth;
                OfferJourneyOptionalActivity.this.month = monthOfYear;
                OfferJourneyOptionalActivity.this.year = year;
                returnDateField.setText(String.format("%d.%d.%d.", day, month + 1, year));
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        dialog.show();
    }

    @OnClick(R.id.offerJourneyReturnTimeButton)
    void selectReturnTime(){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                OfferJourneyOptionalActivity.this.hour = hourOfDay;
                OfferJourneyOptionalActivity.this.minute = minute;
                returnTimeField.setText(String.format("%d:%02dh", hour, minute));
            }
        }, 10, 10, true);

        dialog.show();
    }

    @OnClick(R.id.offerJourneyEnd2)
    void goToEndScreen(){
        if (isDataCorrect()){
            processData();
            startActivity(new Intent(OfferJourneyOptionalActivity.this, OfferJourneyEndActivity.class));
        }
    }

    @BindView(R.id.returnDateError)
    TextView returnDateError;

    private boolean isDataCorrect(){
        if (roundTripSwitch.isChecked()){
            if (returnDateField.getText().toString().trim().isEmpty() || returnTimeField.getText().toString().trim().isEmpty()){
                returnDateError.setText("Datum i vrijeme povratka moraju biti definirani!");
                returnDateError.setVisibility(View.VISIBLE);
                return false;
            } else if (!isDateCorrect()){
                returnDateError.setText("Datum i vrijeme povratka moraju biti kasniji od vremena polaska!");
                returnDateError.setVisibility(View.VISIBLE);
                return false;
            }
        }

        return true;
    }

    private boolean isDateCorrect(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        cal.setTime(journey.getStartingDate());
        String[] time = journey.getStartingTime().substring(0, journey.getStartingTime().length() - 1).split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        Date startingDate = cal.getTime();

        cal.set(year, month, day, hour, minute);
        Date chosenDate = cal.getTime();

        return chosenDate.compareTo(startingDate) >= 0;
    }

    @BindView(R.id.offerJourneyPassingPlacesField)
    EditText passingPlacesField;

    @BindView(R.id.offerJourneyCommentField)
    EditText commentField;

    private void processData(){
        if (roundTripSwitch.isChecked()) {
            journey.setRoundTrip(true);
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.set(year, month, day, hour, minute);
            journey.setReturnDate(cal.getTime());
            journey.setReturnTime(returnTimeField.getText().toString().trim());
        }

        String passingPlaces = passingPlacesField.getText().toString().trim();
        if (!passingPlaces.isEmpty()){
            String[] places = passingPlaces.split("\n");
            for (String place : places){
                journey.addPassingPlace(place.trim());
            }
        }

        String comment = commentField.getText().toString().trim();
        if (!comment.isEmpty()){
            journey.setComment(comment);
        }

        ((MyApplication) getApplication()).setJourney(journey);
    }

}
