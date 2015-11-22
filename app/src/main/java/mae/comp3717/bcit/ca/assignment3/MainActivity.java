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
import android.webkit.WebView;
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

public class MainActivity extends AppCompatActivity {

    private String apiKey = "V1O8PT16x-6LcZMXTnREH87LNi1m0pwC";
    private String apiString = "&apiKey=" + apiKey;
    private String baseURL = "https://api.mongolab.com/api/1/databases/";
    private String database = "asn3-students";
    private String collection = "/collections/students?q=";
    private String urlString = baseURL + database + collection;
    private WebView webView;
    private TextView tv_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        tv_msg  = (TextView) findViewById(R.id.output_msg);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        final BCITDatabaseHelper helper;
//        final SQLiteDatabase database;
//
//        helper   = new BCITDatabaseHelper(getApplicationContext());
//        database = helper.getWritableDatabase();
//
//        System.out.println("@@@ MAIN_ACTIVITY ON RESUME");
//        database.execSQL("DROP TABLE IF EXISTS " + BCITDatabaseHelper.COURSE_TABLE);
//        System.out.println("!!! DROP TABLE");
//        database.execSQL("CREATE TABLE IF NOT EXISTS " + BCITDatabaseHelper.COURSE_TABLE + " (" +
//                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                BCITDatabaseHelper.COURSE_NAME + " TEXT, " +
//                BCITDatabaseHelper.COURSE_DESCRIPTION + " TEXT)");
//        System.out.println("!!! CREATE TABLE");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void signin(final View view) {

        final EditText et_id     = (EditText) findViewById(R.id.et_ID);
        //TextView tv_msg          = (TextView) findViewById(R.id.output_msg);
        String id                = et_id.getText().toString().trim();
        tv_msg.setText("");

        InputMethodManager inputMgr = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (id.isEmpty())  {
            tv_msg.setText("Please type in your ID");
        } else {
            if (valid_ID(id.toCharArray())) {
                loadStudent(id);
            }
        }
    }

    boolean valid_ID(char[] id) {

        TextView tv_msg = (TextView) findViewById(R.id.output_msg);

        if (id.length != 9 || !(id[0] == 'A')) {
            tv_msg.setText("ID must follow this pattern: A followed by 8 digits");
            return false;
        }

        for (int i = 1; i < id.length; i++) {
            if (!Character.isDigit(id[i])) {
                tv_msg.setText("ID must follow this pattern: A followed by 8 digits");
                return false;
            }
        }

        return true;
    }

    public void loadStudent(String id) {

        String JSON_id = "";
        try {
            JSONObject json = new JSONObject();
            json.put("_id", id);
            JSON_id = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(urlString + JSON_id + apiString);

        new RetrieveDataTask().execute(JSON_id);
    }

    class RetrieveDataTask extends AsyncTask<String, String, String> {

        private String studentData;
        private InputStream in = null;
        private HttpURLConnection urlConnection = null;
        //ProgressDialog progress = null;
        @Override
        protected String doInBackground(String... params) {
            String studentJson = params[0];
            //System.out.println("************* studentJson: " + studentJson);
            //System.out.println("************* url: " + urlString + studentJson + apiString);

            try {
                URL url = new URL(urlString + studentJson + apiString);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                StringBuilder sb = new StringBuilder();
                BufferedReader rd = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = rd.readLine()) != null) {
                    System.out.println("************* line: " + line);
                    sb.append(line);
                }
                studentData = sb.toString();
                System.out.println("~~~~~~~~~ studentData: " + studentData);
                Log.d("Student data", studentData);
                System.out.println("______studentData.length = " + studentData.length() );



            } catch (IOException e) {
            } finally{
                urlConnection.disconnect();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String str) {
            final BCITDatabaseHelper helper;
            final SQLiteDatabase database;

            if (studentData.trim().equals("[  ]")) {
                tv_msg.setText("ID does not exist in database");
                return;
            }

            try {
                helper   = new BCITDatabaseHelper(getApplicationContext());
                database = helper.getWritableDatabase();
                database.execSQL("DELETE FROM " + BCITDatabaseHelper.COURSE_TABLE);
                System.out.println("@@@ DELETING ALL ROWS FROM TABLE");

                System.out.println("@@@ MAIN_ACTIVITY ON POST_EXECUTE");

                JSONArray jsonArray = new JSONArray(studentData);
                JSONObject jsonObj = jsonArray.getJSONObject(0);
                String coursesStr = jsonObj.getString("courses");
                JSONArray array = new JSONArray(coursesStr);

                for (int i = 0; i < array.length(); i++) {
                    JSONArray course = array.getJSONArray(i);
                    System.out.println(course.getString(0) + ": " + course.getString(1));
                    helper.insertCourse(database, course.getString(0), course.getString(1));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(MainActivity.this, TopLevelActivity.class);
            startActivity(intent);
        }

    }

}
