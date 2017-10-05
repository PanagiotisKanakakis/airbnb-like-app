package com.airbnb.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.residence.AddResidenceResponseDto;
import com.airbnb.shared.dto.user.UserLogInRequestDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by panagiotis on 29/9/2017.
 */

public class UpdateResidenceActivity extends AppCompatActivity {

    @Bind(R.id.cost) EditText _cost;
    @Bind(R.id.total_guests) EditText _guests;
    @Bind(R.id.bedrooms_for_guests) EditText _bedrooms;
    @Bind(R.id.beds_for_guests) EditText _beds;
    @Bind(R.id.bathrooms_for_guests) EditText _bath;
    @Bind(R.id.update) Button _update;
    private AddResidenceResponseDto residence;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_residence);
        ButterKnife.bind(this);

        if(getIntent()!=null && getIntent().getExtras() != null) {
            extras = getIntent().getExtras();
            if (extras.get("residence") != null) {
                residence = new Gson().fromJson(extras.get("residence").toString(), AddResidenceResponseDto.class);
                if (residence != null) {
                    _guests.setText(String.valueOf(residence.getCapacity()));
                    _cost.setText(String.valueOf(residence.getPrize()));
                    _bedrooms.setText(String.valueOf(residence.getBedrooms()));
                    _beds.setText(String.valueOf(residence.getBeds()));
                    _bath.setText(String.valueOf(residence.getBathrooms()));
                }
            }
        }

        _update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                residence.setPrize(Integer.parseInt(((EditText)findViewById(R.id.cost)).getText().toString()));
                residence.setBathrooms(Integer.valueOf(((EditText)findViewById(R.id.bathrooms_for_guests)).getText().toString()));
                residence.setCapacity(Integer.valueOf(((EditText)findViewById(R.id.total_guests)).getText().toString()));
                residence.setBedrooms(Integer.valueOf(((EditText)findViewById(R.id.bedrooms_for_guests)).getText().toString()));
                residence.setBeds(Integer.valueOf(((EditText)findViewById(R.id.beds_for_guests)).getText().toString()));

                new UpdateResidenceAsyncTask().execute();

            }
        });
    }

    private class UpdateResidenceAsyncTask extends AsyncTask<Void,Void,Residence> {

        private RestTemplate restTemplate =  new RestApi().getRestTemplate();

        @Override
        protected Residence doInBackground(Void ... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/updateResidence";
            } catch (IOException e) {
                e.printStackTrace();
            }

            Residence result = null;
            Residence updated_residence = new Residence();
            updated_residence.setResidenceId(residence.getResidenceId());
            updated_residence.setPrize(residence.getPrize());
            updated_residence.setBathrooms(residence.getBathrooms());
            updated_residence.setCapacity(residence.getCapacity());
            updated_residence.setBedrooms(residence.getBedrooms());
            updated_residence.setBeds(residence.getBeds());

            try {
                HttpEntity<Residence> request = new HttpEntity<>(updated_residence);
                result = restTemplate.postForObject(uri,request, Residence.class);
                return result;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Residence residence) {
            if(residence != null){
                Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
                String residence_json = new Gson().toJson(residence);
                extras.putString("residence",residence_json);
                intent.putExtras(extras);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }else{
                Toast.makeText(getBaseContext(), "Update failed", Toast.LENGTH_LONG).show();
            }

        }

    }

}
