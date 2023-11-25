package net.androidbootcamp.campmoab.Amenities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.androidbootcamp.campmoab.R;

public class ImageSwipeActivity extends Activity {
    final String[] CONTENT = new String[]
            {"Front Lights",
                    "Yard View",
                    "Wall maps provide local views of 4x4 trails.",
                    "First aid kit has most of the essential items.",
                    "Sirius XM can be accessed with the remote by clicking Listen to Music.",
                    "Heater controls can be changed with the dial.",
                    "Cooler Controls are located to the right of the TV.",
                    "Cooler can be changed using the up and down buttons.",
                    "Hose is on when the black nob is facing down.",
                    "Hose is off when the black nob it horizontal."};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

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

                if (tab > 0) {
                    tab--;
                    pager.setCurrentItem(tab);
                    //pager.arrowScroll(ViewPager.FOCUS_LEFT);
                    textView.setText(CONTENT[tab]);
                    textView.requestLayout();
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = pager.getCurrentItem();

                if (position < 9) {
                    tab++;
                    pager.setCurrentItem(tab);
                    //pager.arrowScroll(ViewPager.FOCUS_RIGHT);
                    textView.setText(CONTENT[tab]);
                    textView.requestLayout();
                }
            }
        });

        //Bind pictures to text - change text on swipe
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        textView.setText(CONTENT[0]);
                        break;
                    case 1:
                        textView.setText(CONTENT[1]);
                        break;
                    case 2:
                        textView.setText(CONTENT[2]);
                        break;
                    case 3:
                        textView.setText(CONTENT[3]);
                        break;
                    case 4:
                        textView.setText(CONTENT[4]);
                        break;
                    case 5:
                        textView.setText(CONTENT[5]);
                        break;
                    case 6:
                        textView.setText(CONTENT[6]);
                        break;
                    case 7:
                        textView.setText(CONTENT[7]);
                        break;
                    case 8:
                        textView.setText(CONTENT[8]);
                        break;
                    case 9:
                        textView.setText(CONTENT[9]);
                        break;
                }
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


    //Swipe activity image adapter\\
    class ImagePagerAdapter extends PagerAdapter {
        int[] pictures = Amenities.PICTURES;
        Context context;

        ImagePagerAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public int getCount() {
            return pictures.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view ==  object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(pictures[position]);

            ((ViewPager) container).addView(imageView, 0);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }
}
