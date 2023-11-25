package net.androidbootcamp.campmoab.Amenities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.androidbootcamp.campmoab.MainActivity;
import net.androidbootcamp.campmoab.R;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserAccount;

public class Amenities extends AppCompatActivity {
// Items to add to the GRID
    static final String[] CONTENT = new String[]
            { "Front Lights", "Yard View", "Wall Maps", "First Aid Kit",
                "Sirius XM", "Heater Controls", "Cooler Controls", "Cooler", "Hose On",
                "Hose Off" };
    static final int[] PICTURES = new int[]
            { R.drawable.house2, R.drawable.backyard,
            R.drawable.wallmap, R.drawable.bathroom, R.drawable.sirius_xm, R.drawable.heat_control,
            R.drawable.cooler_control, R.drawable.cooler, R.drawable.hose_on, R.drawable.hose_off };

    private static final String TAG = Amenities.class.getSimpleName();
    GridView grid;
    private ImageAdapter imageAdapter;
    private ImageView home, account;
    private int mPhotoSize, mPhotoSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities);
        //instantiate controls
        home = (ImageView) findViewById(R.id.homeImage);
        account = (ImageView) findViewById(R.id.acctImage);
        grid = (GridView) findViewById(R.id.gridView);

        // get the photo size and spacing
        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);

        // initialize image adapter
        imageAdapter = new ImageAdapter();

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

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Amenities.this, MainActivity.class));
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Amenities.this, UserAccount.class));
            }
        });
}


    /////Adapter class extends base adapter\\\\\
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private FrameLayout.LayoutParams mImageViewLayoutParams;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageViewLayoutParams = new
                    FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
        }

        public int getCount() {
            return CONTENT.length;
        }

        // set numcols
        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

        // set photo item height
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new
                    FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }

        public Object getItem(int position) { return position; }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null)
                view = mInflater.inflate(R.layout.custom_dialog, null);

            //ImageView close = (ImageView) view.findViewById(R.id.closeX);
            ImageView img = (ImageView) view.findViewById(R.id.img);
            TextView title = (TextView) view.findViewById(R.id.txt_image_name);
            ImageView home = (ImageView) view.findViewById(R.id.homeImage);
            ImageView account = (ImageView) view.findViewById(R.id.acctImage);


            img.setLayoutParams(mImageViewLayoutParams);

            // Check the height matches our calculated column width
            if (img.getLayoutParams().height != mItemHeight) {
                img.setLayoutParams(mImageViewLayoutParams);
            }
            //close.setImageResource(position);
            img.setImageResource(PICTURES[position]);
            title.setText(CONTENT[position]);

            return view;
        }
    }
}