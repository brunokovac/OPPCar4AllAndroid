package hr.fer.android.opp.car4all;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Message;

/**
 * Created by Bruno on 4.1.2018..
 */

public class MessageArrayAdadpter extends ArrayAdapter<Message> {

    private Context context;
    private List<Message> messages;

    public MessageArrayAdadpter(Context context, List<Message> messages){
        super(context, -1, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.chat_message_element, parent, false);

        TextView contentField = (TextView) rowView.findViewById(R.id.chatMessageItemContentField);
        TextView usernameAndTimeField = (TextView) rowView.findViewById(R.id.chatMessageItemUsernameAndTimeField);

        contentField.setText(messages.get(position).getContent());
        usernameAndTimeField.setText(String.format("%s (%s)", messages.get(position).getSender().getUsername(),
                messages.get(position).getSendingTime()));

        LinearLayout layout = (LinearLayout) rowView.findViewById(R.id.chatMessageItemLayout);
        if (messages.get(position).getSender().getPersonID() ==
                ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson().getPersonID()){
            layout.setBackgroundColor(Color.parseColor("#daf5d5"));
            contentField.setGravity(Gravity.RIGHT);
            usernameAndTimeField.setGravity(Gravity.RIGHT);
        }

        return rowView;
    }
}
