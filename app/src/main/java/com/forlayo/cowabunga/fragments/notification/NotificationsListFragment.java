package com.forlayo.cowabunga.fragments.notification;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forlayo.cowabunga.R;
import com.forlayo.cowabunga.db.NotificationListAdapter;
import com.forlayo.cowabunga.db.NotificationListProvider;


public class NotificationsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    NotificationListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.element_list, container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(0x01, null, this);
        adapter = new NotificationListAdapter(getActivity(),null);
        setListAdapter(adapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), NotificationListProvider.CONTENT_URI, NotificationListProvider.PROYECTION, null, null,NotificationListProvider.DB_TIMESTAMP +" DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(new allButFirstCursor(cursor));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    class allButFirstCursor extends CursorWrapper
    {
        public allButFirstCursor(Cursor cursor) {
            super(cursor);
        }
        @Override
        public final boolean moveToFirst() {
            return moveToPosition(1);
        }

        @Override
        public int getCount() {
            return super.getCount()-1;
        }

        @Override
        public boolean moveToPosition(int position) {
            if ((position+1) >= super.getCount()) {
                return false;
            }

            return super.moveToPosition(position+1);

        }
    }

}
