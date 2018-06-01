package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 18.12.2017..
 */

public class UserMainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.userOfferJourneyButton)
    void offerJourney(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new OfferJourneyObligatoryFragment()).commit();
    }

    @OnClick(R.id.userFindJourneyButton)
    void findJourney(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack("").replace(R.id.user_main_layout, new FindJourneyFragment()).commit();
    }
}
