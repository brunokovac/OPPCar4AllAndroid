package hr.fer.android.opp.car4all;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

import java.util.List;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 4.1.2018..
 */

public class AllUsersArrayAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;

    public AllUsersArrayAdapter(Context context, List<User> users){
        super(context, -1, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.all_users_element, parent, false);

        ImageView profilePicture = (ImageView) rowView.findViewById(R.id.allUsersElementProfilePicture);
        TextView usernameField = (TextView) rowView.findViewById(R.id.allUsersElementUsernameField);

        final User user = users.get(position);

        Glide.with(context).load(Uri.parse(user.getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);
        usernameField.setText(user.getUsername());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity)context).getApplication())).setPersonBeingChecked(user);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

                Person person = ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();
                if (person instanceof Moderator){
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.moderator_main_layout, new UserInfoFragment()).commit();
                }else{
                    fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, new UserInfoFragment()).commit();
                }

            }
        });

        final Button button = (Button) rowView.findViewById(R.id.allUsersElementButton);
        if (user.isDeleted()){
            button.setText("VRATI");
        }else{
            button.setText("IZBACI");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (button.getText().equals("VRATI")) {
                        user.setDeleted(false);
                        DAOProvider.getDao().editPerson(user);
                        button.setText("IZBACI");

                        Toast.makeText(context, "Korisnik je vraćen u zajednicu!", Toast.LENGTH_SHORT).show();

                    } else {
                        user.setDeleted(true);
                        DAOProvider.getDao().editPerson(user);
                        button.setText("VRATI");

                        Toast.makeText(context, "Korisnik je izbačen iz zajednice!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }
}
