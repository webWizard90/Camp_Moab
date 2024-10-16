package net.androidbootcamp.campmoab.Hiking_Trails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.androidbootcamp.campmoab.BaseActivity;
import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

public class Hiking_Trails extends BaseActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private LinearLayout hiddenView1, hiddenView2, hiddenView3, hiddenView4;
    private CardView card1, card2, card3, card4;
    private ImageView arrow1, arrow2, arrow3, arrow4;
    private SupportMapFragment mapFragment;
    //private MapView mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_hiking_trails, findViewById(R.id.content_frame));
        toolbar.setTitle("Hiking Trails");

        String[] hikes = {"Hidden Valley Trail", "Carona Arch Trail", "Negro Bill Canyon", "Morning Glory Bridge"};
        hiddenView1 = (LinearLayout) findViewById(R.id.hidden_view1);
        hiddenView2 = (LinearLayout) findViewById(R.id.hidden_view2);
        hiddenView3 = (LinearLayout) findViewById(R.id.hidden_view3);
        hiddenView4 = (LinearLayout) findViewById(R.id.hidden_view4);
        card1 = (CardView) findViewById(R.id.cardHiddenValley);
        card2 = (CardView) findViewById(R.id.cardCaronaArch);
        card3 = (CardView) findViewById(R.id.cardNegroBill);
        card4 = (CardView) findViewById(R.id.cardMorningGlory);
        arrow1 = (ImageView) findViewById(R.id.iconDropDown1);
        arrow2 = (ImageView) findViewById(R.id.iconDropDown2);
        arrow3 = (ImageView) findViewById(R.id.iconDropDown3);
        arrow4 = (ImageView) findViewById(R.id.iconDropDown4);
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the CardView is already expanded, set its visibility
                // to gone and change the expand less icon to expand more.
                if (hiddenView1.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(card1, new AutoTransition());
                    hiddenView1.setVisibility(View.GONE);
                    arrow1.setImageResource(R.drawable.drop_down_arrow_black);
                }
                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(card1, new AutoTransition());
                    hiddenView1.setVisibility(View.VISIBLE);
                    arrow1.setImageResource(R.drawable.drop_down_arrow_black);
                }
            }
        });

        arrow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the CardView is already expanded, set its visibility
                // to gone and change the expand less icon to expand more.
                if (hiddenView2.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(card2, new AutoTransition());
                    hiddenView2.setVisibility(View.GONE);
                    arrow2.setImageResource(R.drawable.drop_down_arrow_black);
                }
                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(card2, new AutoTransition());
                    hiddenView2.setVisibility(View.VISIBLE);
                    arrow2.setImageResource(R.drawable.drop_down_arrow_black);
                }
            }
        });

        arrow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the CardView is already expanded, set its visibility
                // to gone and change the expand less icon to expand more.
                if (hiddenView3.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(card3, new AutoTransition());
                    hiddenView3.setVisibility(View.GONE);
                    arrow3.setImageResource(R.drawable.drop_down_arrow_black);
                }
                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(card3, new AutoTransition());
                    hiddenView3.setVisibility(View.VISIBLE);
                    arrow3.setImageResource(R.drawable.drop_down_arrow_black);
                }
            }
        });

        arrow4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the CardView is already expanded, set its visibility
                // to gone and change the expand less icon to expand more.
                if (hiddenView4.getVisibility() == View.VISIBLE) {
                    // The transition of the hiddenView is carried out by the TransitionManager class.
                    // Here we use an object of the AutoTransition Class to create a default transition
                    TransitionManager.beginDelayedTransition(card4, new AutoTransition());
                    hiddenView4.setVisibility(View.GONE);
                    arrow4.setImageResource(R.drawable.drop_down_arrow_black);
                }
                // If the CardView is not expanded, set its visibility to
                // visible and change the expand more icon to expand less.
                else {
                    TransitionManager.beginDelayedTransition(card4, new AutoTransition());
                    hiddenView4.setVisibility(View.VISIBLE);
                    arrow4.setImageResource(R.drawable.drop_down_arrow_black);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        final LatLng hiddenVallyLocation = new LatLng(38.573315, -109.549843);
        Marker hiddenVally = googleMap.addMarker(
                new MarkerOptions()
                        .position(hiddenVallyLocation)
                        .title("Hidden Valley")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        final LatLng caronaArchLocation = new LatLng(38.579943, -109.619887);
        Marker caronaArch = googleMap.addMarker(
                new MarkerOptions()
                        .position(caronaArchLocation)
                        .title("Carona Arch")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        final LatLng negroBillLocation = new LatLng(38.61003,-109.534563);
        Marker negroBill= googleMap.addMarker(
                new MarkerOptions()
                        .position(negroBillLocation)
                        .title("Negro Bill Canyon")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        final LatLng morningGloryLocation = new LatLng(38.593548, -109.508633);
        Marker morningGlory = googleMap.addMarker(
                new MarkerOptions()
                        .position(morningGloryLocation)
                        .title("Morning Glory Arch") 
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        // Move camera to the specific location and zoom in
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(negroBillLocation, 10.5f));

        // Set a listener for info window events.
        googleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }
}