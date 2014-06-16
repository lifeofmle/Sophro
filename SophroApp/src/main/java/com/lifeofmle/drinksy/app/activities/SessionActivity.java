package com.lifeofmle.drinksy.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.adapters.SessionItemAdapter;
import com.lifeofmle.drinksy.app.common.Session;
import com.lifeofmle.drinksy.app.common.SessionItem;
import com.lifeofmle.drinksy.app.common.SessionItemType;
import com.lifeofmle.drinksy.app.components.ProgressWheel;
import com.lifeofmle.drinksy.app.utilities.DateUtility;
import com.lifeofmle.drinksy.app.utilities.PreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SessionActivity extends Activity {

    private SessionItemAdapter adapter;
    private Session session;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_water:
                onAddWaterSelected();
                return true;
            case R.id.action_endSession:
                onEndSessionSelected();
                return true;
            case R.id.action_editLimit:
                onEditLimit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddDrink(View view){

        if (session == null){
            return;
        }

        SessionItem drink = new SessionItem();
        drink.setItemType(SessionItemType.Drink);
        session.addItem(drink);

        if (session.isHasAlert()){
            session.setHasAlert(false);
            vibrate();
            raiseAlert(session.getAlert());
        }

        updateView(session);
    }

    public void onAddSessionItem(SessionItem sessionItem){

        if (session == null){
            return;
        }

        session.addItem(sessionItem);

        updateView(session);
    }

    public void executeSessionSummary(View view){
        Intent intent = new Intent(getBaseContext(), SessionSummaryActivity.class);
        intent.putExtra(IntentKeys.SESSION_SUMMARY, session);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("Session Activity", "onResume Session");

        Object objSession = PreferencesManager.getPreferences(this, PreferenceKeys.CURRENT_SESSION, Session.class);
        if (objSession != null)
        {
            session = (Session)objSession;

            // Populate view
            updateView(session);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (countDownTimer != null){
            countDownTimer.cancel();
        }

        Log.i("Session Activity", "onPause Session");

        if (session != null){
            PreferencesManager.setPreferences(this, PreferenceKeys.CURRENT_SESSION, session);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IntentKeys.SESSION, session);
        startActivity(intent);
    }

    private void updateView(Session session){

        setTitle(session.getSessionLabel());

        SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.TIME_FORMAT);
        String startStatus = String.format("Started at %s", formatter.format(session.getStartDate()));

        TextView textView_status = (TextView) findViewById(R.id.label_start);
        textView_status.setText(startStatus);

        TextView textView = (TextView) findViewById(R.id.label_remainingCount);
        textView.setText(String.valueOf(session.getRemainingCount()));

        // Get listview
        ListView listViewActivities = (ListView)findViewById(R.id.listview_activity);

        ArrayList<SessionItem> sessionItems = new ArrayList<SessionItem>(session.getItems());

        adapter = new SessionItemAdapter(this, R.layout.layout_session_item, sessionItems);

        View emptyView = findViewById(R.id.layout_empty_activities);
        listViewActivities.setEmptyView(emptyView);
        listViewActivities.setAdapter(adapter);

        ProgressBar progressBarLimit = (ProgressBar) findViewById(R.id.progressBar_limit);
        progressBarLimit.setMax(session.getMaximumCount());

        LinearLayout panel_overlimit = (LinearLayout) findViewById(R.id.panel_overlimit);

        int remainingCount = session.getRemainingCount();
        if (remainingCount >= 0){
            progressBarLimit.setProgress(session.getRemainingCount());
            panel_overlimit.setVisibility(View.GONE);
        }else{
            progressBarLimit.setProgress(0);
            panel_overlimit.setVisibility(View.VISIBLE);
        }

        TextView textView_limit = (TextView) findViewById(R.id.label_sessionLimit);
        textView_limit.setText(String.valueOf(session.getMaximumCount()));

        List<SessionItem> items = session.getItems();
        int drinkCount = 0;
        int waterCount = 0;
        if (items != null){
            for (SessionItem item : items){
                if (item.getItemType().equals(SessionItemType.Drink)){
                    drinkCount++;
                } else if (item.getItemType().equals(SessionItemType.Water)){
                    waterCount++;
                }
            }
        }

        TextView textView_drink = (TextView) findViewById(R.id.label_sessionDrinkCount);
        textView_drink.setText(String.valueOf(drinkCount));

        TextView textView_water = (TextView) findViewById(R.id.label_sessionWaterCount);
        textView_water.setText(String.valueOf(waterCount));

        updateSessionStatus();

        updateCountdown();
    }

    private void updateSessionStatus(){
        if (session == null){
            return;
        }

        session.updateNextDrinkTime();

        ProgressWheel progressDuration = (ProgressWheel) findViewById(R.id.progressWheel_timer);
        int progress = (int) (((double)session.getNextDrinkProgress()/(double)100) * 360);
        progressDuration.setProgress(progress);

        TextView textView_timeLeft = (TextView) findViewById(R.id.label_remainingTime);
        textView_timeLeft.setText(session.getNextDrinkTimeMessage());

        TextView textView_timeLeftUnit = (TextView) findViewById(R.id.label_remainingTimeUnit);
        textView_timeLeftUnit.setText(session.getNextDrinkTimeUnit());

        int barColor = progressDuration.getBarColor();
        int newColor = barColor;

        if (session.getNextDrinkTimeMessage().equals("GO")){
            newColor = getResources().getColor(R.color.OkColor);
            progressDuration.setBarColor(newColor);
            textView_timeLeftUnit.setVisibility(View.GONE);

        } else {
            newColor = getResources().getColor(R.color.ProgressColor);
            textView_timeLeftUnit.setVisibility(View.VISIBLE);
        }

        if (barColor != newColor){
            progressDuration.setBarColor(newColor);
            progressDuration.refresh();
        }
    }

    private void updateCountdown(){
        if (session == null){
            return;
        }

        if (session.getNextDrinkDurationInMillis() > 0){
            countDownTimer = new CountDownTimer(session.getNextDrinkDurationInMillis(), 1000) {
                public void onTick(long millisUntilFinished) {
                    updateTimer();
                }
                public void onFinish() {
                    timerCompleted();
                }
            }.start();
        }
    }

    private void updateTimer(){
        if (session == null){
            return;
        }

        updateSessionStatus();

        Log.d("Session Activity", "timer ticked");
    }

    private void timerCompleted(){

        updateSessionStatus();

        Log.i("Session Activity", "timer completed");
    }

    private void raiseAlert(final SessionItem alert){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(alert.getMessage())
                .setPositiveButton(R.string.dialog_confirm_overlimit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        onAddSessionItem(alert);
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();

        builder.show();
    }

    private void onAddWaterSelected(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_add_water_prompt)
                .setPositiveButton(R.string.dialog_confirm_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SessionItem water = new SessionItem();
                        water.setItemType(SessionItemType.Water);
                        onAddSessionItem(water);
                    }
                })
                .setNegativeButton(R.string.dialog_confirm_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // do nothing
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();

        builder.show();

    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(1000);
    }

    private void onEndSessionSelected(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_end_session_prompt)
                .setPositiveButton(R.string.dialog_confirm_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onEndSession();

                    }
                })
                .setNegativeButton(R.string.dialog_confirm_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // do nothing
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();

        builder.show();
    }

    private void onEndSession(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IntentKeys.END_SESSION, session);
        startActivity(intent);
    }

    private void onEditLimit(){
        Intent intent = new Intent(this, SessionLimitActivity.class);
        startActivity(intent);
    }
}
