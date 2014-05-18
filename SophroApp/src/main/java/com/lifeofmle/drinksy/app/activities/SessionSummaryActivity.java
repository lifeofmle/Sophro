package com.lifeofmle.drinksy.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.adapters.SessionItemAdapter;
import com.lifeofmle.drinksy.app.common.Session;
import com.lifeofmle.drinksy.app.common.SessionItem;
import com.lifeofmle.drinksy.app.common.SessionItemType;
import com.lifeofmle.drinksy.app.utilities.DateUtility;
import com.lifeofmle.drinksy.app.utilities.PreferencesManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SessionSummaryActivity extends Activity {

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);

        getActionBar().setDisplayShowHomeEnabled(false);

        Intent intent = getIntent();
        Serializable intentSession = intent.getSerializableExtra(IntentKeys.SESSION_SUMMARY);
        if (intentSession != null){
            session = (Session)intentSession;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.session_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        switch(id){
            case R.id.action_share:
                onShare();
                return true;
            case R.id.action_delete:
                onDeleteSession();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session == null){
            Object objSession = PreferencesManager.getPreferences(this, PreferenceKeys.SUMMARY_SESSION, Session.class);
            if (objSession != null)
            {
                session = (Session)objSession;
            }
        }

        // Populate view
        updateView(session);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (session != null){
            PreferencesManager.setPreferences(this, PreferenceKeys.SUMMARY_SESSION, session);
        }
    }

    private void updateView(Session session){

        // Populate the summary area
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.TIME_FORMAT);

        TextView textView_startdate = (TextView) findViewById(R.id.label_sessionStartTime);
        textView_startdate.setText(formatter.format(session.getStartDate()));

        TextView textView_enddate = (TextView) findViewById(R.id.label_sessionEndTime);
        textView_enddate.setText(formatter.format(session.getEndDate()));

        SimpleDateFormat dayFormat = new SimpleDateFormat(DateUtility.DAY_FORMAT);
        TextView textView_day = (TextView) findViewById(R.id.label_day);
        textView_day.setText(dayFormat.format(session.getStartDate()));

        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtility.DATE_SHORT);
        TextView textView_date = (TextView) findViewById(R.id.label_date);
        textView_date.setText(dateFormat.format(session.getStartDate()));

        LinearLayout sessionStatus = (LinearLayout) findViewById(R.id.panel_sessionStatus);
        ImageView sessionStatusIcon = (ImageView) findViewById(R.id.icon_sessionStatusIcon);

        if (session.isOverlimit()){
            sessionStatusIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_error));
            sessionStatus.setBackgroundColor(getResources().getColor(R.color.OverlimitBackgroundColor));
        }else{
            sessionStatusIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good));
            sessionStatus.setBackgroundColor(getResources().getColor(R.color.OkColor));
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


        // Get listview
        ListView listViewActivities = (ListView)findViewById(R.id.listview_summaryactivity);

        ArrayList<SessionItem> sessionItems = new ArrayList<SessionItem>(session.getItems());

        SessionItemAdapter adapter = new SessionItemAdapter(this, R.layout.layout_session_item, sessionItems);
        listViewActivities.setEmptyView(findViewById(R.id.layout_empty_sessions));
        listViewActivities.setAdapter(adapter);
    }

    private void onDeleteSession(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IntentKeys.DELETE_SESSION, session);
        startActivity(intent);
    }

    private void onShare(){

        String filename = "drinksy_screenshot.jpg";

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.w("Session Summary", "Can't open external storage");
            return;
        }

        File screenshot = getScreenshot(filename);

        Uri uri = Uri.parse(screenshot.getAbsolutePath());

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setData(uri);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(sharingIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(Intent.createChooser(sharingIntent, "Share Image"));
    }

    public File getScreenshot(String filename){

        View view =  findViewById(R.id.summary_layout);
        view.getRootView();

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);

        Bitmap b = view.getDrawingCache();

        view.setDrawingCacheEnabled(false );

        File directory = getExternalFilesDir("images");

        if (!directory.exists()){
            directory.mkdirs();
        }
        
        boolean canWrite = directory.canWrite();

        File myPath = new File(directory, filename);

        try {
            FileOutputStream fos = new FileOutputStream(myPath, false);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return myPath;
    }
}
