package com.nike.dooit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nike.dooit.api.DooitAPIError;
import com.nike.dooit.api.DooitErrorHandler;
import com.nike.dooit.api.managers.AuthenticationManager;
import com.nike.dooit.api.responses.AuthenticationResponse;

import javax.inject.Inject;

import rx.functions.Action1;

public class RootActivity extends AppCompatActivity {

    @Inject
    AuthenticationManager authenticationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        ((DooitApplication)getApplication()).component.inject(this);
        authenticationManager.login("admin", "pass", new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        }).subscribe(new Action1<AuthenticationResponse>() {
            @Override
            public void call(AuthenticationResponse authenticationResponse) {
                System.out.println(authenticationResponse.getUser().getFirst_name());
            }
        });
    }
}
