package hr.fer.android.opp.car4all;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.List;

import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 4.1.2018..
 */

public class NewUsersArrayAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;

    public NewUsersArrayAdapter(Context context, List<User> users){
        super(context, -1, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.new_user_element, parent, false);

        TextView fullNameField = (TextView) rowView.findViewById(R.id.newUserElementFullNameField);
        TextView oibField = (TextView) rowView.findViewById(R.id.newUserElementOIBField);
        TextView usernameField = (TextView) rowView.findViewById(R.id.newUserElementUsernameField);

        final User user = users.get(position);

        fullNameField.setText(String.format("%s %s", user.getName(), user.getSurname()));
        oibField.setText(user.getOIB());
        usernameField.setText(user.getUsername());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setPersonBeingChecked(user);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                if (person instanceof Administrator) {
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, new ModeratorCheckUserFragment()).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new ModeratorCheckUserFragment()).commit();
                }
            }
        });

        Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
        final Moderator moderator = person instanceof Administrator ? ((Administrator) person).getModerator() : (Moderator) person;

        ((Button) rowView.findViewById(R.id.newUserElementDeclineButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    moderator.declineUser(user);
                    sendEmail(user.getEmail(), false);

                    NewUsersArrayAdapter.this.remove(NewUsersArrayAdapter.this.getItem(position));
                    NewUsersArrayAdapter.this.notifyDataSetChanged();

                }catch(Exception e){
                    Log.d("greska", e.getMessage());
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.newUserElementAcceptButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    moderator.acceptUser(user);
                    sendEmail(user.getEmail(), true);

                    NewUsersArrayAdapter.this.remove(NewUsersArrayAdapter.this.getItem(position));
                    NewUsersArrayAdapter.this.notifyDataSetChanged();

                }catch(Exception e){
                    Log.d("greska", e.getMessage());
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }

    private void sendEmail(String recipientEmail, boolean accepted){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",recipientEmail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Car4all - Registracija");

        String message = String.format("Poštovani,%n%nVaš zahtjev za pristupom u Car4All zajednicu je %s" +
                "%n%nCar4All moderator", accepted ? "prihvaćen" : "odbijen");
        intent.putExtra(Intent.EXTRA_TEXT, message);

        context.startActivity(Intent.createChooser(intent, "Send report"));
    }
}
