package com.forlayo.cowabunga.db;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.forlayo.cowabunga.R;


public class NotificationListAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    public static class ViewHolder {
        ImageView app_icon;
        TextView app_name;
        TextView ticker_text;
    }

    public NotificationListAdapter(Context context, Cursor c) {
        super(context, c, 0);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.notification_row, parent, false);
        holder.app_name = (TextView) v.findViewById(R.id.notification_row_appname);
        holder.app_icon = (ImageView) v.findViewById(R.id.notification_row_icon);
        holder.ticker_text = (TextView) v.findViewById(R.id.notification_row_text);
        v.setTag(holder);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.app_name.setText( cursor.getString(cursor.getColumnIndex(NotificationListProvider.DB_APP_NAME)) );
        holder.ticker_text.setText(cursor.getString(cursor.getColumnIndex(NotificationListProvider.DB_TICKER_TEXT)));
        String package_name = cursor.getString(cursor.getColumnIndex(NotificationListProvider.DB_PACKAGE_NAME));
        try {
            holder.app_icon.setImageDrawable(mContext.getPackageManager().getApplicationIcon(package_name));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

}
