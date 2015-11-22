package mae.comp3717.bcit.ca.assignment3;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BCITDatabaseHelper
        extends SQLiteOpenHelper
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
//        database.execSQL("DROP TABLE IF EXISTS " + BCITDatabaseHelper.COURSE_TABLE);
//        System.out.println("!!! DROP TABLE");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + COURSE_TABLE + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_NAME + " TEXT, " +
                COURSE_DESCRIPTION + " TEXT)");
        System.out.println("!!! CREATE TABLE");

        //insertCourse(database, "COMP 3717", "Android");
        //insertCourse(database, "COMP 3512", "C++");
        //insertCourse(database, "COMP 3711", "OOAD");
        //insertCourse(database, "COMP 3721", "Datacomm");
        //insertCourse(database, "COMP 3760", "Algorithms");
        //insertCourse(database, "COMP 3900", "Projects");
    }

    public void insertCourse(final SQLiteDatabase database,
                             final String name,
                             final String description)
    {
        final ContentValues courseValues;

        courseValues = new ContentValues();
        courseValues.put(COURSE_NAME,name);
        courseValues.put(COURSE_DESCRIPTION, description);
        System.out.println("!!! BEFORE INSERTING ROW");
        database.insert(COURSE_TABLE, null, courseValues);
        System.out.println("!!! AFTER INSERTING ROW");
    }

    @Override
    public void onUpgrade(final SQLiteDatabase database,
                          final int            oldVersion,
                          final int            newVersion)
    {
    }
}
