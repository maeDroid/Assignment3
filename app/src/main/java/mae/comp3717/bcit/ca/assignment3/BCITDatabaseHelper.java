package mae.comp3717.bcit.ca.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BCITDatabaseHelper extends SQLiteOpenHelper
{
    public static final String DB_NAME = "bcit";
    public static final int DB_VERSION = 1;
    public static final String COURSE_TABLE = "COURSE";
    public static final String COURSE_NAME = "NAME";
    public static final String COURSE_DESCRIPTION = "DESCRIPTION";

    public BCITDatabaseHelper(final Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase database)
    {
        database.execSQL("CREATE TABLE IF NOT EXISTS " + COURSE_TABLE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_NAME + " TEXT, " +
                COURSE_DESCRIPTION + " TEXT)");
    }

    public void insertCourse(final SQLiteDatabase database,
                             final String name,
                             final String description)
    {
        final ContentValues courseValues;

        courseValues = new ContentValues();
        courseValues.put(COURSE_NAME,name);
        courseValues.put(COURSE_DESCRIPTION, description);
        database.insert(COURSE_TABLE, null, courseValues);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database,
                          final int            oldVersion,
                          final int            newVersion)
    {
    }
}
