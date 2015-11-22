package mae.comp3717.bcit.ca.assignment3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class CourseActivity
        extends AppCompatActivity
{

    public static final String COURSE_INDEX = "course-index";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final Intent intent;
        final int    index;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        intent = getIntent();
        index = intent.getIntExtra(COURSE_INDEX,
                -1);

        try
        {
            final SQLiteOpenHelper helper;
            final SQLiteDatabase database;
            final Cursor cursor;

            helper = new BCITDatabaseHelper(this);
            database = helper.getReadableDatabase();
            cursor = database.query(BCITDatabaseHelper.COURSE_TABLE,
                    new String[] { BCITDatabaseHelper.COURSE_NAME,
                            BCITDatabaseHelper.COURSE_DESCRIPTION
                    },
                    "_id = ?",
                    new String[] { Integer.toString(index)
                    },
                    null,
                    null,
                    null);

            if(cursor.moveToFirst())
            {
                final String name;
                final String description;
                final TextView descriptionView;

                name = cursor.getString(0);
                description = cursor.getString(1);
                setTitle(name);
                descriptionView = (TextView)findViewById(R.id.description);
                descriptionView.setText(description);
            }

            cursor.close();
            database.close();
        }
        catch(final SQLiteException ex)
        {
            Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_LONG).show();
            Log.e("CourseActivity",
                    "Database error",
                    ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
