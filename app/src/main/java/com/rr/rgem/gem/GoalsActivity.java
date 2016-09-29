package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.models.Goal;
import com.rr.rgem.gem.models.Transaction;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jacob on 2016/09/28.
 */

public class GoalsActivity extends AppCompatActivity {

    private GEMNavigation navigation;
    private LinearLayout goalScreen;
    private LinearLayout contentLayout;
    private ArrayList<Goal> goals;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new GEMNavigation(this);
        Utils.toast(this,"starting Goals activity");
        goalScreen = (LinearLayout) navigation.addLayout(R.layout.goal_carousel);
        contentLayout = (LinearLayout) goalScreen.findViewById(R.id.carousel);
        goals = generateGoals();
        if (goals == null) {
            TextView noGoals = new TextView(this);
            noGoals.setText(R.string.goals_not_yet);
            noGoals.setTextSize(20);
            noGoals.setGravity(Gravity.CENTER);
            contentLayout.addView(noGoals);
        } else {
            for (Goal goal : goals) {
                addGoalCard(goal);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem actionButton = menu.getItem(1);
        actionButton.setIcon(R.drawable.ic_action_name);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bimbingbung) {
            Intent intent = new Intent(GoalsActivity.this, GoalActivity.class);
            GoalsActivity.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void addGoalCard(Goal goal)
    {
        View card = LayoutInflater.from(contentLayout.getContext()).inflate(R.layout.goal_card, null);
        contentLayout.addView(card);
        TextView title = (TextView) card.findViewById(R.id.title);
        TextView savings = (TextView) card.findViewById(R.id.savings);
        ImageView editGoal = (ImageView) card.findViewById(R.id.editGoal);
        title.setText(goal.getName());
        savings.setText("Rp " + Utils.formatNumber(goal.getTotalSaved().longValue()));
        editGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalsActivity.this, SavingsActivity.class);
                GoalsActivity.this.startActivity(intent);
            }
        });

        ProgressBar averageSavings = (ProgressBar) card.findViewById(R.id.averageSavings);
        ProgressBar weeklySavings = (ProgressBar) card.findViewById(R.id.weeklySavings);
        averageSavings.setMax(100);
        weeklySavings.setMax(100);
        averageSavings.setProgress(33);
        weeklySavings.setProgress(66);
    }

    private static ArrayList<Goal> generateGoals() {
        ArrayList <Goal> goals = new ArrayList<Goal>();
        Goal goal;

        goal = new Goal("Teddy Bear", new BigDecimal(5000), new Date(2000, 5, 2), new Date(2000, 6, 2));
        goal.addTransaction(new Transaction(new Date(2000, 5, 28), new BigDecimal(500)));
        //System.out.println("TRANSACTION AMOUNT " + goal);
        goals.add(goal);

        goal = new Goal("Shoes", new BigDecimal(10000), new Date(2000, 5, 2), new Date(2000, 6, 2));
        goal.addTransaction(new Transaction(new Date(2000, 5, 28), new BigDecimal(2500)));
        goals.add(goal);

        goal = new Goal("Tanzanite Ring", new BigDecimal(999999), new Date(2000, 5, 2), new Date(2000, 6, 2));
        goal.addTransaction(new Transaction(new Date(2000, 5, 28), new BigDecimal(50000)));
        goals.add(goal);

        return goals;
    }
}
