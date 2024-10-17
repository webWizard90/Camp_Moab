package net.androidbootcamp.campmoab.Amenities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.androidbootcamp.campmoab.Amenities.Amenities;

//Swipe activity image adapter\\
public class ImagePagerAdapter extends PagerAdapter {
    int[] pictures = Amenities.PICTURES;
    Context context;

    public ImagePagerAdapter(Context context)
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