package ph.edu.speed.orbit.fieldmapper;



import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.google.maps.android.SphericalUtil.computeArea;


public class Mapper extends ActionBarActivity {

    GPSTracker gpsTracker;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int Count;
    Double[] lat = new Double[99];
    Double[] lng = new Double[99];
    JSONObject jCoor;
    JSONArray ArrayCoor;
    double Area;

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
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String str = pref.getString("Coordinates", "[]");
        int num = pref.getInt("Count", 0);
        new json_parser.coordinate_parser(str,num);

        lat = json_parser.Lat;
        lng = json_parser.Lng;
        Count = json_parser.Count;


        /** test coordinates for area computation */
        //lng = new Double[]{120.53849458694458,120.53875207901,120.53907394409178,120.53912758827211,120.53921341896057,120.53922414779663,120.53929924964905,120.53935289382935,120.53918123245239,120.53899884223937,120.53887009620665,120.53875207901,120.53848385810853,120.53849458694458};
        //lat = new Double[]{14.68288638982858,14.682969417571767,14.683052445283455,14.682709955769598,14.682429736677431,14.682294816245724,14.682108003202861,14.681869297415833, 14.681807026298122,14.681827783339322, 14.681848540378576,14.682056110662632,14.682626927927863,14.682844875945142};


        if(lat.length > 2){
            PolygonOptions setup = new PolygonOptions().strokeColor(Color.RED).fillColor(Color.BLUE);

            for(int i = 0; i < lat.length; i++){
                setup.add(new LatLng(lat[i], lng[i]));
            }

            Area = computeArea(setup.getPoints());
            editor = pref.edit();
            editor.putString("Area", String.valueOf(Area));
            editor.commit();
        }

        TextView a = (TextView) findViewById(R.id.tvMstat);
        a.setText("area: " + String.valueOf(Area));

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

