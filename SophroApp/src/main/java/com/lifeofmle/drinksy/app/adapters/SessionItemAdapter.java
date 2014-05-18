package com.lifeofmle.drinksy.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lifeofmle.drinksy.app.R;
import com.lifeofmle.drinksy.app.common.SessionItem;
import com.lifeofmle.drinksy.app.common.SessionItemType;
import com.lifeofmle.drinksy.app.utilities.DateUtility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by mle on 2014-04-29.
 */
public class SessionItemAdapter extends ArrayAdapter<SessionItem> {
    private final Context context;
    private final ArrayList<SessionItem>  values;
    private LayoutInflater inflater;



    public SessionItemAdapter(Context context, int resourceId, ArrayList<SessionItem> items){
        super(context, resourceId, items);

        this.context = context;
        this.values = items;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        SessionItem sessionItem = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.layout_drink_item, null);

            if (sessionItem.getItemType().equals(SessionItemType.Drink)){

                convertView = inflater.inflate(R.layout.layout_drink_item, null);

                TextView textView = (TextView) convertView.findViewById(R.id.label_drinkItemTime);

                SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.TIME_FORMAT);
                textView.setText(formatter.format(sessionItem.getCreatedDate()));

            } else if (sessionItem.getItemType().equals(SessionItemType.Water)){

                convertView = inflater.inflate(R.layout.layout_water_item, null);

                TextView textView = (TextView) convertView.findViewById(R.id.label_waterItemTime);

                SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.TIME_FORMAT);
                textView.setText(formatter.format(sessionItem.getCreatedDate()));

            } else if (sessionItem.getItemType().equals(SessionItemType.Alert)){
                convertView = inflater.inflate(R.layout.layout_alert_item, null);

                TextView textView_message = (TextView) convertView.findViewById(R.id.label_alertMessage);
                TextView textView = (TextView) convertView.findViewById(R.id.label_alertItemTime);

                SimpleDateFormat formatter = new SimpleDateFormat(DateUtility.TIME_FORMAT);
                textView.setText(formatter.format(sessionItem.getCreatedDate()));

                textView_message.setText(sessionItem.getMessage());
            }

            holder.view = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        return convertView;
    }
}
