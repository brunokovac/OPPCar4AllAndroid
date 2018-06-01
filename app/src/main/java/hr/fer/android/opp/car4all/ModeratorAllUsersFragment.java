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
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

public class ModeratorAllUsersFragment extends Fragment {

    private Moderator moderator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moderator_all_users, container, false);
        ButterKnife.bind(this, view);

        Person person = ((MyApplication) getActivity().getApplication()).getPerson();
        moderator = person instanceof Administrator ? ((Administrator) person).getModerator() : (Moderator) person;
        setupData();

        return view;
    }

    @BindView(R.id.moderatorAllUsersErrorMessage)
    TextView errorMessage;

    @BindView(R.id.moderatorAllUsersList)
    ListView allUsersList;

    private void setupData(){
        errorMessage.setVisibility(View.GONE);

        try{
            List<User> allUsers = moderator.getAllNormalUsers();

            if (allUsers.isEmpty()){
                errorMessage.setVisibility(View.VISIBLE);
            }

            ArrayAdapter<User> adapter = new AllUsersArrayAdapter(getActivity(), allUsers);
            allUsersList.setAdapter(adapter);

        }catch (Exception exc){
            Log.d("greska", exc.getMessage());
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

}
