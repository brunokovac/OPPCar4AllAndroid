package hr.fer.android.opp.car4all;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.JourneyRequest;
import hr.fer.android.opp.car4all.models.User;

public class RequestsFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        setupData();

        return view;
    }

    @BindView(R.id.requestsList)
    ListView requestsList;

    @BindView(R.id.requestsErrorMessage)
    TextView errorMessage;

    private void setupData(){
        errorMessage.setVisibility(View.GONE);

        try {
            List<JourneyRequest> requests = user.getJourneyRequests();

            if (requests.isEmpty()) {
                errorMessage.setVisibility(View.VISIBLE);
            }

            ArrayAdapter<JourneyRequest> adapter = new RequestArrayAdadpter(getActivity(), requests);
            requestsList.setAdapter(adapter);
        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }


}
