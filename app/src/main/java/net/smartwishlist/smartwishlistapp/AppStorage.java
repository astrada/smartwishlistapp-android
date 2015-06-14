package net.smartwishlist.smartwishlistapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import com.appspot.smart_wish_list.smartwishlist.model.SmartWishListNotificationTriggerData;
import com.google.api.client.json.JsonParser;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

public class AppStorage {

    private final DbOpenHelper dbOpenHelper;

    public AppStorage(Context context) {
        dbOpenHelper = new DbOpenHelper(context);
    }

    public void insertNotifications(List<SmartWishListNotificationTriggerData> triggers) {
        if (triggers == null) return;

        Double timestamp = ApiSignature.getTimestamp();
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            for (SmartWishListNotificationTriggerData trigger : triggers) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(NotificationContract.COLUMN_NAME_PRODUCT_ID,
                        trigger.getItem().getRegion() +
                                trigger.getItem().getAsin());
                try {
                    contentValues.put(NotificationContract.COLUMN_NAME_JSON,
                            AppConstants.JSON_FACTORY.toString(trigger));
                } catch (IOException e) {
                    AppLogging.logException(e);
                }
                contentValues.put(NotificationContract.COLUMN_NAME_TIMESTAMP, timestamp);
                sqLiteDatabase.insertWithOnConflict(NotificationContract.TABLE_NAME, null,
                        contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public Cursor queryAllNewNotifications(double timestamp) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        return sqLiteDatabase.query(NotificationContract.TABLE_NAME,
                new String[]{NotificationContract._ID,
                        NotificationContract.COLUMN_NAME_JSON},
                NotificationContract.COLUMN_NAME_TIMESTAMP + " >= ?",
                new String[]{Double.toString(timestamp)},
                null, null, null);
    }

    public SmartWishListNotificationTriggerData queryNotificationById(long id) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(NotificationContract.TABLE_NAME,
                new String[]{NotificationContract.COLUMN_NAME_JSON},
                NotificationContract._ID + " = ?", new String[]{Long.toString(id)},
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String json = cursor.getString(0);
            SmartWishListNotificationTriggerData result = parseNotificationTriggerData(json);
            if (result != null) return result;
            cursor.close();
        }
        return null;
    }

    public void deleteAllOldNotifications(double timestamp) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.delete(NotificationContract.TABLE_NAME,
                    NotificationContract.COLUMN_NAME_TIMESTAMP + " < ?",
                    new String[]{Double.toString(timestamp)});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    @Nullable
    public static SmartWishListNotificationTriggerData parseNotificationTriggerData(String json) {
        SmartWishListNotificationTriggerData result = new SmartWishListNotificationTriggerData();
        try {
            JsonParser jsonParser = AppConstants.JSON_FACTORY.createJsonParser(json);
            jsonParser.parse(result);
            return result;
        } catch (IOException e) {
            AppLogging.logException(e);
        }
        return null;
    }

    public static class NotificationLoader extends CursorLoader {

        private final double timestamp;

        public NotificationLoader(Context context, double timestamp) {
            super(context);
            this.timestamp = timestamp;
        }

        @Override
        public Cursor loadInBackground() {
            AppStorage appStorage = new AppStorage(getContext());
            return appStorage.queryAllNewNotifications(timestamp);
        }
    }

    public static class LoadTriggerDataTask
            extends AsyncTask<Long, Void, SmartWishListNotificationTriggerData> {

        private final Context context;

        public LoadTriggerDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected SmartWishListNotificationTriggerData doInBackground(Long... longs) {
            AppStorage appStorage = new AppStorage(context);
            return appStorage.queryNotificationById(longs[0]);
        }
    }

    public static class NotificationContract implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id";
        public static final String COLUMN_NAME_JSON = "json";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    private static class DbOpenHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = AppConstants.APP_NAMESPACE + ".db";
        private static final int DATABASE_VERSION = 1;
        private static final String SQL_CREATE_SCHEMA =
                "CREATE TABLE " + NotificationContract.TABLE_NAME + " (" +
                        NotificationContract._ID + " INTEGER PRIMARY KEY, " +
                        NotificationContract.COLUMN_NAME_PRODUCT_ID + " UNIQUE, " +
                        NotificationContract.COLUMN_NAME_JSON + " TEXT," +
                        NotificationContract.COLUMN_NAME_TIMESTAMP + " FLOAT);" +
                "CREATE INDEX notification_timestamp ON " + NotificationContract.TABLE_NAME +
                        " (" + NotificationContract.COLUMN_NAME_TIMESTAMP + ");";
        private static final String SQL_DROP_SCHEMA =
                "DROP TABLE IF EXISTS " + NotificationContract.TABLE_NAME + ";" +
                "DROP INDEX IF EXISTS notification_timestamp;";

        DbOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            db.execSQL(SQL_CREATE_SCHEMA);
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            recreateSchema(db, newVersion);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            recreateSchema(db, newVersion);
        }

        private void recreateSchema(SQLiteDatabase db, int newVersion) {
            if (newVersion == DATABASE_VERSION) {
                db.beginTransaction();
                db.execSQL(SQL_DROP_SCHEMA);
                db.execSQL(SQL_CREATE_SCHEMA);
                db.setTransactionSuccessful();
                db.endTransaction();
            }
        }
    }
}
