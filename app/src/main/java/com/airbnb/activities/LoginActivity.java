package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.UserLogInRequestDto;
import com.airbnb.shared.dto.user.UserLogInResponseDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private ProgressDialog progressDialog ;

    @Bind(R.id.username) EditText _username;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        progressDialog =  new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    @Override
    public void onPause(){
        super.onPause();
        progressDialog.dismiss();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _username.getText().toString();
        String password = _passwordText.getText().toString();

       // onLoginSuccess();
        new Login().execute(username,password);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);
    }

    public void onLoginSuccess(User user) {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MainLoggedInActivity.class);
        Bundle bundle = new Bundle();

        String user_json = new Gson().toJson(user);
        bundle.putString("user", user_json);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _username.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() ) {
            _username.setError("enter a valid username");
            valid = false;
        } else {
            _username.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private class Login extends AsyncTask<String,Void,User> {

        private RestTemplate restTemplate =  new RestApi().getRestTemplate();

        @Override
        protected User doInBackground(String... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/login";
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserLogInRequestDto userLogInRequestDto = new UserLogInRequestDto();
            userLogInRequestDto.setUsername(params[0]);
            userLogInRequestDto.setPassword(params[1]);

            User result = null;
            try {
                HttpEntity<UserLogInRequestDto> request = new HttpEntity<>(userLogInRequestDto);
                result = restTemplate.postForObject(uri,request, User.class);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final User user) {
            if(user != null) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onLoginSuccess(user);
                                progressDialog.dismiss();
                            }
                        }, 1000);
            }else{
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onLoginFailed();
                                progressDialog.dismiss();
                            }
                        }, 1000);
            }

        }

    }
}
