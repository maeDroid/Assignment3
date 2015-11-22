package mae.comp3717.bcit.ca.assignment3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class TopLevelActivity extends AppCompatActivity
{
    public static final String STUDENT_INFO = "student_info";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final ListView optionsView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);

        Intent intent = getIntent();

        String student_info = intent.getStringExtra(STUDENT_INFO);
        System.out.println(" ---------------- " + student_info);
        Toast.makeText(this, student_info, Toast.LENGTH_LONG);
        optionsView = (ListView)findViewById(R.id.list_options);
        optionsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent,
                                    final View           itemView,
                                    final int            position,
                                    final long           id)
            {
                final Class  clazz;
                final Intent intent;

                switch(position)
                {
                    case 0:
                    {
                        clazz = CoursesActivity.class;
                        break;
                    }
                    case 1:
                    {
                        clazz = TasksActivity.class;
                        break;
                    }
                    case 2:
                    {
                        clazz = SetMatesActivity.class;
                        break;
                    }
                    default:
                    {
                        clazz = null;
                    }
                }

                intent = new Intent(TopLevelActivity.this, clazz);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_level,
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
}
