package co.edu.udea.cmovil.gr04.yamba;

import com.thenewcircle.yamba.client.YambaClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends AppCompatActivity {

    private static final String TAG = StatusActivity.class.getSimpleName();
    private Button mButtonTweet;
    private EditText mTextStatus;
    private TextView mTextCount;
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        mButtonTweet = (Button) findViewById(R.id.status_button_tweet);
        mTextStatus = (EditText) findViewById(R.id.status_text);
        mTextCount = (TextView) findViewById(R.id.status_text_count);
        mTextCount.setText(Integer.toString(140));
        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        mButtonTweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String status = mTextStatus.getText().toString();
                PostTask postTask = new PostTask();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mTextStatus.getWindowToken(), 0);
                postTask.execute(status);
                Log.d(TAG, "onClicked");
            }

        });

        mTextStatus.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                int count = 140 - s.length();
                mTextCount.setText(Integer.toString(count));

                if (count < 50) {
                    mTextCount.setTextColor(Color.RED);
                } else {
                    mTextCount.setTextColor(mDefaultColor);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });

        Log.d(TAG, "onCreated");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
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

    class PostTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(StatusActivity.this, "Posting",
                    "Please wait...");
            progress.setCancelable(true);
        }

        // Executes on a non-UI thread
        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(StatusActivity.this);
                String username = prefs.getString("username", "student");
                String password = prefs.getString("password", "password");

                YambaClient cloud = new YambaClient(username, password);
                cloud.postStatus(params[0]);

                Log.d(TAG, "Successfully posted to the cloud: " + params[0]);
                return "Successfully posted";
            } catch (Exception e) {
                Log.e(TAG, "Failed to post to the cloud", e);
                e.printStackTrace();
                return "Failed to post";
            }
        }

        // Called after doInBackground() on UI thread
        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            if (this != null && result != null)
                Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
                        .show();
        }

    }
}
