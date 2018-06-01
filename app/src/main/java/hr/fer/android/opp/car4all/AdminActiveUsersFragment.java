package hr.fer.android.opp.car4all;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class AdminActiveUsersFragment extends Fragment {

    private Administrator admin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_active_users, container, false);
        ButterKnife.bind(this, view);

        admin = (Administrator) ((MyApplication) getActivity().getApplication()).getPerson();
        setupData();

        return view;
    }

    @BindView(R.id.adminActiveUsersErrorMessage)
    TextView errorMessage;

    @BindView(R.id.adminActiveUsersList)
    ListView activeUsersList;

    private void setupData(){
        errorMessage.setVisibility(View.GONE);

        try{
            List<User> activeUsers = admin.getActiveUsers();

            if (activeUsers.isEmpty()){
                errorMessage.setVisibility(View.VISIBLE);
            }

            ArrayAdapter<User> adapter = new AdminChangeAuthorityArrayAdapter(getActivity(), activeUsers);
            activeUsersList.setAdapter(adapter);

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

}
