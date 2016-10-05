package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rr.rgem.gem.answers.GoalAnswers;
import com.rr.rgem.gem.answers.GoalsAnswers;
import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.controllers.common.Factory;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.models.Goal;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sjj98 on 9/19/2016.
 */
public class GoalActivity extends ApplicationActivity {

    private GEMNavigation navigation;
    private RelativeLayout contentLayout;
    private LinearLayout coachScreen;

    private LeftRightConversation coachView;
    //private JSONConversation coachController;
    private GoalAnswers coachController;
    private ImageView currentImage;
    Goal goal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new GEMNavigation(this);
        Utils.toast(this,"starting Goals activity");
        coachScreen = (LinearLayout) navigation.addLayout(R.layout.conversational_layout);
        contentLayout = (RelativeLayout) coachScreen.findViewById(R.id.container);
        coachView = new LeftRightConversation(contentLayout);
        Message start = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
        start.setTitle("Lets Get Started!!!");

        //coachController = new JSONConversation(this,R.raw.goals);

        coachController = Factory.createGoal(this,coachView);

        coachController.setDoneCallback(new ConvoCallback() {
            @Override
            public String callback(Map<String, String> vars, final Map<String, String> responses) {

                Message summary = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
                summary.setTitle("SUMMARY:");
                coachView.addFreeFormPlain(summary);

                for (String key: responses.keySet()) {
                    Message msg = new Message(0, "2016", true, Message.ResponseType.FreeForm, null);
                    msg.setTitle(key + ": " + responses.get(key));
                    coachView.addFreeFormPlain(msg);
                }

                Button button1 = new Button(contentLayout.getContext());
                button1.setText(R.string.challenge_next);
                coachView.addRightView(button1, "name");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        coachController.save(null);
                        Intent intent = new Intent(GoalActivity.this,GoalsActivity.class);
                        intent.putExtra("responses", (HashMap<String, String>) responses);
                        startActivity(intent);

                    }
                });

                return null;

            }

        });
        coachController.getState().sendChallenges(this,null);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                ImageUploadDialog dialog = new ImageUploadDialog();
                dialog.show(manager, "dialog");
            }
        };

    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}
