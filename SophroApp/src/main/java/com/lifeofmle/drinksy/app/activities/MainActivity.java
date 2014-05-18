package com.lifeofmle.drinksy.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.adapters.SessionAdapter;
import com.lifeofmle.drinksy.app.common.Session;
import com.lifeofmle.drinksy.app.common.SessionRepository;
import com.lifeofmle.drinksy.app.utilities.PreferencesManager;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends Activity {

    private Session currentSession;
    private SessionRepository sessionRepository;
    private SessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Object objSessionRepository = PreferencesManager.getPreferences(this, PreferenceKeys.SESSION_REPOSITORY, SessionRepository.class);
        if (objSessionRepository != null) {
            sessionRepository = (SessionRepository) objSessionRepository;
        }

        if (sessionRepository == null){
            sessionRepository = new SessionRepository();
        }

        Intent intent = getIntent();

        boolean startNewSession = false;

        Serializable intentSession = intent.getSerializableExtra(IntentKeys.DELETE_SESSION);

        if (intentSession != null){
            deleteSession((Session)intentSession);

        }else{
            intentSession = intent.getSerializableExtra(IntentKeys.END_SESSION);
            if (intentSession != null){

                // End session
                Session oldSession = (Session)intentSession;
                oldSession.endSession();

                // Save the session
                saveSession(oldSession);

                // Delete old session
                intentSession = null;

                startNewSession = true;
            }else{
                if (intentSession == null){
                    intentSession = intent.getSerializableExtra(IntentKeys.SESSION);
                }
            }
        }

        if (intentSession == null){

            if (!startNewSession){
                Object objSession = PreferencesManager.getPreferences(this, PreferenceKeys.CURRENT_SESSION, Session.class);
                if (objSession != null){
                    currentSession = (Session)objSession;
                }
            }

            if (currentSession == null){
                currentSession = new Session();
            }
        }
        else{
            currentSession = (Session)intentSession;
        }

        loadSessions();
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

        if (currentSession != null){
            currentSession.startSession();
            PreferencesManager.setPreferences(this, PreferenceKeys.CURRENT_SESSION, currentSession);
        }

        Intent intent = new Intent(this, SessionLimitActivity.class);
        intent.putExtra(IntentKeys.SESSION, currentSession);
        startActivity(intent);
    }

    public void executeResume(View view){
        Intent intent = new Intent(this, SessionActivity.class);
        intent.putExtra(IntentKeys.SESSION, currentSession);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("Main", "onResume Session");

        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save session
        PreferencesManager.setPreferences(this, PreferenceKeys.CURRENT_SESSION, currentSession);

        // Save repository state
        PreferencesManager.setPreferences(this, PreferenceKeys.SESSION_REPOSITORY, sessionRepository);
    }

    private void updateView(){

        boolean isNew = currentSession.isNew();

        if (isNew){
            Button startButton = (Button) findViewById(R.id.button_start);
            startButton.setVisibility(View.VISIBLE);

            Button resumeButton = (Button) findViewById(R.id.button_resume);
            resumeButton.setVisibility(View.GONE);

        }else{
            Button startButton = (Button) findViewById(R.id.button_start);
            startButton.setVisibility(View.GONE);

            Button resumeButton = (Button) findViewById(R.id.button_resume);
            resumeButton.setVisibility(View.VISIBLE);
        }
    }

    private void saveSession(Session session){

        // Save session
        List<Session> sessions = sessionRepository.getSessions();
        sessions.add(0, session);
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
