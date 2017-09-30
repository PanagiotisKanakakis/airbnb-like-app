package com.airbnb.activities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.Utils.Util;
import com.airbnb.images.ImageModel;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Comment;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.user.UserUtilsDto;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;


import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewProfileActivity extends AppCompatActivity {

    private User active_user;
    private Bundle bundle = new Bundle();
    @Bind(R.id.imgView) ImageView _img;
    @Bind(R.id.name) TextView _nameText;
    @Bind(R.id.reviews) TextView _reviews;
    @Bind(R.id.email) TextView _emailText;
    @Bind(R.id.number_of_homes) TextView _number_of_homes;
    @Bind(R.id.userResidences) ListView _userResidences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            User user = new Gson().fromJson(extras.get("host_user").toString(), User.class);
            if (user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("host_user", user_json);

                _nameText.setText(active_user.getName());
                _emailText.setText(active_user.getEmail());

                if(active_user.getProfilePhoto() != null && active_user.getProfilePhoto().getPath() != null){
                    Uri uri = Uri.fromFile(new File(active_user.getProfilePhoto().getPath()));
                    Picasso.with(this).load(uri)
                            .transform(new CircleTransform())
                            .resize(150,150).into(_img);
                }
                new GetUserResidencesAsyncTask(this).execute();
            }
        }
    }

    private class GetUserResidencesAsyncTask extends AsyncTask<String, Void, Residence[]> {
        private RestTemplate restTemplate =  new RestApi().getRestTemplate();
        private Context context;

        public GetUserResidencesAsyncTask(Context applicationContext) {
            this.context = applicationContext;
        }

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
                result = restTemplate.postForObject(uri, request,Residence[].class);
                return result;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Residence[] result) {
            if(result != null && result.length > 0){
                ArrayList<ImageModel> resultSet = new ArrayList<>();
                for(Residence r : result){
                    ImageModel im = new ImageModel();
                    im.setResidenceId(r.getResidenceId());
                    im.setCost(String.valueOf(r.getPrize()));
                    im.setDescription(r.getDescription());

                    int sum = 0;
                    for(Comment c : r.getComments())
                        sum += c.getGrade();

                    if(r.getComments().size() > 0)
                        im.setGrade(String.valueOf(sum / r.getComments().size()));
                    else
                        im.setGrade(String.valueOf(0.0));

                    if(r.getPhotoPaths() != null && r.getPhotoPaths().size() > 0)
                        im.setPath(r.getPhotoPaths().get(0).getPath());

                    im.setType(r.getType());
                    resultSet.add(im);
                }
                _userResidences.setAdapter(new CustomAdapter(getApplicationContext(),resultSet,"tenant",bundle));
            }
        }
    }
}
