package mae.comp3717.bcit.ca.assignment3;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    private String urlString;
    private String apiString;
    private TextView tv_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String apiKey           = "V1O8PT16x-6LcZMXTnREH87LNi1m0pwC";
        final String baseURL          = "https://api.mongolab.com/api/1/databases/";
        final String databaseName     = "asn3-students";
        final String collectionName   = "students";
        final String collectionString = "/collections/" + collectionName + "?q=";

        urlString = baseURL + databaseName + collectionString;
        apiString = "&apiKey=" + apiKey;
        tv_msg    = (TextView) findViewById(R.id.output_msg);

        Log.d("urlString", urlString);
        Log.d("apiString", apiString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  onClick handler for the Sign In button.
     *
     *  If the EditText field is empty, it outputs an appropriate message.
     *  If it is non-emtpy, it checks if the provided value is a valid ID.
     *  If it is a valid ID, it checks if the ID is in the database.
     *  If it is in the database, it saves the ID into a JSON Object and calls
     *  RetrieveDataTask, passing in the ID.
     *
     *  Launches the TopLevelActivity when all is done.
     */
    public void signin(final View view)
    {
        final EditText et_id     = (EditText) findViewById(R.id.et_ID);
        String id                = et_id.getText().toString().trim();
        tv_msg.setText("");

        InputMethodManager inputMgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (id.isEmpty())
        {
            tv_msg.setText("Please type in your ID");
        } else {
            if (valid_ID(id.toCharArray()) && ID_exists(id))
            {
                String bcit_id = "";

                try
                {
                    JSONObject json = new JSONObject();
                    json.put("_id", id);
                    bcit_id = json.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new RetrieveDataTask().execute(bcit_id);
                Intent intent = new Intent(MainActivity.this, TopLevelActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     *  Validates the ID.
     *
     *  Returns true if the ID is valid; false otherwise.
     *
     *  An ID is valid only if it is 9 characters long and starts with 'A'
     *  followed by 8 digits.
     */
    boolean valid_ID(char[] id)
    {
        if (id.length != 9 || !(id[0] == 'A'))
        {
            tv_msg.setText("ID must follow this pattern: A followed by 8 digits");
            return false;
        }

        for (int i = 1; i < id.length; i++)
        {
            if (!Character.isDigit(id[i]))
            {
                tv_msg.setText("ID must follow this pattern: A followed by 8 digits");
                return false;
            }
        }

        return true;
    }

    /**
     *  Checks if ID exists in the Mongo database.
     *
     *  Normally this method would be called after connecting to the database,
     *  but within the scope of this assignment, it just checks if it is one
     *  of two possible ID's:  A00000000 or A00000001
     */
    boolean ID_exists(String id)
    {
        if ( !id.equals("A00000000") && !id.equals("A00000001") )
        {
            tv_msg.setText("ID does not exist in database");
            return false;
        }
        return true;
    }

    /**
     *  Connects to the Mongo database 'asn3-students' and retrieves
     *  a specific document in the 'students' collection.
     *
     *  After parsing through the retrieved document, it inserts the
     *  relevant info (course number and course description) into an
     *  SQLite database.
     */
    class RetrieveDataTask extends AsyncTask<String, String, String>
    {
        private String studentData;
        private InputStream in = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... params)
        {
            String studentJson = params[0];

            try
            {
                URL url           = new URL(urlString + studentJson + apiString);
                urlConnection     = (HttpURLConnection) url.openConnection();
                in                = new BufferedInputStream(urlConnection.getInputStream());
                StringBuilder sb  = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }

                studentData = sb.toString();
                Log.d("Student Info", studentData);

            } catch (IOException e) {
            } finally {
                urlConnection.disconnect();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String str)
        {
            final BCITDatabaseHelper helper;
            final SQLiteDatabase database;

            try
            {
                helper   = new BCITDatabaseHelper(getApplicationContext());
                database = helper.getWritableDatabase();
                database.execSQL("DELETE FROM " + BCITDatabaseHelper.COURSE_TABLE);

                JSONArray jsonArray    = new JSONArray(studentData);
                JSONObject jsonObj     = jsonArray.getJSONObject(0);
                String coursesStr      = jsonObj.getString("courses");
                JSONArray coursesArray = new JSONArray(coursesStr);

                for (int i = 0; i < coursesArray.length(); i++)
                {
                    JSONArray course   = coursesArray.getJSONArray(i);
                    Log.d("Course #" + (i + 1), course.getString(0) + ": " + course.getString(1));
                    helper.insertCourse(database, course.getString(0), course.getString(1));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
