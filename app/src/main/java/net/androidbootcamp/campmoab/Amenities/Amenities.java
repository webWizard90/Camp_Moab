package net.androidbootcamp.campmoab.Amenities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import net.androidbootcamp.campmoab.Amenities.Adapters.ImageAdapter;
import net.androidbootcamp.campmoab.BaseActivities.BaseActivity;
import net.androidbootcamp.campmoab.R;

public class Amenities extends BaseActivity {
    public static final String[] CONTENT = new String[]
            { "Front Lights", "Yard View", "Back Deck View", "Side View", "Wall Maps", "First Aid Kit",
                "Sirius XM", "Global Remote", "Apps Remote", "Heater Controls", "Cooler Controls", "Cooler" };
    public static final int[] PICTURES = new int[]
            { R.drawable.house2, R.drawable.main_view, R.drawable.deck_view, R.drawable.street_view,
            R.drawable.wallmaps, R.drawable.first_aid_kit, R.drawable.sirius_xm, R.drawable.global_remote, R.drawable.app_remote, R.drawable.heat_control,
            R.drawable.cooler_controls, R.drawable.cooler };
    private static final String TAG = Amenities.class.getSimpleName();
    private GridView grid;
    private int mPhotoSize, mPhotoSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_amenities, findViewById(R.id.content_frame));
        toolbar.setTitle("Amenities");

        //account = (ImageView) findViewById(R.id.acctImage);
        grid = (GridView) findViewById(R.id.gridView);

        // Get the photo size and spacing
        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);

        // Pass CONTENT and PICTURES to ImageAdapter
        ImageAdapter imageAdapter = new ImageAdapter(this, CONTENT, PICTURES);

        //when picture is clicked on, start swipe activity
        grid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Sending image id to FullScreenActivity
                Intent i = new Intent(Amenities.this, ImageSwipeActivity.class);
                // passing array index to swipe activity
                i.putExtra("id", position);
                startActivity(i);
            }
        });

        // set image adapter to the GridView
        grid.setAdapter(imageAdapter);

        // get the view tree observer of the grid and set the height and number columns dynamically
        grid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imageAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(grid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (grid.getWidth() / numColumns) - mPhotoSpacing;
                        imageAdapter.setNumColumns(numColumns);
                        imageAdapter.setItemHeight(columnWidth);

                    }
                }
            }
        });
    }
}