package com.bigmantra.natco.service;

import android.util.Log;

import com.bigmantra.natco.service.RequestTemplateCreator;

import org.json.JSONObject;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by Girish Lakshmanan on 9/5/19.
 */

public class SyncFile {
    private static final String TAG = SyncFile.class.getSimpleName();

    public static Task<JSONObject> deleteFile(String fileName) {
        TaskCompletionSource<JSONObject> taskCompletionSource = new TaskCompletionSource<>();
        RequestTemplate template = RequestTemplateCreator.deleteFileByName(fileName);
        NetworkRequest networkRequest = new NetworkRequest(template, taskCompletionSource);

        Continuation<JSONObject, JSONObject> onFileDelete = new Continuation<JSONObject, JSONObject>() {
            @Override
            public JSONObject then(Task<JSONObject> task) throws Exception {
                if (task.isFaulted()) {
                    Exception exception = task.getError();
                    Log.e(TAG, "Error in deleteFile", exception);
                    throw exception;
                }

                // Error response example: {"code":153,"error":"Could not delete file."}
                // File may not exist
                Log.d(TAG, "delete file result: " + task.getResult().toString());
                if (task.getResult().toString().equals("{}")) {
                    Log.d(TAG, "File delete success.");
                } else {
                    Log.e(TAG, "Error in deleting file: " + fileName);
                    // force to delete entry in Photo table
                }

                return task.getResult();
            }
        };

        return networkRequest.send().continueWith(onFileDelete);
    }
}
