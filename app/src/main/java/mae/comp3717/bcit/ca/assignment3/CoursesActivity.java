package mae.comp3717.bcit.ca.assignment3;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class CoursesActivity
        extends ListActivity
{
    private SQLiteDatabase database;
    private Cursor         cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final ListView courseList;

        super.onCreate(savedInstanceState);
        courseList = getListView();

        try
        {
            final SQLiteOpenHelper helper;
            final SimpleCursorAdapter courseAdapter;

            helper   = new BCITDatabaseHelper(this);
            database = helper.getReadableDatabase();
            cursor   = database.query(BCITDatabaseHelper.COURSE_TABLE,
                    new String[]
                            {
                                    "_id",
                                    BCITDatabaseHelper.COURSE_NAME
                            },
                    null,
                    null,
                    null,
                    null,
                    null);
            courseAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    new String[]
                            {
                                    BCITDatabaseHelper.COURSE_NAME
                            },
                    new int[]
                            {
                                    android.R.id.text1
                            },
                    0);
            courseList.setAdapter(courseAdapter);
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
    public void onDestroy()
    {
        super.onDestroy();
        cursor.close();
        database.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses,
                menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(final ListView listView,
                                final View     itemView,
                                final int      position,
                                final long     id)
    {
        final Intent intent;

        intent = new Intent(this, CourseActivity.class);
        intent.putExtra(CourseActivity.COURSE_INDEX, (int)id);
        startActivity(intent);
    }
}
