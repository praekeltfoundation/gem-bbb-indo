package com.rr.rgem.gem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rr.rgem.gem.navigation.GEMNavigation;
import com.rr.rgem.gem.views.LeftRightConversation;
import com.rr.rgem.gem.views.Message;

import java.util.Calendar;

public class MainActivity extends ApplicationActivity{

    private static String APP_PREFS = "shared_bimbingbung";
    private GEMNavigation navigation;
    private RelativeLayout contentLayout;
    private LinearLayout coachScreen;
    private LeftRightConversation conversation ;
    private LeftRightConversation coachView;

    //Reminder Challenge function
     void reminderChallenge(){

         AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
         Intent intent= new Intent(MainActivity.this,Notification_receiver_C.class);
         PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

         Calendar calendar= Calendar.getInstance();
         Calendar now = Calendar.getInstance();

         calendar.set(Calendar.DAY_OF_WEEK,6);
         calendar.set(Calendar.HOUR_OF_DAY,16);
         calendar.set(Calendar.MINUTE,44);
         calendar.set(Calendar.SECOND,1);
         calendar.set(Calendar.AM_PM, Calendar.PM);

         System.out.println("CALENDARRRRRRRRRR_C");
         System.out.println(calendar);
         System.out.println("NOW_C");
         System.out.println(now);

         if(calendar.before(now)){
             System.out.println("CANCELEDDDD_C");
             alarmManager.cancel(pendingIntent);
         }
         else
         {
             System.out.println("ALERRTTTTT_C");
             alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7,pendingIntent);
         }
    }

    //Reminder Progress Tracking function
     void reminderProgressTracking(){

         AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
         Intent intent= new Intent(MainActivity.this,Notification_receiver_TP.class);
         PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
         Calendar calendar= Calendar.getInstance();
         Calendar now = Calendar.getInstance();
         calendar.set(Calendar.DAY_OF_WEEK,6);
         calendar.set(Calendar.HOUR_OF_DAY,16);
         calendar.set(Calendar.MINUTE,45);
         calendar.set(Calendar.SECOND,1);
         calendar.set(Calendar.AM_PM, Calendar.PM);

         System.out.println("CALENDARRRRRRRRRR_TP");
         System.out.println(calendar);
         System.out.println("NOW_TP");
         System.out.println(now);

         if(calendar.before(now)){
             System.out.println("CANCELEDDDD_TP");
             alarmManager.cancel(pendingIntent);
         }
         else
         {
             System.out.println("ALERRTTTTT_TP");
             alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY*7,pendingIntent);
         }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //LEAVE THESE COMMENTED**********************************
        //Call reminderChallenge
        //reminderChallenge();
        //Call reminderProgressTrcaking-Must add condition to check when the user was not able to save(Calling here for testing purposes)
        //reminderProgressTracking();
        //LEAVE THESE COMMENTED**********************************

        navigation = new GEMNavigation(this);
        coachScreen = (LinearLayout) navigation.addLayout(R.layout.conversational_layout);
        contentLayout = (RelativeLayout) coachScreen.findViewById(R.id.container);
        conversation = (LeftRightConversation) new LeftRightConversation((RelativeLayout) contentLayout);

        Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));
        if(!persisted.isRegistered()){
            //Intent intent = new Intent(this, RegistrationActivity.class);
            //startActivity(intent);
            Intent intent = new Intent(this, OnBoardingActivity.class);
            startActivity(intent);
            this.finish();
        }else{


            coachView = new LeftRightConversation(contentLayout);
            Message message = new Message(1, "2012", true, Message.ResponseType.FreeForm, null);
            message.setTitle("Hello you have registered and set your goals;swipe right or left to navigate further");
            coachView.addFreeFormPlain(message);

        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
