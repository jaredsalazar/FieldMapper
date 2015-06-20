package ph.edu.speed.orbit.fieldmapper;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Mapper extends ActionBarActivity {

    GPSTracker gpsTracker;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int Count;
    Double[] lat = new Double[99];
    Double[] lng = new Double[99];
    JSONObject jCoor;
    JSONArray ArrayCoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapper);

        Button start = (Button) findViewById(R.id.btStart);
        Button stop = (Button) findViewById(R.id.btStop);

        Count = 0;
        jCoor = new JSONObject();
        ArrayCoor = new JSONArray();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapping_start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapping_stop();
            }
        });


    }

    private void mapping_stop() {
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
    }

    private void mapping_start() {
        try {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            editor = pref.edit();
            gpsTracker = new GPSTracker(Mapper.this);
            lat[Count] = gpsTracker.getLatitude();
            lng[Count] = gpsTracker.getLongitude();
            jCoor.put("Latitude", lat[Count]);
            jCoor.put("Longitude", lng[Count]);
            ArrayCoor.put(jCoor);
            Count++;
            jCoor = new JSONObject();
            editor.putString("Coordinates", String.valueOf(ArrayCoor));
            editor.putInt("Count", Count);
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

