package com.airbnb.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.images.MailModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Comment;
import com.airbnb.shared.dto.entity.Message;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.UserUtilsDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by panagiotis on 16/9/2017.
 */

public class InboxActivity extends AppCompatActivity {

    private static final String TAG = "InboxActivity";

    private User active_user;
    private Bundle bundle  = new Bundle();

    @Bind(R.id.profile) TextView _profile;
    @Bind(R.id.id_text) TextView _msg;
    @Bind(R.id.listing) TextView _listing;
    @Bind(R.id.tenant) TextView _tenant;
    @Bind(R.id.userMails) ListView userMails;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        ButterKnife.bind(this);


        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
            }
        }

        _profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _listing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListingActivity.class);
                bundle.putString("mode","host");
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _tenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainLoggedInActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        new GetInboxMessagesAsyncTask().execute();

    }

    private class GetInboxMessagesAsyncTask extends AsyncTask<Void, Void, Message[]> {
        private RestTemplate restTemplate = new RestApi().getRestTemplate();
        private Context context;

        @Override
        protected Message[] doInBackground(Void... voids) {
            String uri = "";
            try {
                uri = Util.getProperty("baseAddress", getApplicationContext()) + "/getInbox";
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserUtilsDto userUtilsDto = new UserUtilsDto();
            userUtilsDto.setUsername(active_user.getUsername());
            Message[] result = null;
            try {
                HttpEntity<UserUtilsDto> request = new HttpEntity<>(userUtilsDto);
                result = restTemplate.postForObject(uri, userUtilsDto,Message[].class);
                return result;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Message[] msgs) {

            if(msgs!= null && msgs.length > 0){
                _msg.setVisibility(View.INVISIBLE);

                ArrayList<MailModel> resultSet = new ArrayList<>();

                for(Message m:msgs){
                    MailModel mailModel = new MailModel();
                    mailModel.setFrom(m.getFromUser().getUsername());
                    mailModel.setBody(m.getMessageText());
                    resultSet.add(mailModel);
                }
                userMails.setAdapter(new CustomMailAdapter(getApplicationContext(),resultSet));

            }


        }
    }
}
