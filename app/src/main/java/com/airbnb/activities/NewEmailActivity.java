package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.airbnb.Utils.Util;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.message.NewMessageDto;
import com.airbnb.shared.dto.user.UserRegisterRequestDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.ObjectInput;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewEmailActivity extends AppCompatActivity {

    private static final String TAG = "NewEmailActivity";
    private ProgressDialog progressDialog ;
    @Bind(R.id.email_from)
    EditText _email_from;
    @Bind(R.id.email_to)
    EditText _email_to;
    @Bind(R.id.subject) EditText _subject;
    @Bind(R.id.body) EditText _body;
    @Bind(R.id.btn_send) Button _send;

    NewMessageDto newMessageDto;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_email);
        ButterKnife.bind(this);
        extras = getIntent().getExtras();
        _send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessageDto = new NewMessageDto();
                newMessageDto.setFromUser(_email_from.getText().toString());
                newMessageDto.setToUser(_email_to.getText().toString());
                newMessageDto.setMessageText(_body.getText().toString());

                new SendMailAsyncTask().execute();
            }
        });
    }


    private class SendMailAsyncTask extends AsyncTask<Void,Void,Void> {

        private RestTemplate restTemplate = new RestApi().getRestTemplate();

        @Override
        protected Void doInBackground(Void... params) {
            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/newMessage";
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                HttpEntity<NewMessageDto> request = new HttpEntity<>(newMessageDto);
                restTemplate.postForObject(uri,request, Object.class);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void p) {
            Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
            User host_user = new Gson().fromJson(extras.get("host_user").toString(), User.class);
            String user = new Gson().toJson(host_user);
            extras.putString("user",user);
            intent.putExtras(extras);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }
    }

}
