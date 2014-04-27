package com.lifeofmle.sophro.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lifeofmle.sophro.app.R;
import com.lifeofmle.sophro.app.common.Session;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.main_layout) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void executeStart(View view) {

        Intent intent = new Intent(this, SessionLimitActivity.class);

        Session session = new Session();

        intent.putExtra(ActivityTopics.StartNewSession, session);

        startActivity(intent);

    }
}