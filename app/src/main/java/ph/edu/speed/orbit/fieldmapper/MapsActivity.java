package ph.edu.speed.orbit.fieldmapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import static com.google.maps.android.SphericalUtil.computeArea;

public class MapsActivity extends FragmentActivity {

    Integer Count;
    Double[] lat = new Double[99];
    Double[] lng = new Double[99];
    SharedPreferences pref;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Count = 0;
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        viewmap();

        Button create = (Button)findViewById(R.id.btSave);
        create.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPolygon();
            }
        });

    }



    //Creates Polygon on other Activity
    private void createPolygon() {
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Create New Polygon");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MapsActivity.this,Mapper.class);
                        startActivity(i);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



    //Setting up the polygons if available from sharedpref
    private void viewmap() {

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String info = pref.getString("Area","No information");
        TextView stat = (TextView) findViewById(R.id.tvStat);
        stat.setText("Area: " + info);

        String str = pref.getString("Coordinates", "[]");
        int num = pref.getInt("Count", 0);
        new json_parser.coordinate_parser(str,num);

        lat = json_parser.Lat;
        lng = json_parser.Lng;


        /** test coordinates for polygon simulation */
        //lng = new Double[]{120.53849458694458,120.53875207901,120.53907394409178,120.53912758827211,120.53921341896057,120.53922414779663,120.53929924964905,120.53935289382935,120.53918123245239,120.53899884223937,120.53887009620665,120.53875207901,120.53848385810853,120.53849458694458};
        //lat = new Double[]{14.68288638982858,14.682969417571767,14.683052445283455,14.682709955769598,14.682429736677431,14.682294816245724,14.682108003202861,14.681869297415833, 14.681807026298122,14.681827783339322, 14.681848540378576,14.682056110662632,14.682626927927863,14.682844875945142};

        Count = lat.length;

        if (lng.length != 0){
            setUpMap();
        }else{
            Toast.makeText(getApplicationContext(), "coordinates are null", Toast.LENGTH_SHORT).show();
        }
    }



    //Setting up the polygons on the map
    private void setUpMap() {
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        //Setting up if ok
        if((mMap != null) && (lat != null) && (lng != null) && Count != 0){
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            PolygonOptions setup = new PolygonOptions().strokeColor(Color.RED).fillColor(Color.BLUE);

            for(int i = 0; i < Count; i++){
                setup.add(new LatLng(lat[i], lng[i]));
            }

            Polygon polygon = mMap.addPolygon(setup);
            //double areaS = computeArea(polygon.getPoints());
            //TextView stat = (TextView) findViewById(R.id.tvStat);
           // stat.setText("Area: %.2f" + String.valueOf(areaS));

            //zoom to location
            LatLng coordinate = new LatLng(lat[0], lng[0]);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate,20);
            mMap.animateCamera(yourLocation);
        }else{
            Toast.makeText(getApplicationContext(), "no polygons to show", Toast.LENGTH_SHORT).show();
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();


        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        viewmap();
    }
}
