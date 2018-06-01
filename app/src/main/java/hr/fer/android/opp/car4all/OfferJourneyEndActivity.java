package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 18.12.2017..
 */

public class OfferJourneyEndActivity extends AppCompatActivity {

    private Journey journey;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_journey_end);

        ButterKnife.bind(this);

        journey = ((MyApplication) getApplication()).getJourney();
        user = (User) ((MyApplication) getApplication()).getPerson();
    }

    @OnClick(R.id.quitJourneyOffer)
    void quitJourneyOffer(){
        if (user instanceof Moderator) {
            startActivity(new Intent(OfferJourneyEndActivity.this, ModeratorHomeActivity.class));
        }else{
            startActivity(new Intent(OfferJourneyEndActivity.this, UserHomeActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
    }

    @OnClick(R.id.finalizeJourneyOffer)
    void finalizeJourneyOffer(){
        try {
            user.offerJourney(journey);
        } catch (Exception e) {
            Toast.makeText(OfferJourneyEndActivity.this, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(OfferJourneyEndActivity.this, UserHomeActivity.class));
    }
}
