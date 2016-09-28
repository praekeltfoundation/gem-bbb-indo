package com.rr.rgem.gem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.rr.rgem.gem.views.Utils;

/**
 * Created by chris on 9/8/2016.
 */
public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Persisted persisted = new Persisted(getSharedPreferences(Persisted.APP_PREFS,0));
        if (persisted.isRegistered()) {
            Intent intent = new Intent(RegistrationActivity.this, RegistrationCompleteActivity.class);
            RegistrationActivity.this.startActivity(intent);
            RegistrationActivity.this.finish();
            return;
        }
        setContentView(R.layout.user_registration_page);
        final Button button = (Button) findViewById(R.id.buttonRegister);
        button.setOnClickListener(new View.OnClickListener() {
            private boolean isValidMobile(CharSequence phone){
                return android.util.Patterns.PHONE.matcher(phone).matches();
            }
            private void toast(CharSequence text){
                Context context = getApplicationContext();
                Utils.toast(context,text);

            }
            public void onClick(View v) {
                TextView editName = (TextView)findViewById(R.id.editTextName);
                TextView editMobile = (TextView)findViewById(R.id.editTextMobile);
                TextView editPassword = (TextView)findViewById(R.id.editTextPassword);
                if(editName.getText().length() < 4){
                    toast(getString(R.string.invalid_username_length));
                    return;
                }
                if(!isValidMobile(editMobile.getText())){
                    toast(getString(R.string.invalid_mobile_number));
                    return;
                }
                if(editPassword.getText().length() < 8){
                    toast(getString(R.string.invalid_password_length));
                    return;
                }
                toast(getString(R.string.congratulations_registered));
                //TODO: check for log in activity
                persisted.setRegistered(true);
                Intent intent = new Intent(RegistrationActivity.this, RegistrationCompleteActivity.class);
                RegistrationActivity.this.startActivity(intent);
                RegistrationActivity.this.finish();
            }
        });
    }
}
