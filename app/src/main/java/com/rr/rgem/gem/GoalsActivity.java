package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rr.rgem.gem.answers.GoalsAnswers;
import com.rr.rgem.gem.controllers.common.Factory;
import com.rr.rgem.gem.models.Goal;
import com.rr.rgem.gem.models.Transaction;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jacob on 2016/09/28.
 */

public class GoalsActivity extends ApplicationActivity {

    private GEMNavigation navigation;
    private LinearLayout goalScreen;
    private LinearLayout contentLayout;
    private GoalsAnswers controller;
    private ImageView currentImage;
    public List<Goal> goals;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new GEMNavigation(this);
        Utils.toast(this,getString(R.string.startingGoalsActivity));
        goalScreen = (LinearLayout) navigation.addLayout(R.layout.goal_carousel);
        contentLayout = (LinearLayout) goalScreen.findViewById(R.id.carousel);
        this.controller = Factory.createGoalsCarousel(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null)
        {
            if(extras.containsKey("responses")) {
                HashMap<String, String> responses = (HashMap<String, String>) extras.get("responses");
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;

                try {
                    date = dateFormat.parse(responses.get("goalDate"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                GregorianCalendar endDate = new GregorianCalendar();
                endDate.setTime(date);
                Goal goal = new Goal (responses.get("goalName"), new BigDecimal(responses.get("goalAmount")), new GregorianCalendar().getTime(), endDate.getTime());
                goal.setImageName(responses.get("goalImage"));
                goals.add(goal);
            }
            else if (extras.containsKey("transaction")) {
                HashMap<String, String> transactionInformation = (HashMap<String, String>) extras.get("transaction");

                int amount = Integer.parseInt(transactionInformation.get("amount"));
                if(transactionInformation.get("savingsType").equals("withdraw"))
                    amount *= -1;
                System.out.println(getString(R.string.AMOUNT) + amount);
                Transaction transaction = new Transaction(new GregorianCalendar().getTime(), new BigDecimal(amount));
                goals.get(getGoalIndex(transactionInformation.get("goal"))).addTransaction(transaction);
            }
        }

        if (goals == null || goals.size() == 0) {
            TextView noGoals = new TextView(this);
            noGoals.setText(R.string.goals_not_yet);
            noGoals.setTextSize(20);
            contentLayout.addView(noGoals);
        } else {
            for (Goal goal : goals) {
                addGoalCard(goal);
            }
        }

        Gson g = new Gson();
        String j = g.toJson(goals);
        this.controller.save(j);
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
        if (id == R.id.action_dooit) {
            Intent intent = new Intent(GoalsActivity.this, GoalActivity.class);
            GoalsActivity.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setGoals(List<Goal> goals)
    {
        this.goals = goals;
    }

    private void addGoalCard(final Goal goal)
    {
        View card = LayoutInflater.from(contentLayout.getContext()).inflate(R.layout.goal_card, null);
        contentLayout.addView(card);
        TextView title = (TextView) card.findViewById(R.id.title);
        TextView savings = (TextView) card.findViewById(R.id.savings);
        ImageView editGoal = (ImageView) card.findViewById(R.id.editGoal);
        ImageView goalImage = (ImageView) card.findViewById(R.id.goalImage);

        File image = Utils.getFileFromName("imageDir", goal.getImageName(), getApplicationContext());
        if (Utils.fileExists(image))
        {
            try {
                Bitmap photo = BitmapFactory.decodeStream(new FileInputStream(image));
                goalImage.setImageBitmap(photo);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        title.setText(goal.getName());
        savings.setText("Rp " + Utils.formatNumber(goal.getTotalSaved().longValue()));
        editGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalsActivity.this, SavingsActivity.class);
                intent.putExtra("goal", goal.getName());
                GoalsActivity.this.startActivity(intent);
            }
        });

        ProgressBar averageSavings = (ProgressBar) card.findViewById(R.id.averageSavings);
        ProgressBar weeklySavings = (ProgressBar) card.findViewById(R.id.weeklySavings);

        averageSavings.setMax(goal.getWeeklySavingsGoal().intValue());
        weeklySavings.setMax(goal.getWeeklySavingsGoal().intValue());
        averageSavings.setProgress(goal.getAverageWeeklySavings().intValue());
        weeklySavings.setProgress(goal.getLastWeekSavings().intValue());
    }

    public int getGoalIndex(String name) {
        int index = 0;
        for(Goal goal : goals) {
            if(goal.getName().equals(name))
                return index;
            ++index;
        }
        return -1;
    }

    public void removeGoal(int index) {
        goals.remove(index);
    }
}
