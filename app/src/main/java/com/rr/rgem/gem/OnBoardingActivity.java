package com.rr.rgem.gem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rr.rgem.gem.controllers.JSONConversation;
import com.rr.rgem.gem.models.ConvoCallback;
import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.ImageUploadDialog;
import com.rr.rgem.gem.views.Message;
import com.rr.rgem.gem.views.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by sjj98 on 9/19/2016.
 */
public class OnBoardingActivity extends ApplicationActivity {

    GEMNavigation navigation;
    RelativeLayout contentLayout;
    LinearLayout coachScreen;

    LeftRightConversation coachView;
    //JSONCoach_Onboarding coachController;
    JSONConversation coachController;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = new GEMNavigation(this);
        Utils.toast(this,"starting OnBoarding activity");
        coachScreen = (LinearLayout) navigation.addLayout(R.layout.conversational_layout);
        contentLayout = (RelativeLayout) coachScreen.findViewById(R.id.container);
        coachView = new LeftRightConversation(contentLayout);
        Message start = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
        start.setTitle("Lets Get Started!!!");
        coachView.addFreeFormPlain(start);

        coachController = new JSONConversation(this,R.raw.onboarding);
        Map<String, ConvoCallback> callbacks =  coachController.getFnMap();
        callbacks.put("checkPasswordMatch", new ConvoCallback() {
            @Override
            public String callback(Map<String, String> vars, Map<String, String> responses) {
                if (responses.get("passwordOne").equals(responses.get("passwordTwo"))) {
                    vars.put("password", responses.get("passwordOne"));
                    return "infoOnboardingEnd";
                } else {
                    return "validationFailPasswordMatch";
                }
            }
        });
        coachController.setDoneCallback(new ConvoCallback() {
            @Override
            public String callback(Map<String, String> vars, Map<String, String> responses) {

                Message summary = new Message(1, "2016", true, Message.ResponseType.FreeForm, null);
                summary.setTitle("SUMMARY:");
                coachView.addFreeFormPlain(summary);

                for (String key: responses.keySet()) {
                    Message msg = new Message(0, "2016", true, Message.ResponseType.FreeForm, null);
                    msg.setTitle(key + ": " + responses.get(key));
                    coachView.addFreeFormPlain(msg);
                }

                Button button1 = new Button(OnBoardingActivity.this);
                button1.setText(R.string.challenge_next);
                coachView.addRightView(button1, "name");
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: add movement to next activity
                        //Intent intent = new Intent(OnBoardingActivity.this, ChallengeActivity.class);
                        //startActivity(intent);
                        Utils.toast(OnBoardingActivity.this,"Conversation done");
                        //GoalActivity.goToChallenges();
                        //conversationActivity conActivity;
                        //conversationActivity.

                        final Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));
                        persisted.setRegistered(true);
                        Intent intent = new Intent(OnBoardingActivity.this,GoalActivity.class);
                        startActivity(intent);
                        OnBoardingActivity.this.finish();
                    }
                });
                return null;

            }

        });

        coachController.sendChallenges(this,coachView,null);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                ImageUploadDialog dialog = new ImageUploadDialog();
                dialog.show(manager, "dialog");
            }
        };

        Message message = new Message(1, "2016", true, Message.ResponseType.ImageUpload, null);
        message.setTitle("Set a profile picture: ");
        currentImage = coachView.addImageUploadQuestion(message, listener);
        currentImageName = "profile.jpg";

    }

    public  void goToNext() {
        final Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));

        Button button1 = new Button(contentLayout.getContext());
        button1.setText(R.string.challenge_next);
        coachView.addRightView(button1, "name");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                persisted.setRegistered(true);
                Intent intent = new Intent(OnBoardingActivity.this,GoalsActivity.class);
                startActivity(intent);

            }
        });
    }



}
