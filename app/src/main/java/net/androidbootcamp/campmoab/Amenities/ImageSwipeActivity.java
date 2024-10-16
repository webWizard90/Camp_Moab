package net.androidbootcamp.campmoab.Amenities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.androidbootcamp.campmoab.R;

public class ImageSwipeActivity extends Activity {
    final String[] CONTENT = new String[]
            {"Front Lights",
                    "Yard View",
                    "Local views of 4x4 trails.",
                    "First aid kit with essential items.",
                    "Access Sirius XM with the remote by clicking Listen to Music.",
                    "Change heater controls with the dial.",
                    "Cooler Controls are located to the right of the TV.",
                    "Change cooler using the up and down buttons.",
                    "Hose is on when the black nob is facing down.",
                    "Hose is off when the black nob it horizontal."};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrival_view_pager);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
        ImageView leftArrow = (ImageView) findViewById(R.id.leftNav);
        ImageView rightArrow = (ImageView) findViewById(R.id.rightNav);
        TextView textView = (TextView) findViewById(R.id.textViewPager);

        // get intent data
        Intent i = getIntent();
        // Selected image id
        int position = i.getExtras().getInt("id");

        ImagePagerAdapter adapter = new ImagePagerAdapter(this);
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        textView.setText(CONTENT[position]);

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = pager.getCurrentItem();

                if (tab == 0) {
                    tab = CONTENT.length - 1; // Wrap to the last item
                } else {
                    tab--; // Move to the previous item
                }

                pager.setCurrentItem(tab);
                textView.setText(CONTENT[tab]);
                textView.requestLayout();

                /*if (tab > 0) {
                    tab--;
                    pager.setCurrentItem(tab);
                    //pager.arrowScroll(ViewPager.FOCUS_LEFT);
                    textView.setText(CONTENT[tab]);
                    textView.requestLayout();
                }*/
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = pager.getCurrentItem();

                if (tab == CONTENT.length - 1) {
                    tab = 0; // Wrap to the first item
                } else {
                    tab++; // Move to the next item
                }

                pager.setCurrentItem(tab);
                textView.setText(CONTENT[tab]);
                textView.requestLayout();

                /*if (position < 9) {
                    tab++;
                    pager.setCurrentItem(tab);
                    //pager.arrowScroll(ViewPager.FOCUS_RIGHT);
                    textView.setText(CONTENT[tab]);
                    textView.requestLayout();
                }*/
            }
        });

        //Bind pictures to text - change text on swipe
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                textView.setText(CONTENT[position]);
                textView.requestLayout();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //Close the image adapter
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)v.getContext()).finish();
            }
        });
    }
}
