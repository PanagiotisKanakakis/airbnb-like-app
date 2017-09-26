package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.RoleDto;
import com.airbnb.shared.dto.user.UserRegisterRequestDto;
import com.airbnb.shared.dto.user.UserRegisterResponseDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private ProgressDialog progressDialog ;
    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_surname) EditText _surnameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.input_username) EditText _username;
    @Bind(R.id.tenant) CheckBox _tenant;
    @Bind(R.id.host) CheckBox _host;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String surname = _surnameText.getText().toString();
        String username = _username.getText().toString();
        Boolean host = _host.isChecked();
        Boolean tenant = _tenant.isChecked();

        new SignUp().execute(name,surname,email,mobile,password,username,host,tenant);
    }


    public void onSignupSuccess(User user) {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);


        Intent intent = new Intent(getApplicationContext(), MainLoggedInActivity.class);
        Bundle bundle = new Bundle();

        String user_json = new Gson().toJson(user);
        bundle.putString("user", user_json);
        intent.putExtras(bundle);
        startActivity(intent);

        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String surname = _surnameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String username = _username.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (surname.isEmpty() || surname.length() < 3) {
            _surnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _surnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()!=10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if (username.isEmpty()) {
            _username.setError("Enter Valid Username");
            valid = false;
        } else {
            _username.setError(null);
        }

        return valid;
    }

    private class SignUp extends AsyncTask<Object,Void,User>{

        private RestTemplate restTemplate = new RestApi().getRestTemplate();

        @Override
        protected User doInBackground(Object... params) {
            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/register";
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserRegisterRequestDto userRegisterRequestDto = new UserRegisterRequestDto();
            userRegisterRequestDto.setName(params[0].toString());
            userRegisterRequestDto.setSurname(params[1].toString());
            userRegisterRequestDto.setEmail(params[2].toString());
            userRegisterRequestDto.setPhoneNumber(params[3].toString());
            userRegisterRequestDto.setPassword(params[4].toString());
            userRegisterRequestDto.setUsername(params[5].toString());
            List<RoleDto> roles = new ArrayList<>();

            if (Boolean.getBoolean(params[6].toString()))
                roles.add(RoleDto.HOST);
            if (Boolean.getBoolean(params[7].toString()))
                roles.add(RoleDto.TENANT);
            userRegisterRequestDto.setRoleDtos(roles);

            User result = null;
            try {
                HttpEntity<UserRegisterRequestDto> request = new HttpEntity<>(userRegisterRequestDto);
                result = restTemplate.postForObject(uri,request, User.class);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final User user) {
            if(user != null){
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onSignupSuccess(user);
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }
            else{
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                onSignupFailed();
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }

        }
    }
}