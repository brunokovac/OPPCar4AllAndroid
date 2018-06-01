package hr.fer.android.opp.car4all;

import android.content.Context;
import android.graphics.PorterDuff;
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
import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.PersonType;
import hr.fer.android.opp.car4all.models.User;

/**
 * Created by Bruno on 4.1.2018..
 */

public class AdminChangeAuthorityArrayAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;

    public AdminChangeAuthorityArrayAdapter(Context context, List<User> users){
        super(context, -1, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.admin_change_authority_element, parent, false);

        ImageView profilePicture = (ImageView) rowView.findViewById(R.id.adminChangeAuthorityElementProfilePicture);
        TextView usernameField = (TextView) rowView.findViewById(R.id.adminChangeAuthorityElementUsernameField);
        final TextView typeField = (TextView) rowView.findViewById(R.id.adminChangeAuthorityTypeField);

        final User user = users.get(position);

        Glide.with(context).load(Uri.parse(user.getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);
        usernameField.setText(user.getUsername());
        typeField.setText(user instanceof Moderator ? "MODERATOR" : "KORISNIK");

        final Administrator admin = (Administrator) ((MyApplication) (((AppCompatActivity)context).getApplication())).getPerson();

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) (((AppCompatActivity) context).getApplication())).setPersonBeingChecked(user);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack("").replace(R.id.admin_main_layout, new UserInfoFragment()).commit();
            }
        });

        ((Button) rowView.findViewById(R.id.adminChangeAuthorityDeleteButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    admin.removeUser(user);
                    Toast.makeText(context, "Korisnik je izbačen iz zajednice!", Toast.LENGTH_SHORT).show();

                    AdminChangeAuthorityArrayAdapter.this.remove(AdminChangeAuthorityArrayAdapter.this.getItem(position));
                    AdminChangeAuthorityArrayAdapter.this.notifyDataSetChanged();

                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.adminChangeAuthorityChangeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PersonType newType = null;
                    if (user.getType() == PersonType.USER){
                        newType = PersonType.MODERATOR;
                        typeField.setText("MODERATOR");
                    }else{
                        newType = PersonType.USER;
                        typeField.setText("KORISNIK");
                        typeField.animate();
                    }

                    admin.changeAuthority(user, newType);
                    Toast.makeText(context, "Ovlasti su promijenjene!", Toast.LENGTH_SHORT).show();

                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }
}
