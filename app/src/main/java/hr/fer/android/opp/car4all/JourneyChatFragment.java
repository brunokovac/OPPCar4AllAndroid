package hr.fer.android.opp.car4all;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Message;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.User;

public class JourneyChatFragment extends Fragment {

    private User user;
    private Journey journey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journey_chat, container, false);
        ButterKnife.bind(this, view);

        user = (User) ((MyApplication) getActivity().getApplication()).getPerson();
        journey = ((MyApplication) getActivity().getApplication()).getJourney();

        updateMessages();

        return view;
    }

    @BindView(R.id.journeyDetailsChatNewMessage)
    EditText newMessageField;

    @OnClick(R.id.journeyDetailsNewMessageButton)
    public void addNewMessage(){
        if (newMessageField.getText().toString().trim().isEmpty()){
            return;
        }

        try{
            Message message = new Message();
            message.setSender(user);
            message.setContent(newMessageField.getText().toString().trim());

            newMessageField.setText("");

            journey.getChat().addMessage(message);
            updateMessages();
        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške..." + exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @BindView(R.id.journeyDetailsChatMessagesList)
    ListView chatMessagesList;

    private void updateMessages(){
        try{
            List<Message> messages = journey.getChat().getMessages();
            if (!messages.isEmpty()){
                journey.getChat().addSeenByUser(user.getPersonID(), false);
            }

            ArrayAdapter<Message> adapter = new MessageArrayAdadpter(getActivity(), messages);
            chatMessagesList.setAdapter(adapter);

            if (user instanceof Moderator) {
                ((ModeratorHomeActivity) getActivity()).checkForNewMessages();
            }else{
                ((UserHomeActivity) getActivity()).checkForNewMessages();
            }

        }catch (Exception exc){
            Toast.makeText(getActivity(), "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.journeyDetailsChatRefreshButton)
    void refreshChat(){
        updateMessages();
    }

}
