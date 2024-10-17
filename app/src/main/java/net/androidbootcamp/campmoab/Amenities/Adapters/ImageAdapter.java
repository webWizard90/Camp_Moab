package net.androidbootcamp.campmoab.Amenities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.androidbootcamp.campmoab.R;

public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private int mItemHeight = 0;
    private int mNumColumns = 0;
    private FrameLayout.LayoutParams mImageViewLayoutParams;
    private String[] content;
    private int[] pictures;

    public ImageAdapter(Context context, String[] content) {
        this.content = content;
        //mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageViewLayoutParams = new
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public ImageAdapter(Context context, String[] content, int[] pictures) {
        this.content = content;
        this.pictures = pictures;
        //mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageViewLayoutParams = new
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public int getCount() {
        return content.length;
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
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }

    public Object getItem(int position) { return position; }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {

        if (view == null)
            view = mInflater.inflate(R.layout.activity_arrival_custom_dialog, null);

        //ImageView close = (ImageView) view.findViewById(R.id.closeX);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        TextView title = (TextView) view.findViewById(R.id.txt_image_name);

        img.setLayoutParams(mImageViewLayoutParams);

        // Check the height matches our calculated column width
        if (img.getLayoutParams().height != mItemHeight) {
            img.setLayoutParams(mImageViewLayoutParams);
        }
        //close.setImageResource(position);
        img.setImageResource(pictures[position]);
        title.setText(content[position]);

        return view;
    }
}
