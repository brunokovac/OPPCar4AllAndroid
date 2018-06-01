package hr.fer.android.opp.car4all;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import hr.fer.android.opp.car4all.dao.DAOProvider;
import hr.fer.android.opp.car4all.models.Administrator;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.Review;

/**
 * Created by Bruno on 4.1.2018..
 */

public class ModeratorReviewArrayAdapter extends ArrayAdapter<Review> {

    private Context context;
    private List<Review> reviews;

    public ModeratorReviewArrayAdapter(Context context, List<Review> reviews){
        super(context, -1, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.moderator_review_element, parent, false);

        ImageView profilePicture = (ImageView) rowView.findViewById(R.id.moderatorReviewElementProfilePicture);
        TextView userField = (TextView) rowView.findViewById(R.id.moderatorReviewElementUserField);
        RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.moderatorReviewElementRatingBar);
        TextView commentField = (TextView) rowView.findViewById(R.id.moderatorReviewElementCommentField);
        TextView reviewerField = (TextView) rowView.findViewById(R.id.moderatorReviewElementReviewerField);

        final Review review = reviews.get(position);

        Glide.with(context).load(Uri.parse(review.getUser().getProfilePicture()))
                .apply(new RequestOptions().placeholder(R.drawable.image_not_found2).error(R.drawable.image_not_found2))
                .into(profilePicture);
        userField.setText(review.getUser().getUsername());

        ratingBar.setRating(review.getGrade());
        commentField.setText(review.getComment());
        reviewerField.setText(review.getReviewer().getUsername());

        Person person = ((MyApplication) ((AppCompatActivity) context).getApplication()).getPerson();
        final Moderator moderator = person instanceof Administrator ? ((Administrator) person).getModerator() : (Moderator) person;

        ((Button) rowView.findViewById(R.id.moderatorReviewElementKeepButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    moderator.checkReview(review);

                    ModeratorReviewArrayAdapter.this.remove(review);
                    ModeratorReviewArrayAdapter.this.notifyDataSetChanged();

                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((Button) rowView.findViewById(R.id.moderatorReviewElementDeleteButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    moderator.removeReview(review);

                    ModeratorReviewArrayAdapter.this.remove(review);
                    ModeratorReviewArrayAdapter.this.notifyDataSetChanged();

                }catch (Exception exc){
                    Toast.makeText(context, "Došlo je do pogreške...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }
}
