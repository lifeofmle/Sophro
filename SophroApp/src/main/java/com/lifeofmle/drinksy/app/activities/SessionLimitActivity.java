package com.lifeofmle.drinksy.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.common.Session;
import com.lifeofmle.drinksy.app.utilities.PreferencesManager;

import java.io.Serializable;

public class SessionLimitActivity extends Activity {

    private Session currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_limit);

        getActionBar().setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();
        Serializable intentSession = intent.getSerializableExtra("session");
        if (intentSession != null){
            currentSession = (Session)intentSession;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.session_limit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void onAddLimit(View view){
        if (currentSession == null){
            return;
        }

        currentSession.setMaximumCount(currentSession.getMaximumCount()+1);

        updateView(currentSession);
    }

    public void onSubtractLimit(View view){
        if (currentSession == null){
            return;
        }

        currentSession.setMaximumCount(currentSession.getMaximumCount()-1);

        updateView(currentSession);
    }

    public void executeProceed(View view){
        Intent intent = new Intent(this, SessionActivity.class);

        intent.putExtra(IntentKeys.SESSION, currentSession);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
    }

    private void updateView(Session session){
        TextView textView = (TextView) findViewById(R.id.label_limitCount);
        textView.setText(String.valueOf(session.getMaximumCount()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("Session Limit", "onResume Session");

        Object objSession = PreferencesManager.getPreferences(this, PreferenceKeys.CURRENT_SESSION, Session.class);
        if (objSession != null)
        {
            currentSession = (Session)objSession;

            // Populate view
            updateView(currentSession);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("Session Limit", "onPause Session");

        if (currentSession != null){
            PreferencesManager.setPreferences(this, PreferenceKeys.CURRENT_SESSION, currentSession);
        }
    }
}
