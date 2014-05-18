package com.lifeofmle.drinksy.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.common.Session;
import com.lifeofmle.drinksy.app.common.SessionItem;
import com.lifeofmle.drinksy.app.common.SessionItemType;
import com.lifeofmle.drinksy.app.utilities.DateUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mle on 2014-04-29.
 */
public class SessionAdapter extends ArrayAdapter<Session> {
    private final Context context;
    private final ArrayList<Session>  values;

    private LayoutInflater inflater;

    public SessionAdapter(Context context, int resourceId, ArrayList<Session> items){
        super(context, resourceId, items);

        this.context = context;
        this.values = items;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        Session session = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.layout_session, null);

            SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.TIME_FORMAT);

            TextView textView_startdate = (TextView) convertView.findViewById(R.id.label_sessionStartTime);
            textView_startdate.setText(formatter.format(session.getStartDate()));

            TextView textView_enddate = (TextView) convertView.findViewById(R.id.label_sessionEndTime);
            textView_enddate.setText(formatter.format(session.getEndDate()));

            SimpleDateFormat dayFormat = new SimpleDateFormat(DateUtility.DAY_FORMAT);
            TextView textView_day = (TextView) convertView.findViewById(R.id.label_day);
            textView_day.setText(dayFormat.format(session.getStartDate()));

            SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtility.DATE_SHORT);
            TextView textView_date = (TextView) convertView.findViewById(R.id.label_date);
            textView_date.setText(dateFormat.format(session.getStartDate()));

            LinearLayout sessionStatus = (LinearLayout) convertView.findViewById(R.id.panel_sessionStatus);
            ImageView sessionStatusIcon = (ImageView) convertView.findViewById(R.id.icon_sessionStatusIcon);

            if (session.isOverlimit()){
                sessionStatusIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_action_error));
                sessionStatus.setBackgroundColor(convertView.getResources().getColor(R.color.OverlimitBackgroundColor));
            }else{
                sessionStatusIcon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_action_good));
                sessionStatus.setBackgroundColor(convertView.getResources().getColor(R.color.OkColor));
            }

            TextView textView_limit = (TextView) convertView.findViewById(R.id.label_sessionLimit);
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

            TextView textView_drink = (TextView) convertView.findViewById(R.id.label_sessionDrinkCount);
            textView_drink.setText(String.valueOf(drinkCount));

            TextView textView_water = (TextView) convertView.findViewById(R.id.label_sessionWaterCount);
            textView_water.setText(String.valueOf(waterCount));

            holder.view = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }
}
