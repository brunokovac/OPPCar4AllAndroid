package hr.fer.android.opp.car4all;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import hr.fer.android.opp.car4all.dao.SQLConnectionProvider;

public class StartActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ButterKnife.bind(this);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);

        new WaitingThread().start();
    }

    class WaitingThread extends Thread {

        @Override
        public void run() {
            ConnectionThread t = new ConnectionThread();
            t.start();

            try {
                t.join();
            } catch (InterruptedException e) {
            }

            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        }
    }


}
