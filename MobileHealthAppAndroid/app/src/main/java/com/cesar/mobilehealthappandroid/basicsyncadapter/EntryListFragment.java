/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cesar.mobilehealthappandroid.basicsyncadapter;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesar.mobilehealthappandroid.Globals;
import com.cesar.mobilehealthappandroid.Message;
import com.cesar.mobilehealthappandroid.R;
import com.cesar.mobilehealthappandroid.basicsyncadapter.provider.MessageContract;
import com.cesar.mobilehealthappandroid.common.accounts.GenericAccountService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * List fragment containing a list of Atom entry objects (articles) stored in the local database.
 * <p>
 * <p>Database access is mediated by a content provider, specified in
 * {@link com.cesar.mobilehealthappandroid.basicsyncadapter.provider.FeedProvider}. This content
 * provider is
 * automatically populated by  {@link SyncService}.
 * <p>
 * <p>Selecting an item from the displayed list displays the article in the default browser.
 * <p>
 * <p>If the content provider doesn't return any data, then the first sync hasn't run yet. This sync
 * adapter assumes data exists in the provider once a sync has run. If your app doesn't work like
 * this, you should add a flag that notes if a sync has run, so you can differentiate between "no
 * available data" and "no initial sync", and display this in the UI.
 * <p>
 * <p>The ActionBar displays a "Refresh" button. When the user clicks "Refresh", the sync adapter
 * runs immediately. An indeterminate ProgressBar element is displayed, showing that the sync is
 * occurring.
 */
public class EntryListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "EntryListFragment";

    /**
     * Cursor adapter for controlling ListView results.
     */
    private SimpleCursorAdapter mAdapter;

    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     * <p>
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;

    /**
     * Options menu used to populate ActionBar.
     */
    private Menu mOptionsMenu;

    /**
     * Projection for querying the content provider.
     */
    private static final String[] PROJECTION = new String[]{
            MessageContract.Entry._ID,
            MessageContract.Entry.COLUMN_NAME_SUBJECT,
            MessageContract.Entry.COLUMN_NAME_MSG,
            MessageContract.Entry.COLUMN_NAME_DATE_TIME,
            MessageContract.Entry.COLUMN_NAME_ISSUER,
            MessageContract.Entry.COLUMN_NAME_RECIPIENT,
            MessageContract.Entry.COLUMN_NAME_ISSUER_NAME,
            MessageContract.Entry.COLUMN_NAME_ISSUER_IMG,

    };

    // Column indexes. The index of a column in the Cursor is the same as its relative position in
    // the projection.
    /**
     * Column index for _ID
     */
    private static final int COLUMN_ID = 0;
    /**
     * Column index for title
     */
    private static final int COLUMN_TITLE = 1;
    /**
     * Column index for link
     */
    private static final int COLUMN_MSG = 2;
    /**
     * Column index for published
     */
    private static final int COLUMN_DATE = 3;
    private static final int COLUMN_ISSUER = 4;
    private static final int COLUMN_RECIPIENT = 5;
    private static final int COLUMN_ISSUER_NAME = 6;
    private static final int COLUMN_ISSUER_IMG = 7;

    /**
     * List of Cursor columns to read from when preparing an adapter to populate the ListView.
     */
    private static final String[] FROM_COLUMNS = new String[]{
            MessageContract.Entry.COLUMN_NAME_ISSUER_IMG,
            MessageContract.Entry.COLUMN_NAME_ISSUER_NAME,
            MessageContract.Entry.COLUMN_NAME_SUBJECT,
            MessageContract.Entry.COLUMN_NAME_DATE_TIME
    };

    /**
     * List of Views which will be populated by Cursor data.
     */
    private static final int[] TO_FIELDS = new int[]{
            R.id.icon,
            R.id.issuer,
            R.id.subtitle,
            R.id.date_time};

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EntryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Create SyncAccount at launch, if needed.
     * <p>
     * <p>This will create a new account with the system for our application, register our
     * {@link SyncService} with it, and establish a sync schedule.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Create account, if needed
        SyncUtils.CreateSyncAccount(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new SimpleCursorAdapter(
                getActivity(),       // Current context
                R.layout.list_message,  // Layout for individual rows
                null,                // Cursor
                FROM_COLUMNS,        // Cursor columns to use
                TO_FIELDS,           // Layout fields to use
                0                    // No flags
        );
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if (i == COLUMN_DATE) {
                    ((TextView) view).setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Timestamp(cursor.getLong(i))));
                    return true;
                } else if (i == COLUMN_ISSUER_IMG) {
                    Bitmap bmp = Globals.getInstance().convertBlobToBitmap(cursor.getBlob(i));
                    if (bmp != null) {
                        ((ImageView) view).setImageBitmap(bmp);
                    }
                    return true;
                } else {
                    // Let SimpleCursorAdapter handle other fields automatically
                    return false;
                }
            }
        });
        setListAdapter(mAdapter);
        if (Globals.getInstance().isConfiguredSsyncUser()) {
            setEmptyText(getText(R.string.loading));
        } else {
            setEmptyText(Globals.getInstance().MessageUnconfiguredUserSync);
        }
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    /**
     * Query the content provider for data.
     * <p>
     * <p>Loaders do queries in a background thread. They also provide a ContentObserver that is
     * triggered when data in the content provider changes. When the sync adapter updates the
     * content provider, the ContentObserver responds by resetting the loader and then reloading
     * it.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // We only have one loader, so we can ignore the value of i.
        // (It'll be '0', as set in onCreate().)
        return new CursorLoader(getActivity(),  // Context
                MessageContract.Entry.CONTENT_URI, // URI
                PROJECTION,                // Projection
                null,                           // Selection
                null,                           // Selection args
                MessageContract.Entry.COLUMN_NAME_DATE_TIME + " desc"); // Sort
    }

    /**
     * Move the Cursor returned by the query into the ListView adapter. This refreshes the existing
     * UI with the data in the Cursor.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    /**
     * Called when the ContentObserver defined for the content provider detects that data has
     * changed. The ContentObserver resets the loader, and then re-runs the loader. In the adapter,
     * set the Cursor value to null. This removes the reference to the Cursor, allowing it to be
     * garbage-collected.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    /**
     * Create the ActionBar.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mOptionsMenu = menu;
        inflater.inflate(R.menu.menu_entry_list, menu);
    }

    /**
     * Respond to user gestures on the ActionBar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                SyncUtils.TriggerRefresh();
                return true;
            case R.id.menu_previous:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_delete_all_msg:
                deleteMessage(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMessage(long id) {
        try {

            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
            if (id > 0) {
                String[] args = new String[]{String.valueOf(id)};
                batch.add(ContentProviderOperation.newDelete(MessageContract.Entry.CONTENT_URI).withSelection(MessageContract.Entry._ID + "=?", args).build());
            } else {
                batch.add(ContentProviderOperation.newDelete(MessageContract.Entry.CONTENT_URI).build());
            }

            getContext().getContentResolver().applyBatch(MessageContract.CONTENT_AUTHORITY, batch);
            getContext().getContentResolver().notifyChange(
                    MessageContract.Entry.CONTENT_URI,
                    null,
                    false);
        } catch (Exception ex) {
            Globals.getInstance().makeToast(getContext(), ex.getMessage(), Toast.LENGTH_LONG);
        }
    }

    /**
     * Load an article in the default browser when selected by the user.
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Get a URI for the selected item, then start an Activity that displays the URI. Any
        // Activity that filters for ACTION_VIEW and a URI can accept this. In most cases, this will
        // be a browser.

        // Get the item at the selected position, in the form of a Cursor.
        Cursor c = (Cursor) mAdapter.getItem(position);
        // Get the link to the article represented by the item.
        String msg = c.getString(COLUMN_MSG);
        if (msg == null) {
            Log.e(TAG, "Attempt to launch entry with null msg");
            return;
        }

        Log.i(TAG, "Opening msg: " + msg);
        Message msg_selected = new Message();
        msg_selected.setId(c.getInt(COLUMN_ID));
        msg_selected.setIssuer(c.getInt(COLUMN_ISSUER));
        msg_selected.setSubject(c.getString(COLUMN_TITLE));
        msg_selected.setRecipient(c.getInt(COLUMN_RECIPIENT));
        msg_selected.setIssuer_name(c.getString(COLUMN_ISSUER_NAME));
        msg_selected.setDate_time(new Timestamp(c.getLong(COLUMN_DATE)));
        msg_selected.setImg(Globals.getInstance().convertBlobToBitmap(c.getBlob(COLUMN_ISSUER_IMG)));
        msg_selected.setMsg(msg);
        Globals.getInstance().setMessageSelected(msg_selected);

        startActivity(new Intent(getActivity().getBaseContext(), DetailMessageActivity.class));

    }

    /**
     * Set the state of the Refresh button. If a sync is active, turn on the ProgressBar widget.
     * Otherwise, turn it off.
     *
     * @param refreshing True if an active sync is occuring, false otherwise
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, MessageContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, MessageContract.CONTENT_AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                final Cursor c = (Cursor) mAdapter.getItem(position);


                AlertDialog confirm = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.delete_question)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMessage(c.getInt(COLUMN_ID));
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();

                confirm.show();

                return true;
            }
        };

        getListView().setOnItemLongClickListener(listener);

    }


}