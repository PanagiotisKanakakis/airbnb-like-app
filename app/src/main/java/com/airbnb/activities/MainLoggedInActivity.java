package com.airbnb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import com.airbnb.shared.dto.entity.Residence;
import com.google.gson.Gson;
import com.sourcey.activities.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainLoggedInActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.searchResultView) ListView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logged_in);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        searchResult.setVisibility(ListView.INVISIBLE);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent, 5);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(getIntent()!=null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            Residence[] residences = new Gson().fromJson(extras.get("residences").toString(), Residence[].class);
            if(residences != null){
                searchResult.setVisibility(ListView.VISIBLE);
                ArrayList<ImageModel> rs = new ArrayList();
                for(int i = 0;i<10;i++) {
                    for (Residence r : residences) {
                        ImageModel img = new ImageModel();
                        img.setDescription(r.getDescription());
                        img.setCost("$" + r.getPrize().toString() + "per night");
                        img.setType(r.getType());
                        img.setGrade("-" + r.getComments().size());
                        img.setReviews(String.valueOf(r.getComments().size()));
                        img.setImage_drawable(R.drawable.man);
                        rs.add(img);
                    }
                }
                CustomAdapter customUpdater = new CustomAdapter(this,rs);
                searchResult.setAdapter(customUpdater);

            }
        }


    }


}
