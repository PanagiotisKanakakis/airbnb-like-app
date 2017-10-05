package com.airbnb.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.Utils.Util;
import com.airbnb.rest.RestApi;
import com.airbnb.shared.dto.entity.Reservation;
import com.airbnb.shared.dto.entity.Residence;
import com.airbnb.shared.dto.entity.User;
import com.airbnb.shared.dto.residence.AddResidenceResponseDto;
import com.airbnb.shared.dto.residence.ReservationDto;
import com.airbnb.shared.dto.user.UserLogInRequestDto;
import com.google.gson.Gson;
import com.google.gson.internal.ObjectConstructor;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.sourcey.activities.R;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarActivity extends AppCompatActivity {

    private Bundle extras;
    private AddResidenceResponseDto residence;
    private String arrivalDate = null;
    private String departuDate = null;
    private CaldroidFragment caldroidFragment = new CaldroidFragment();
    private Calendar cal = Calendar.getInstance();
    private ArrayList<Date> disabledDates = new ArrayList<>();
    private ProgressDialog progressDialog ;
    private User active_user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        progressDialog =  new ProgressDialog(CalendarActivity.this,
                R.style.AppTheme_Dark_Dialog);
        if(getIntent()!=null && getIntent().getExtras() != null && getIntent().getExtras().get("residence")!=null){
            extras = getIntent().getExtras();
            residence = new Gson().fromJson(extras.get("residence").toString(), AddResidenceResponseDto.class);

            if(residence != null)
                initCalendar(residence.getReservationInfo());

            User user = new Gson().fromJson(extras.get("user").toString(), User.class);
            if(user != null) {
                active_user = user;
                String user_json = new Gson().toJson(active_user);
                extras.putString("user", user_json);

            }
        }



        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                if(arrivalDate == null){
                    arrivalDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

                    Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy-MM-dd").format(date),
                            Toast.LENGTH_SHORT).show();
                    try {
                        caldroidFragment.setSelectedDate(convert(arrivalDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    caldroidFragment.refreshView();
                }else{
                    departuDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    Toast.makeText(getApplicationContext(), new SimpleDateFormat("yyyy-MM-dd").format(date),
                            Toast.LENGTH_SHORT).show();
                    try {
                        caldroidFragment.setSelectedDates(convert(arrivalDate),convert(departuDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    caldroidFragment.refreshView();


                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please wait for the reservation...");
                    progressDialog.show();

                    new ReservationAsyncTask().execute(residence.getResidenceId().toString(),active_user.getUsername(),
                            arrivalDate,departuDate);

                }
            }
        };
        caldroidFragment.setCaldroidListener(listener);

    }

    private void initCalendar(List<Reservation> reservationInfo) {

        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, false);

        for(Reservation r : reservationInfo)
            createRange(r.getArrivalDate(),r.getDepartureDate());

        caldroidFragment.setDisableDates(disabledDates);


        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_view, caldroidFragment);
        t.commit();
    }

    private Date convert(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    private List<Date> createRange(Date start,Date end){

        cal.setTime(start);
        disabledDates.add(cal.getTime());
        while (cal.getTime().before(end)) {
            cal.add(Calendar.DATE, 1);
            disabledDates.add(cal.getTime());
        }
        return disabledDates;
    }


    private class ReservationAsyncTask extends AsyncTask<String,Void,Residence> {

        private RestTemplate restTemplate =  new RestApi().getRestTemplate();

        @Override
        protected Residence doInBackground(String... params) {

            String uri = "";
            try {
                uri = Util.getProperty("baseAddress",getApplicationContext()) +  "/reserveResidence";
            } catch (IOException e) {
                e.printStackTrace();
            }

            ReservationDto reservationDto = new ReservationDto();
            reservationDto.setResidenceId(Integer.parseInt(params[0]));
            reservationDto.setUsername(params[1]);
            try {
                reservationDto.setArrivalDate(convert(params[2]));
                reservationDto.setDepartureDate(convert(params[3]));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Residence residence = null;
            try {
                HttpEntity<ReservationDto> request = new HttpEntity<>(reservationDto);
                residence = restTemplate.postForObject(uri,request, Residence.class);
                return residence;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Residence residence) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Reservation ready!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }, 1000);
            Intent intent = new Intent(getApplicationContext(), MainLoggedInActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }


}