package com.airbnb.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.residence.AddResidenceRequestDto;
import com.airbnb.shared.dto.user.UserUtilsDto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListingActivity extends AppCompatActivity {

    private static final String TAG = "1";
    private User active_user;
    private Bundle bundle = new Bundle();
    @Bind(R.id.userResidences) ListView userResidences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_progress);
        ButterKnife.bind(this);
        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
                new GetUserResidencesAsynchTask().execute();
            }
        }
    }

    private class GetUserResidencesAsynchTask extends AsyncTask<String, Void, Residence[]> {
        private RestTemplate restTemplate =  new RestApi().getRestTemplate();

        @Override
        protected Residence[] doInBackground(String... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/getUserResidences";
            } catch (IOException e) {
                e.printStackTrace();
            }

            UserUtilsDto userUtilsDto = new UserUtilsDto();
            userUtilsDto.setUsername(active_user.getUsername());

            Residence[] result = null;
            try {
                HttpEntity<UserUtilsDto> request = new HttpEntity<>(userUtilsDto);
                result = restTemplate.getForObject(uri, Residence[].class, request);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Residence[] result) {
            if(result != null){
                ArrayList<ImageModel> resultSet = new ArrayList<>();

                for(Residence r : result){
                    ImageModel im = new ImageModel();
                    im.setCost("30");
                    im.setDescription(r.getDescription());
                    im.setGrade("0");
                    im.setImagePath(r.getPhotoPaths().get(0));
                    im.setType(r.getType());
                    resultSet.add(im);
                }
                userResidences.setAdapter(new CustomAdapter(getApplicationContext(),resultSet));
            }
        }
    }
}
