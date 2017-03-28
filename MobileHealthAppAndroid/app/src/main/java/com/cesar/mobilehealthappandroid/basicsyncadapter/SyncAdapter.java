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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cesar.mobilehealthappandroid.Globals;
import com.cesar.mobilehealthappandroid.Message;
import com.cesar.mobilehealthappandroid.R;
import com.cesar.mobilehealthappandroid.basicsyncadapter.provider.MessageContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    private static final String[] PROJECTION = new String[] {
            MessageContract.Entry._ID,
            MessageContract.Entry.COLUMN_NAME_ENTRY_ID,
            MessageContract.Entry.COLUMN_NAME_SUBJECT,
            MessageContract.Entry.COLUMN_NAME_MSG,
            MessageContract.Entry.COLUMN_NAME_DATE_TIME,
            MessageContract.Entry.COLUMN_NAME_ISSUER,
            MessageContract.Entry.COLUMN_NAME_RECIPIENT,
            MessageContract.Entry.COLUMN_NAME_ISSUER_NAME,
            MessageContract.Entry.COLUMN_NAME_ISSUER_IMG};

    // Constants representing column positions from PROJECTION.
    public static final int COLUMN_ID = 0;
    public static final int COLUMN_ENTRY_ID = 1;
    public static final int COLUMN_TITLE = 2;
    public static final int COLUMN_LINK = 3;
    public static final int COLUMN_PUBLISHED = 4;
    public static final int COLUMN_ISSUER = 5;
    public static final int COLUMN_RECIPIENT = 6;
    public static final int COLUMN_ISSUER_NAME = 7;
    public static final int COLUMN_ISSUER_IMG = 8;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link android.content.AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        if(!Globals.getInstance().isConfiguredSsyncUser()){
            return;
        }

        Log.i(TAG, "Beginning network synchronization");
        try {
            InputStream stream = null;

            try {
                stream = downloadUrl(getUrl());
                Reader reader = new InputStreamReader(stream, "UTF-8");
                Type listType = new TypeToken<ArrayList<Message>>(){}.getType();
                List<Message> messages = new Gson().fromJson(reader,listType);
                if(messages!=null && !messages.isEmpty()){
                    updateMessageData(messages, syncResult);
                }
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

    /**
     * Read XML from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database
     * write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at
     * once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform
     *            database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */
    public void updateMessageData(final List<Message> messages, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
        final ContentResolver contentResolver = getContext().getContentResolver();

        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Add new items
        for (Message msg : messages) {
            Log.i(TAG, "Scheduling insert: entry_id=" + msg.getId());

            byte[] issue_img = getBlob(msg.getIssuer_img());

            batch.add(ContentProviderOperation.newInsert(MessageContract.Entry.CONTENT_URI)
                    .withValue(MessageContract.Entry.COLUMN_NAME_ENTRY_ID, msg.getId())
                    .withValue(MessageContract.Entry.COLUMN_NAME_SUBJECT, msg.getSubject())
                    .withValue(MessageContract.Entry.COLUMN_NAME_MSG, msg.getMsg())
                    .withValue(MessageContract.Entry.COLUMN_NAME_DATE_TIME, msg.getDate_time().getTime())
                    .withValue(MessageContract.Entry.COLUMN_NAME_ISSUER, msg.getIssuer())
                    .withValue(MessageContract.Entry.COLUMN_NAME_RECIPIENT, msg.getRecipient())
                    .withValue(MessageContract.Entry.COLUMN_NAME_ISSUER_NAME, msg.getIssuer_name())
                    .withValue(MessageContract.Entry.COLUMN_NAME_ISSUER_IMG, issue_img)
                    .build());
            syncResult.stats.numInserts++;
            msg.setImg(Globals.getInstance().convertBlobToBitmap(issue_img));
            notifyMessage(msg);
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(MessageContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                MessageContract.Entry.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.
    }

    private byte[] getBlob(String issuer_img) {
        try {
            if(issuer_img !=null && !issuer_img.isEmpty()){
                InputStream in = new java.net.URL(issuer_img).openStream();
                Bitmap icon = BitmapFactory.decodeStream(in);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void notifyMessage(Message msg) {
        Globals.getInstance().setMessageSelected(msg);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(msg.getSubject())
                        .setContentText(msg.getMsg());

        Intent resultIntent = new Intent(getContext(), DetailMessageActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        Log.i(TAG, "Streaming data from network: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public URL getUrl() throws MalformedURLException {

        String [] requestedColumns = {
                "MAX("+MessageContract.Entry.COLUMN_NAME_DATE_TIME+")"
        };

        Cursor c = mContentResolver.query(
                MessageContract.Entry.CONTENT_URI,
                requestedColumns,
                MessageContract.Entry.COLUMN_NAME_RECIPIENT+ "=" + 1 + "",
                null, null);


        while (c.moveToNext()) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(c.getLong(0));
            cal.add(Calendar.HOUR,3);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            String time = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
            String date_time = date+"T"+time+"Z";
            return new URL(Globals.getInstance().getUrlDownloadServer(date_time));
        }

        return new URL(Globals.getInstance().getUrlDownloadServer());
    }

}
