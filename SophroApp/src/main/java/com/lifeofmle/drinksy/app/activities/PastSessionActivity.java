package com.lifeofmle.drinksy.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.adapters.SessionAdapter;
import com.lifeofmle.drinksy.app.common.Session;
import com.lifeofmle.drinksy.app.common.SessionRepository;
import com.lifeofmle.drinksy.app.utilities.PreferencesManager;

import java.io.Serializable;
import java.util.List;

public class PastSessionActivity extends Activity {

    private SessionRepository sessionRepository;
    private SessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_session);

        getActionBar().setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();

        Object sessionRepositoryObject = intent.getSerializableExtra(IntentKeys.SESSION_REPOSITORY);
        if (sessionRepositoryObject == null){
            sessionRepositoryObject = PreferencesManager.getPreferences(this, PreferenceKeys.SESSION_REPOSITORY, SessionRepository.class);
        }

        if (sessionRepositoryObject != null)
        {
            sessionRepository = (SessionRepository)sessionRepositoryObject;
        }

        Serializable intentSession = intent.getSerializableExtra(IntentKeys.DELETE_SESSION);

        if (intentSession != null){
            deleteSession((Session)intentSession);

        }else{
            intentSession = intent.getSerializableExtra(IntentKeys.END_SESSION);
            if (intentSession != null){

                // End session
                Session oldSession = (Session)intentSession;
                oldSession.endSession();
            }
        }

        loadSessions();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.past_session, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save repository state
        PreferencesManager.setPreferences(this, PreferenceKeys.SESSION_REPOSITORY, sessionRepository);
    }

    private void deleteSession(Session session){

        // Remove session
        List<Session> sessions = sessionRepository.getSessions();
        int index = sessions.indexOf(session);
        if (index >= 0){
            sessions.remove(index);
        }

        Toast toast = Toast.makeText(this, R.string.label_session_deleted, Toast.LENGTH_SHORT);
        toast.show();

        loadSessions();
    }

    private void loadSessions(){
        // Get listview
        ListView listViewSessions = (ListView)findViewById(R.id.past_activities);

        if (adapter == null){
            adapter = new SessionAdapter(this, R.layout.layout_session, sessionRepository.getSessions());
        }

        View emptyView = findViewById(R.id.layout_empty_sessions);
        listViewSessions.setEmptyView(emptyView);
        listViewSessions.setAdapter(adapter);

        listViewSessions.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Object item = parent.getItemAtPosition(position);

                Session pastSession = (Session)item;

                Intent intent = new Intent(getBaseContext(), SessionSummaryActivity.class);
                intent.putExtra(IntentKeys.SESSION_SUMMARY, pastSession);
                startActivity(intent);
            }
        });


    }
}
