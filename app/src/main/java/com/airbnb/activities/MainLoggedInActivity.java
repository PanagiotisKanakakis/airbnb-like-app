package com.airbnb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.airbnb.images.ImageModel;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainLoggedInActivity extends AppCompatActivity {
    private static final int INBOX = 1;
    private static final int PROFILE = 2;
    private static final int HOST = 3;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.searchResultView) ListView searchResult;
    @Bind(R.id.profile) TextView _profileLink;
    @Bind(R.id.inbox) TextView _inboxLink;
    @Bind(R.id.host) TextView _hostLink;

    private User active_user;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logged_in);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();

            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
            }

            if (extras.get("residences") != null){
                Residence[] residences = new Gson().fromJson(extras.get("residences").toString(),Residence[].class);
                if(residences != null){
                    ArrayList<ImageModel> resultSet = new ArrayList<>();
                    for(Residence r : residences){
                        ImageModel im = new ImageModel();
                        im.setResidenceId(r.getResidenceId());
                        im.setCost("30");
                        im.setDescription(r.getDescription());
                        im.setGrade("0");

                        if(r.getPhotoPaths() != null && r.getPhotoPaths().size() > 0)
                            im.setPath(r.getPhotoPaths().get(0).getPath());

                        im.setType(r.getType());
                        resultSet.add(im);
                    }
                    searchResult.setAdapter(new CustomAdapter(getApplicationContext(),resultSet));
                    searchResult.setVisibility(ListView.VISIBLE);
                }
            }
        }else
            searchResult.setVisibility(ListView.INVISIBLE);




        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _inboxLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InboxActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _profileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _hostLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HostMainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();

            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                bundle.putString("user", user_json);
            }

            if (extras.get("residences") != null){
                Residence[] residences = new Gson().fromJson(extras.get("residences").toString(),Residence[].class);
                if(residences != null){
                    ArrayList<ImageModel> resultSet = new ArrayList<>();
                    for(Residence r : residences){
                        ImageModel im = new ImageModel();
                        im.setResidenceId(r.getResidenceId());
                        im.setCost("30");
                        im.setDescription(r.getDescription());
                        im.setGrade("0");

                        if(r.getPhotoPaths() != null && r.getPhotoPaths().size() > 0)
                            im.setPath(r.getPhotoPaths().get(0).getPath());

                        im.setType(r.getType());
                        resultSet.add(im);
                    }
                    searchResult.setAdapter(new CustomAdapter(getApplicationContext(),resultSet));
                }
            }



        }


    }


}
