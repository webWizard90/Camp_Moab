package net.androidbootcamp.campmoab.Classes;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListPopupWindow;
import android.widget.Spinner;

import java.lang.reflect.Field;

public class CustomSpinnerClass extends Spinner {
    private static final String TAG = "CustomSpinnerClass";
    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public CustomSpinnerClass(Context context) {
        super(context);
    }

    public CustomSpinnerClass(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinnerClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinnerClass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinnerClass(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public interface OnSpinnerEventsListener {

        void onSpinnerOpened();

        void onSpinnerClosed();

    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened();
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed();
        }

        // Close the rounded_dropdown_background programmatically
        closeDropdown();
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        android.util.Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened() && hasWindowFocus) {
            android.util.Log.i(TAG, "closing popup");
            performClosedEvent();
        }
    }

    // Method to close rounded_dropdown_background using reflection
    private void closeDropdown() {
        try {
            Field popupField = Spinner.class.getDeclaredField("mPopup");
            popupField.setAccessible(true);
            Object popup = popupField.get(this);

            if (popup instanceof ListPopupWindow) {
                ((ListPopupWindow) popup).dismiss();  // Close the rounded_dropdown_background
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing Spinner rounded_dropdown_background: " + e.getMessage());
        }
    }
}