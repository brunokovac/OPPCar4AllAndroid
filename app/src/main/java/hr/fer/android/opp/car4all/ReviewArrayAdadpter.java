package hr.fer.android.opp.car4all;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Review;

/**
 * Created by Bruno on 4.1.2018..
 */

public class ReviewArrayAdadpter extends ArrayAdapter<Review> {

    private Context context;
    private List<Review> reviews;

    public ReviewArrayAdadpter(Context context, List<Review> reviews){
        super(context, -1, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_review_element, parent, false);

        RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.reviewItemRatingBar);
        TextView commentField = (TextView) rowView.findViewById(R.id.reviewItemCommentField);
        TextView usernameField = (TextView) rowView.findViewById(R.id.reviewItemUsernameField);

        ratingBar.setRating(reviews.get(position).getGrade());
        commentField.setText(reviews.get(position).getComment());
        usernameField.setText(reviews.get(position).getReviewer().getUsername());

        return rowView;
    }
}
