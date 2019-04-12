package com.expensemanager.app.service;

import android.text.TextUtils;
import android.util.Log;

import com.expensemanager.app.R;
import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.main.EApplication;
import com.expensemanager.app.models.BusDailySummary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;
import io.realm.Realm;

/**
 * Created by Girish Lakshmanan on 8/17/19.
 */

public class SyncBusDailySummary {
    private static final String TAG = SyncBusDailySummary.class.getSimpleName();

    public static Task<Void> getAllBusDailySummaries() {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.getAllBusDailySummaries();
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);

        Continuation<JSONObject, Void> saveBusDailySummary = new Continuation<JSONObject, Void>() {
            @Override
            public Void then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in downloading all busDailySummaries.", exception);
                    throw exception;
                }

                JSONObject busDailySummaries = task.getResult();
                if (busDailySummaries == null) {
                    throw new Exception("Empty response.");
                }

                Log.d(TAG, "BusDailySummaries: \n" + busDailySummaries);

                try {
                    JSONArray busDailySummaryArray = busDailySummaries.getJSONObject("_embedded").getJSONArray("busDailySummaries");
                    BusDailySummary.mapFromJSONArray(busDailySummaryArray);
                } catch (JSONException e) {
                    Log.e(TAG, "Error in getting busDailySummary JSONArray.", e);
                }

                return null;
            }
        };

        Log.d(TAG, "Start downloading BusDailySummaries");
        return networkRequest.send().continueWith(saveBusDailySummary);
    }

    public static Task<Void> getAllBusDailySummariesByUserId(String userId) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.getAllBusDailySummariesByUserId(userId);
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);

        Continuation<JSONObject, Void> saveBusDailySummary = new Continuation<JSONObject, Void>() {
            @Override
            public Void then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in downloading all busDailySummaries.", exception);
                    throw exception;
                }

                JSONObject busDailySummaries = task.getResult();
                if (busDailySummaries == null) {
                    throw new Exception("Empty response.");
                }

                Log.d(TAG, "BusDailySummaries: \n" + busDailySummaries);

                try {
                    JSONArray busDailySummaryArray = busDailySummaries.getJSONObject("_embedded").getJSONArray("busDailySummaries");
                    BusDailySummary.mapFromJSONArray(busDailySummaryArray);
                } catch (JSONException e) {
                    Log.e(TAG, "Error in getting busDailySummary JSONArray.", e);
                }

                return null;
            }
        };

        Log.d(TAG, "Start downloading BusDailySummaries");
        return networkRequest.send().continueWith(saveBusDailySummary);
    }

    public static Task<Void> getAllBusDailySummariesByGroupId(String groupId) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.getAllBusDailySummariesByGroupId(groupId);
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);

        Continuation<JSONObject, Void> saveBusDailySummary = new Continuation<JSONObject, Void>() {
            @Override
            public Void then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in downloading all busDailySummaries.", exception);
                    throw exception;
                }

                JSONObject busDailySummaries = task.getResult();
                if (busDailySummaries == null) {
                    throw new Exception("Empty response.");
                }

                Log.d(TAG, "BusDailySummaries: \n" + busDailySummaries);

                try {
                    JSONArray busDailySummaryArray = busDailySummaries.getJSONObject("_embedded").getJSONArray("busDailySummaries");
                    BusDailySummary.mapFromJSONArray(busDailySummaryArray);
                } catch (JSONException e) {
                    Log.e(TAG, "Error in getting busDailySummary JSONArray.", e);
                }

                return null;
            }
        };

        Log.d(TAG, "Start downloading BusDailySummaries");
        return networkRequest.send().continueWith(saveBusDailySummary);
    }

    public static Task<Void> getBusDailySummaryById(String busDailySummaryId) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.getBusDailySummaryById(busDailySummaryId);
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);

        Continuation<JSONObject, Void> saveBusDailySummary = new Continuation<JSONObject, Void>() {
            @Override
            public Void then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in downloading busDailySummary.", exception);
                    throw exception;
                }

                JSONObject result = task.getResult();
                if (result == null) {
                    throw new Exception("Empty response.");
                }

                Log.d(TAG, "BusDailySummary: \n" + result);

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                BusDailySummary busDailySummary = new BusDailySummary();
                busDailySummary.mapFromJSON(result);
                realm.copyToRealmOrUpdate(busDailySummary);
                realm.commitTransaction();
                realm.close();

                return null;
            }
        };

        Log.d(TAG, "Start downloading BusDailySummaries");
        return networkRequest.send().continueWith(saveBusDailySummary);
    }

    public static Task<JSONObject> create(BusDailySummary busDailySummary) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.createBusDailySummary(busDailySummary);
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);

        Continuation<JSONObject, JSONObject> onCreateBusDailySummaryFinished = new Continuation<JSONObject, JSONObject>() {
            @Override
            public JSONObject then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in creating busDailySummary.", exception);
                    throw exception;
                }

                JSONObject result = task.getResult();
                if (result == null) {
                    throw new Exception("Empty response.");
                }

                // Example response: {"objectId":"tUfEENoHSS","createdAt":"2016-08-18T22:34:59.262Z"}
                Log.d(TAG, "Response: \n" + result);

                return result;
            }
        };

        Log.d(TAG, "Start creating busDailySummary.");
        return networkRequest.send().continueWith(onCreateBusDailySummaryFinished);
    }

    public static Task<Void> update(BusDailySummary busDailySummary) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.updateBusDailySummary(busDailySummary);
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);


        Continuation<JSONObject, Void> onUpdateBusDailySummaryFinished = new Continuation<JSONObject, Void>() {
            @Override
            public Void then(Task<JSONObject> task) throws Exception {
                Log.d(TAG, "onUpdateBusDailySummaryFinished before check task.isFaulted().");
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in updating busDailySummary.", exception);
                    throw exception;
                }

                Log.d(TAG, "onUpdateBusDailySummaryFinished after check task.isFaulted().");

                JSONObject result = task.getResult();

                if (result == null) {
                    throw new Exception("Empty response.");
                }

                // Example response: {"updatedAt":"2016-08-18T23:03:51.785Z"}
                Log.d(TAG, "onUpdateBusDailySummaryFinished Response: \n" + result);
                return null;
            }
        };

        Log.d(TAG, "Start updating busDailySummary.");
        return networkRequest.send().continueWith(onUpdateBusDailySummaryFinished);
    }

    public static Task<Void> delete(String busDailySummaryId) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate requestTemplate = RequestTemplateCreator.deleteBusDailySummary(busDailySummaryId);
        NetworkRequest networkRequest = new NetworkRequest(requestTemplate, taskCompletionSource);

        Continuation<JSONObject, Void> onUpdateCategoryFinished = new Continuation<JSONObject, Void>() {
            @Override
            public Void then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in deleting busDailySummary.", exception);
                    throw exception;
                }

                JSONObject result = task.getResult();
                if (result == null) {
                    throw new Exception("Empty response.");
                }

                // Example response: {}
                Log.d(TAG, "Response: \n" + result);
                return null;
            }
        };

        Log.d(TAG, "Start updating busDailySummary.");
        return networkRequest.send().continueWith(onUpdateCategoryFinished);

    }


}
