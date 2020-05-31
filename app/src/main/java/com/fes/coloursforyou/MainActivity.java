package com.fes.coloursforyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Timer timer;
    private View root;
    private TextView timeTv;
    private Button btn_reset;
    private int h,m,s;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://random-word-api.herokuapp.com/word?number=5";

    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        timeTv = (TextView) findViewById(R.id.timeTv);
        btn_reset = (Button)findViewById(R.id.btn_reset);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    Api_Call();
                    startClock();
                }else{
                    Toast.makeText(getApplicationContext(),"Internet Connection required",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isOnline()) {
            Api_Call();
            startClock();
        }else{
            Toast.makeText(getApplicationContext(),"Internet Connection required",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
        }
    }

    private void startClock() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {


                        @Override
                        public void run() {
                            final String hexaTime = getTime();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    applyColor(hexaTime);
                                }
                            });
                        }

                    }, 1000, 3000);

    }

    public String getTime() {
        Calendar c = Calendar.getInstance();
         h = c.get(Calendar.HOUR_OF_DAY) * 255 / 23;
         m = c.get(Calendar.MINUTE) * 255 / 59;
         s = c.get(Calendar.SECOND) * 255 / 59;

        System.out.println("Hour:" + h);
        System.out.println("Min:" + m);
        System.out.println("Sec:" + s);

        return "#" + String.format("%02X", h) + String.format("%02X", m)  + String.format("%02X", s);
    }

    public void applyColor(String hexaTime) {
        int color = Color.parseColor(hexaTime);
            try {
                    int rando = (int) (Math.random() * 5);
                    root.setBackgroundColor(color);
                  //  timeTv.setText(hexaTime + "-" + names[rando]);

            }catch (Exception e){
                    e.printStackTrace();
                }
    }

    //For check internet connectivity
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void Api_Call() {
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();
                names= response.split(",");

                if (!response.equals("")) {
                            names = response
                            .replace("[", "")
                            .replace("]", "")
                                    .replace("\"", "")
                            .split(",");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("EE","Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

}
