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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

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
        Count = json_parser.Count;

        if (Count != 0){
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

            mMap.addPolygon(setup);
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
