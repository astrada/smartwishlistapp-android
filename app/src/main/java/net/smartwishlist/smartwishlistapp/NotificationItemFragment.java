package net.smartwishlist.smartwishlistapp;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;


public class NotificationItemFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private OnItemSelectedListener onItemSelectedListener;
    private NotificationSimpleCursorAdapter adapter;
    private double timestamp;

    public NotificationItemFragment() {
    }

    public interface OnItemSelectedListener {
        void onItemSelected(long id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new NotificationSimpleCursorAdapter(getActivity());
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onItemSelectedListener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement NotificationItemFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        onItemSelectedListener.onItemSelected(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        timestamp = ApiSignature.getTimestamp();
        AppPreferences preferences = new AppPreferences(getActivity());
        return new AppStorage.NotificationLoader(getActivity(), preferences.getLastViewedNotifications());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        AppPreferences preferences = new AppPreferences(getActivity());
        preferences.setLastViewedNotifications(timestamp);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private static class NotificationSimpleCursorAdapter extends SimpleCursorAdapter {

        private static final String[] FROM_COLUMNS = {AppStorage.NotificationContract._ID,
                AppStorage.NotificationContract.COLUMN_NAME_JSON};
        private static final int[] TO_VIEW = {};

        public NotificationSimpleCursorAdapter(Context context) {
            super(context, R.layout.fragment_notification_item, null, FROM_COLUMNS, TO_VIEW, 0);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            String json = cursor.getString(1);
            SmartWishListNotificationTriggerData trigger =
                    AppStorage.parseNotificationTriggerData(json);
            if (trigger != null) {
                ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView description = (TextView) view.findViewById(R.id.price);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.infoButton);

                DownloadImageTask task = new DownloadImageTask(thumbnail);
                task.execute(trigger.getItem().getImageUrl());
                title.setText(trigger.getItem().getTitle());
                description.setText(trigger.getItem().getFormattedPrice());
                imageButton.setTag(cursor.getLong(0));
            }
        }
    }
}
