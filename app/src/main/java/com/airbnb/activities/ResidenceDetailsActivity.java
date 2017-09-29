package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.airbnb.Utils.Util;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.comment.CommentDto;
import com.airbnb.shared.dto.entity.Comment;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.residence.AddResidenceResponseDto;
import com.airbnb.shared.dto.residence.SearchResidenceByIdDto;
import com.airbnb.shared.dto.residence.SearchResidenceDto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResidenceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {


    @Bind(R.id.imgView) ImageView _img;
    @Bind(R.id.type) TextView _type;
    @Bind(R.id.ratingBar) RatingBar _bar;
    @Bind(R.id.num_of_reviews) TextView _num_of_reviews;
    @Bind(R.id.cost) TextView _cost;
    @Bind(R.id.hostedBy) TextView _hostedBy;
    @Bind(R.id.guests) TextView _guests;
    @Bind(R.id.bedrooms) TextView _bedrooms;
    @Bind(R.id.rules) TextView _rules;
    @Bind(R.id.beds) TextView _beds;
    @Bind(R.id.bath) TextView _bath;
    @Bind(R.id.description) TextView _description;
    @Bind(R.id.reviewList) ListView _reviewList;
    @Bind(R.id.checkAvailability) Button _checkAvailability;
    private GoogleMap mMap;
    private String mode;
    private Bundle extras;
    private User active_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residence_details);
        ButterKnife.bind(this);

        MapView mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        _checkAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(getIntent()!=null && getIntent().getExtras() != null && getIntent().getExtras().get("residenceId")!=null){
            extras = getIntent().getExtras();
            final String residenceId = new Gson().fromJson(extras.get("residenceId").toString(), String.class);
            this.mode = extras.get("mode").toString();

           /* User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            mode = extras.get("mode").toString();
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                extras.putString("user", user_json);
            }*/
            if(residenceId != null) {
                new GetResidenceByIdAsyncTask(this).execute(Integer.parseInt(residenceId));
                new GetCommentsOfResidenceAsyncTask(this).execute(Integer.parseInt(residenceId));
            }

            if(this.mode.equals("host")) {
                _checkAvailability.setText("Update");
                _checkAvailability.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), UpdateResidenceActivity.class);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                });

            }

        }

    }

    private class GetResidenceByIdAsyncTask extends AsyncTask<Integer, Void, AddResidenceResponseDto> {

        private RestTemplate restTemplate =  new RestApi().getRestTemplate();
        private Context context;

        public GetResidenceByIdAsyncTask(Context applicationContext) {
            this.context = applicationContext;
        }

        @Override
        protected AddResidenceResponseDto doInBackground(Integer... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) + "/searchResidenceById";
            } catch (IOException e) {
                e.printStackTrace();
            }

            SearchResidenceByIdDto searchResidenceByIdDto = new SearchResidenceByIdDto();
            searchResidenceByIdDto.setResidenceId(params[0]);

            AddResidenceResponseDto result = null;
            try {
                HttpEntity<SearchResidenceByIdDto> request = new HttpEntity<>(searchResidenceByIdDto);
                result = restTemplate.postForObject(uri, request,AddResidenceResponseDto.class);
                return result;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(AddResidenceResponseDto residence) {
           if(residence != null){
               _type.setText(residence.getType());
               _hostedBy.setText("Hosted by " + residence.getUsername());
               _guests.setText(String.valueOf(residence.getCapacity()) + " Guest(s)");
               _cost.setText(String.valueOf(residence.getPrize()) + " per night");
               _bedrooms.setText(String.valueOf(residence.getBedrooms()) + " Bedroom(s)" );
               _beds.setText(String.valueOf(residence.getBeds()) + " Bed(s)");
               _bath.setText(String.valueOf(residence.getBathrooms())+ " Bath(s)");
               _description.setText(residence.getDescription());
               _rules.setText(residence.getRules());
               _num_of_reviews.setText(String.valueOf(residence.getComments().size()));

               int sum = 0;
               for(Comment c : residence.getComments())
                    sum += c.getGrade();

               if(residence.getComments().size() > 0)
                   _bar.setRating(sum / residence.getComments().size());
               else
                   _bar.setRating((float) 0.0);


               Uri uri = Uri.fromFile(new File(residence.getPhotoPaths().get(0)));
               Picasso.with(context).load(uri)
                       .resize(1000, 400)
                       .centerCrop().into(_img);
               LatLng latLng = new LatLng(34,32);
               mMap.addMarker(new MarkerOptions().position(latLng));


               String residence_json = new Gson().toJson(residence);
               extras.putString("residence", residence_json);

           }
        }
    }

    private class GetCommentsOfResidenceAsyncTask extends AsyncTask<Integer, Void, CommentDto[]> {

        private RestTemplate restTemplate =  new RestApi().getRestTemplate();
        private Context context;

        public GetCommentsOfResidenceAsyncTask(Context applicationContext) {
            this.context = applicationContext;
        }

        @Override
        protected CommentDto[] doInBackground(Integer... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) + "/getCommentsForResidence";
            } catch (IOException e) {
                e.printStackTrace();
            }

            SearchResidenceByIdDto searchResidenceByIdDto = new SearchResidenceByIdDto();
            searchResidenceByIdDto.setResidenceId(params[0]);

            CommentDto[] result = null;
            try {
                HttpEntity<SearchResidenceByIdDto> request = new HttpEntity<>(searchResidenceByIdDto);
                result = restTemplate.postForObject(uri, request,CommentDto[].class);
                return result;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommentDto[] comments) {
            if(comments != null){

            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }




}
