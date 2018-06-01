package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 18.12.2017..
 */

public class OfferJourneyEndFragment extends Fragment {

    private Journey journey;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_journey_end, container, false);
        ButterKnife.bind(this, view);

        journey = ((MyApplication) getActivity().getApplication()).getJourney();
        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();

        return view;
    }

    @OnClick(R.id.quitJourneyOffer)
    void quitJourneyOffer(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (user instanceof Moderator) {
            fragmentManager.beginTransaction().replace(R.id.moderator_main_layout, new ModeratorNewUsersFragment()).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.user_main_layout, new UserMainFragment()).commit();
        }
    }

    @OnClick(R.id.finalizeJourneyOffer)
    void finalizeJourneyOffer(){
        try {
            user.offerJourney(journey);
            Toast.makeText(getActivity(), "Putovanje je ponuđeno!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (user instanceof Moderator) {
            fragmentManager.beginTransaction().replace(R.id.moderator_main_layout, new ModeratorNewUsersFragment()).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.user_main_layout, new UserMainFragment()).commit();
        }
    }
}
