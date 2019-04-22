package com.expensemanager.app.service;

import android.text.TextUtils;
import android.util.Log;

import com.expensemanager.app.helpers.Helpers;
import com.expensemanager.app.models.BusDailySummary;
import com.expensemanager.app.models.Category;
import com.expensemanager.app.models.Expense;
import com.expensemanager.app.models.Group;
import com.expensemanager.app.models.Member;
import com.expensemanager.app.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Girish Lakshmanan on 8/17/19.
 */

public class RequestTemplateCreator {
    private static final String TAG = RequestTemplateCreator.class.getSimpleName();

            private static final String BASE_URL = "http://10.0.2.2:8080/";
//    private static final String BASE_URL = "http://192.168.56.38:8080/";
//    private static final String BASE_URL = "http://192.168.1.150:8080/";
//    private static final String BASE_URL = "http://172.20.10.14:8080/";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String PATCH = "PATCH";

    public static final String DELETE = "DELETE";
    public static final String INCLUDE = "include";
    public static final String CONTENT = "CONTENT";
    public static final String WHERE = "where";

    public static RequestTemplate updatePassword(String userId, String password) {
        String url = BASE_URL + "users/" + userId;
        Map<String, String> params = new HashMap<>();

        params.put(User.PASSWORD_JSON_KEY, password);

        return new RequestTemplate(PUT, url, params);
    }

    public static RequestTemplate login(String username, String password) {
        String url = BASE_URL + "auth";
        Map<String, String> params = new HashMap<>();

        params.put(User.USERNAME_JSON_KEY, username);
        params.put(User.PASSWORD_JSON_KEY, password);

        return new RequestTemplate(POST, url, params);
    }

    public static RequestTemplate signUp(String email, String password, String firstName, String lastName, String phone) {
        String url = BASE_URL + "users";
        Map<String, String> params = new HashMap<>();

        if (!TextUtils.isEmpty(phone)) {
            params.put(User.USERNAME_JSON_KEY, phone);
            params.put(User.PHONE_JSON_KEY, phone);
        } else {
            params.put(User.USERNAME_JSON_KEY, email);
            params.put(User.EMAIL_JSON_KEY, email);
        }

        params.put(User.PASSWORD_JSON_KEY, password);

        if (!TextUtils.isEmpty(firstName)) {
            params.put(User.FIRST_NAME_JSON_KEY, firstName);
        }

        if (!TextUtils.isEmpty(lastName)) {
            params.put(User.LAST_NAME_JSON_KEY, lastName);
        }

        return new RequestTemplate(POST, url, params);
    }

    public static RequestTemplate logout() {
        String url = BASE_URL + "user";

        return new RequestTemplate(GET, url, null);
    }

    public static RequestTemplate getAllExpenses() {
        String url = BASE_URL + "expenses";
        Map<String, String> params = new HashMap<>();
        //todo: getAllExpensesByUserId
        params.put("projection", "expenseProjection");

        return new RequestTemplate(GET, url, params);
    }


    public static RequestTemplate getAllExpensesByUserId(String userId) {
        String url = BASE_URL + "expenses/search/findByUserId";
        Map<String, String> params = new HashMap<>();
        params.put("projection", "expenseProjection");
        params.put("userId", userId);

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getAllExpensesByGroupId(String groupId) {
        String url = BASE_URL + "expenses/search/findByGroupId";
        Map<String, String> params = new HashMap<>();
        params.put("projection", "expenseProjection");
        params.put("groupId", groupId);


        return new RequestTemplate(GET, url, params);
    }


    public static RequestTemplate getExpenseById(String id) {
        String url = BASE_URL + "expenses" + "/" + id;
        Map<String, String> params = new HashMap<>();

        params.put("projection", "expenseProjection");

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate createExpense(ExpenseBuilder expenseBuilder) {
        String url = BASE_URL + "expenses";
        Map<String, String> params = new HashMap<>();

        Expense expense = expenseBuilder.getExpense();

        params.put(Expense.AMOUNT_JSON_KEY, String.valueOf(expense.getAmount()));

        String note = expense.getNote();

        if (note != null && !note.isEmpty() && note.length() > 0) {
            params.put(Expense.NOTE_JSON_KEY, expense.getNote());
        }


        params.put(Expense.USER_OBJ_KEY, BASE_URL + "users/" + expense.getUserId());
        params.put(Expense.GROUP_OBJ_KEY, BASE_URL + "groups/" + expense.getGroupId());
        if (expense.getCategoryId() != null) {
            params.put(Expense.CATEGORY_OBJ_KEY, BASE_URL + "categories/" + expense.getCategoryId());

        }

        try {

            // Date pointer
            // "spentAt" -> "{"__type":"Date","iso":"2016-08-04T21:48:00.000Z"}"
            SimpleDateFormat timezoneFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000'Z'");
            timezoneFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String time = timezoneFormat.format(expense.getExpenseDate());


            params.put(Expense.EXPENSE_DATE_JSON_KEY, time);
        } catch (Exception e) {
            Log.e(TAG, "Error pointer object for 'where' in createExpense", e);
        }

        return new RequestTemplate(POST, url, params);
    }

    public static RequestTemplate updateExpense(ExpenseBuilder expenseBuilder) {
        Expense expense = expenseBuilder.getExpense();

        String url = BASE_URL + "expenses/" + expense.getId();
        Map<String, String> params = new HashMap<>();

        params.put(Expense.AMOUNT_JSON_KEY, String.valueOf(expense.getAmount()));
        Log.i(TAG, String.valueOf(expense.getNote().length()));
        params.put(Expense.NOTE_JSON_KEY, expense.getNote());
        // todo: able to update with categoryId

        params.put(Expense.USER_OBJ_KEY, BASE_URL + "users/" + expense.getUserId());
        params.put(Expense.GROUP_OBJ_KEY, BASE_URL + "groups/" + expense.getGroupId());
        if (expense.getCategoryId() != null) {
            params.put(Expense.CATEGORY_OBJ_KEY, BASE_URL + "categories/" + expense.getCategoryId());

        }

        // Date pointer
        // "spentAt" -> "{"__type":"Date","iso":"2016-08-04T21:48:00.000Z"}"
        SimpleDateFormat timezoneFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000'Z'", Locale.US);
        timezoneFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = timezoneFormat.format(expense.getExpenseDate());

        params.put(Expense.EXPENSE_DATE_JSON_KEY, time);


        return new RequestTemplate(PUT, url, params);
    }

    public static RequestTemplate deleteExpense(String expenseId) {
        String url = BASE_URL + "expenses/" + expenseId;

        return new RequestTemplate(DELETE, url, null);
    }


    public static RequestTemplate getAllBusDailySummaries() {
        String url = BASE_URL + "busdailysummaries";
        Map<String, String> params = new HashMap<>();
        //todo: getAllBusDailySummariesByUserId
        params.put("projection", "busDailySummaryProjection");

        return new RequestTemplate(GET, url, params);
    }


    public static RequestTemplate getAllBusDailySummariesByUserId(String userId) {
        String url = BASE_URL + "busdailysummaries/search/findByUserId";
        Map<String, String> params = new HashMap<>();
        params.put("projection", "busdailysummaryProjection");
        params.put("userId", userId);

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getAllBusDailySummariesByGroupId(String groupId) {


        String url = BASE_URL + "busdailysummaries/search/findByGroupIdAndDateIdBetween";
        Map<String, String> params = new HashMap<>();
        params.put("projection", "busdailysummaryProjection");
        params.put("groupId", groupId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        params.put("startDateId", getYearStartDate());
        params.put("endDateId", dateFormat.format(Calendar.getInstance().getTime()));


        return new RequestTemplate(GET, url, params);
    }

    public static String getFiscalYear() {
        Calendar calendarDate = Calendar.getInstance();
        int month = calendarDate.get(Calendar.MONTH);
        int year = calendarDate.get(Calendar.YEAR);
        return String.valueOf((month >= Calendar.APRIL) ? year : year - 1);
    }

    private static String getYearStartDate() {
        return getFiscalYear() + "0401";
    }


    public static RequestTemplate getBusDailySummaryById(String id) {
        String url = BASE_URL + "busdailysummaries" + "/" + id;
        Map<String, String> params = new HashMap<>();

        params.put("projection", "busdailysummaryProjection");

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate createBusDailySummary(BusDailySummary busDailySummary) {
        String url = BASE_URL + "busdailysummaries";
        Map<String, String> params = new HashMap<>();

        params.put(BusDailySummary.SINGLE1COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle1Collection()));
        params.put(BusDailySummary.SINGLE2COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle2Collection()));
        params.put(BusDailySummary.SINGLE3COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle3Collection()));
        params.put(BusDailySummary.SINGLE4COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle4Collection()));
        params.put(BusDailySummary.SINGLE5COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle5Collection()));
        params.put(BusDailySummary.SINGLE6COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle6Collection()));
        params.put(BusDailySummary.SINGLE7COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle7Collection()));
        params.put(BusDailySummary.SINGLE8COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle8Collection()));
        params.put(BusDailySummary.SINGLE9COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle9Collection()));
        params.put(BusDailySummary.SINGLE10COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle10Collection()));

        params.put(BusDailySummary.DIESELEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getDieselExpense()));
        params.put(BusDailySummary.DIESELLITRES_JSON_KEY, String.valueOf(busDailySummary.getDieselLitres()));
        params.put(BusDailySummary.GREASEEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getGreaseExpense()));
        params.put(BusDailySummary.DRIVERPATHAEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getDriverPathaExpense()));
        params.put(BusDailySummary.DRIVERSALARYEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getDriverSalaryAllowanceExpense()));
        params.put(BusDailySummary.CONDUCTORPATHAEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getConductorPathaExpense()));
        params.put(BusDailySummary.CONDUCTORSALARYEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getConductorSalaryAllowanceExpense()));
        params.put(BusDailySummary.CHECKINGPATHAEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getCheckingPathaExpense()));
        params.put(BusDailySummary.COMMISSIONEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getCommissionExpense()));
        params.put(BusDailySummary.OTHEREXPENSE_JSON_KEY, String.valueOf(busDailySummary.getOtherExpense()));
        params.put(BusDailySummary.UNIONEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getUnionExpense()));
        params.put(BusDailySummary.CLEANEREXPENSE_JSON_KEY, String.valueOf(busDailySummary.getCleanerExpense()));


        params.put(BusDailySummary.SUBMITTEDBY_OBJ_KEY, BASE_URL + "users/" + busDailySummary.getSubmittedById());
        params.put(BusDailySummary.DRIVER_OBJ_KEY, BASE_URL + "users/" + busDailySummary.getDriverId());
        params.put(BusDailySummary.CONDUCTOR_OBJ_KEY, BASE_URL + "users/" + busDailySummary.getConductorId());
        params.put(BusDailySummary.GROUP_OBJ_KEY, BASE_URL + "groups/" + busDailySummary.getGroupId());


        SimpleDateFormat timezoneFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000'Z'", Locale.US);
        timezoneFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = timezoneFormat.format(busDailySummary.getSummaryDate());

        SimpleDateFormat timezoneFormat2 = new SimpleDateFormat("yyyyMMdd", Locale.US);
        timezoneFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time2 = timezoneFormat2.format(busDailySummary.getSummaryDate());

        params.put(BusDailySummary.SUMMARY_DATE_KEY, time);
        params.put(BusDailySummary.DATEID_JSON_KEY, time2);

        return new RequestTemplate(POST, url, params);
    }

    public static RequestTemplate updateBusDailySummary(BusDailySummary busDailySummary) {

        String url = BASE_URL + "busdailysummaries/" + busDailySummary.getId();
        Map<String, String> params = new HashMap<>();

        params.put(BusDailySummary.SINGLE1COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle1Collection()));
        params.put(BusDailySummary.SINGLE2COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle2Collection()));
        params.put(BusDailySummary.SINGLE3COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle3Collection()));
        params.put(BusDailySummary.SINGLE4COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle4Collection()));
        params.put(BusDailySummary.SINGLE5COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle5Collection()));
        params.put(BusDailySummary.SINGLE6COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle6Collection()));
        params.put(BusDailySummary.SINGLE7COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle7Collection()));
        params.put(BusDailySummary.SINGLE8COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle8Collection()));
        params.put(BusDailySummary.SINGLE9COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle9Collection()));
        params.put(BusDailySummary.SINGLE10COLLECTION_JSON_KEY, String.valueOf(busDailySummary.getSingle10Collection()));

        params.put(BusDailySummary.DIESELEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getDieselExpense()));
        params.put(BusDailySummary.DIESELLITRES_JSON_KEY, String.valueOf(busDailySummary.getDieselLitres()));
        params.put(BusDailySummary.GREASEEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getGreaseExpense()));
        params.put(BusDailySummary.DRIVERPATHAEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getDriverPathaExpense()));
        params.put(BusDailySummary.DRIVERSALARYEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getDriverSalaryAllowanceExpense()));
        params.put(BusDailySummary.CONDUCTORPATHAEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getConductorPathaExpense()));
        params.put(BusDailySummary.CONDUCTORSALARYEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getConductorSalaryAllowanceExpense()));
        params.put(BusDailySummary.CHECKINGPATHAEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getCheckingPathaExpense()));
        params.put(BusDailySummary.COMMISSIONEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getCommissionExpense()));
        params.put(BusDailySummary.OTHEREXPENSE_JSON_KEY, String.valueOf(busDailySummary.getOtherExpense()));
        params.put(BusDailySummary.UNIONEXPENSE_JSON_KEY, String.valueOf(busDailySummary.getUnionExpense()));
        params.put(BusDailySummary.CLEANEREXPENSE_JSON_KEY, String.valueOf(busDailySummary.getCleanerExpense()));

        if ((busDailySummary.getSubmittedById() != null)) {
            params.put(BusDailySummary.SUBMITTEDBY_OBJ_KEY, BASE_URL + "users/" + busDailySummary.getSubmittedById());

        }
        if (busDailySummary.getDriverId() != null) {
            params.put(BusDailySummary.DRIVER_OBJ_KEY, BASE_URL + "users/" + busDailySummary.getDriverId());

        }
        if (busDailySummary.getConductorId() != null) {
            params.put(BusDailySummary.CONDUCTOR_OBJ_KEY, BASE_URL + "users/" + busDailySummary.getConductorId());

        }
        if (busDailySummary.getGroupId() != null) {
            params.put(BusDailySummary.GROUP_OBJ_KEY, BASE_URL + "groups/" + busDailySummary.getGroupId());

        }


        SimpleDateFormat timezoneFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00.000'Z'", Locale.US);
        timezoneFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String time = timezoneFormat.format(busDailySummary.getSummaryDate());
//
//        SimpleDateFormat timezoneFormat2 = new SimpleDateFormat("yyyyMMdd", Locale.US);
//        timezoneFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String time2 = timezoneFormat2.format(busDailySummary.getSummaryDate());

        params.put(BusDailySummary.SUMMARY_DATE_KEY, time);
        params.put(BusDailySummary.DATEID_JSON_KEY, busDailySummary.getId());
        params.put(BusDailySummary.ID_KEY, busDailySummary.getId());


        return new RequestTemplate(PATCH, url, params);
    }

    public static RequestTemplate deleteBusDailySummary(String busDailySummaryId) {
        String url = BASE_URL + "busdailysummaries/" + busDailySummaryId;

        return new RequestTemplate(DELETE, url, null);
    }

    public static RequestTemplate approveBusDailySummary(String busDailySummaryId) {
        String url = BASE_URL + "busdailysummaries/" + busDailySummaryId;
        Map<String, String> params = new HashMap<>();

        params.put(BusDailySummary.ID_KEY_FK, busDailySummaryId);
        params.put(BusDailySummary.IS_APPROVED_KEY, "true");

        return new RequestTemplate(PATCH, url, params);
    }


    public static RequestTemplate getAllCategories() {
        String url = BASE_URL + "categories";
        Map<String, String> params = new HashMap<>();
        //todo: getAllCategoriesByUserId

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getAllCategoriesByUserId(String userId) {
        if (userId == null) {
            return null;
        }

        String url = BASE_URL + "categories/search/findByUserId";
        Map<String, String> params = new HashMap<>();

        params.put("userid", userId);


//        params.put(WHERE, Helpers.encodeURIComponent(userIdObj.toString()));

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getAllCategoriesByGroupId(String groupId) {
        if (groupId == null) {
            return null;
        }

        String url = BASE_URL + "categories/search/findByGroupId";
        Map<String, String> params = new HashMap<>();
        JSONObject groupIdObj = new JSONObject();


        params.put("groupid", groupId);


//        params.put(WHERE, Helpers.encodeURIComponent(groupIdObj.toString()));

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate createCategory(Category category) {
        String url = BASE_URL + "categories";
        Map<String, String> params = new HashMap<>();

        try {
            params.put(Category.NAME_JSON_KEY, category.getName());
            params.put(Category.COLOR_JSON_KEY, category.getColor());

            params.put(Category.USER_JSON_KEY, category.getUserId());

            params.put(Category.GROUP_JSON_KEY, category.getGroupId());
            params.put(Category.ICON_JSON_KEY, category.getIcon());


        } catch (Exception e) {
            Log.e(TAG, "Error pointer object for 'where' in createExpense", e);
        }

        return new RequestTemplate(POST, url, params);
    }

    public static RequestTemplate updateCategory(Category category) {
        String url = BASE_URL + "categories/" + category.getId();
        Map<String, String> params = new HashMap<>();

        params.put(Category.NAME_JSON_KEY, category.getName());
        params.put(Category.COLOR_JSON_KEY, category.getColor());

        params.put(Category.USER_JSON_KEY, category.getUserId());

        params.put(Category.GROUP_JSON_KEY, category.getGroupId());
        params.put(Category.ICON_JSON_KEY, category.getIcon());

        return new RequestTemplate(PUT, url, params);
    }

    public static RequestTemplate deleteCategory(String categoryId) {
        String url = BASE_URL + "categories" + categoryId;

        return new RequestTemplate(DELETE, url, null);
    }

    /**
     * Upload photo to File table
     *
     * @param photoName
     * @param content
     * @return
     */
    public static RequestTemplate uploadPhoto(String photoName, byte[] content) {
        String url = BASE_URL + "files/" + photoName;
        Map<String, byte[]> params = new HashMap<>();
        params.put(CONTENT, content);

        return new RequestTemplate(POST, url, null, params, false);
    }

    public static RequestTemplate getExpensePhotoByPhotoId(String photoId) {
        String url = BASE_URL + "classes/Photo/" + photoId;
        return new RequestTemplate(GET, url, null);
    }

    public static RequestTemplate getExpensePhotoByExpenseId(String expenseId) {
        String url = BASE_URL + "classes/Photo";
        Map<String, String> params = new HashMap<>();

        JSONObject expensePointerObj = new JSONObject();
        JSONObject expenseIdObj = new JSONObject();
        try {
            expensePointerObj.put("__type", "Pointer");
            expensePointerObj.put("className", "Expense");
            expensePointerObj.put("objectId", expenseId);
            expenseIdObj.put("expenseId", expensePointerObj);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating expense id pointer object for where in getExpensePhotoByExpenseId", e);
        }

        params.put(WHERE, Helpers.encodeURIComponent(expenseIdObj.toString()));
        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate addExpensePhoto(String expenseId, String fileName) {
        String url = BASE_URL + "classes/Photo";
        Map<String, String> params = new HashMap<>();
        JSONObject expensePointerObject = new JSONObject();
        JSONObject photoObject = new JSONObject();

        try {
            // Build Expense pointer
            expensePointerObject.put("__type", "Pointer");
            expensePointerObject.put("className", "Expense");
            expensePointerObject.put("objectId", expenseId);

            Log.d(TAG, "expense pointer:" + expensePointerObject.toString());
            params.put("expenseId", expensePointerObject.toString());

            // Build File pointer
            photoObject.put("__type", "File");
            photoObject.put("name", fileName);

            Log.d(TAG, "photoObject:" + photoObject.toString());
            params.put("photo", photoObject.toString());

            return new RequestTemplate(POST, url, params);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating expense pointer or photo pointer for where clause in addExpensePhoto()", e);
        }

        return null;
    }

    public static RequestTemplate getLoginUser() {
        String url = BASE_URL + "user";

        return new RequestTemplate(GET, url, null, true);
    }

    public static RequestTemplate getAllUsersByUserFullName(String userFullName) {
        if (userFullName == null) {
            return null;
        }

        String url = BASE_URL + "users";
        Map<String, String> params = new HashMap<>();


        params.put(User.FULLNAME_JSON_KEY, userFullName);


        return new RequestTemplate(GET, url, params);
    }


    public static RequestTemplate getAllUsersByUserEmail(String userEmail) {
        if (userEmail == null) {
            return null;
        }

        String url = BASE_URL + "users/search/findByEmail";
        Map<String, String> params = new HashMap<>();

        JSONObject userNameObj = new JSONObject();

        try {
            userNameObj.put(User.EMAIL_JSON_KEY, userEmail);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating user email object for where in getAllUsersByUserEmail", e);
        }

//        params.put(WHERE, Helpers.encodeURIComponent(userNameObj.toString()));
        params.put(User.EMAIL_JSON_KEY, Helpers.encodeURIComponent(userEmail));

        return new RequestTemplate(GET, url, params);
    }


    public static RequestTemplate getAllUsersByUserPhoneNumber(String userPhoneNumber) {
        if (userPhoneNumber == null) {
            return null;
        }

        String url = BASE_URL + "users";
        Map<String, String> params = new HashMap<>();

        params.put(User.PHONE_JSON_KEY, userPhoneNumber);


        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        String url = BASE_URL + "users";
        Map<String, String> params = new HashMap<>();

        params.put(User.USERNAME_JSON_KEY, username);


        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getAllUsers() {

        String url = BASE_URL + "users";
        Map<String, String> params = new HashMap<>();

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate deleteFileByName(String fileName) {
        String url = BASE_URL + "files/" + fileName;

        return new RequestTemplate(DELETE, url, null);
    }

    public static RequestTemplate deleteExpensePhoto(String expensePhotoId) {
        String url = BASE_URL + "classes/Photo/" + expensePhotoId;

        return new RequestTemplate(DELETE, url, null);
    }

    public static RequestTemplate updateUser(User user) {
        String url = BASE_URL + "users/" + user.getId();
        Map<String, String> params = new HashMap<>();

        if (user.getEmail() != null && Helpers.isValidEmail(user.getEmail())) {
            params.put(User.EMAIL_JSON_KEY, user.getEmail());
            if (user.getUsername().contains("@")) {
                params.put(User.USERNAME_JSON_KEY, user.getEmail());
            }
        }

        if (user.getFirstName() != null) {
            params.put(User.FIRST_NAME_JSON_KEY, user.getFirstName());
        }

        if (user.getLastName() != null) {
            params.put(User.LAST_NAME_JSON_KEY, user.getLastName());
        }

        if (user.getPhone() != null && user.getUsername().contains("@")) {
            params.put(User.PHONE_JSON_KEY, user.getPhone());
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            Log.d(TAG, "entry key: " + entry.getKey() + ", value:" + entry.getValue());
        }

        return new RequestTemplate(PUT, url, params);
    }

    public static RequestTemplate addUserPhoto(String userId, String fileName) {
        String url = BASE_URL + "users/" + userId;
        Map<String, String> params = new HashMap<>();
        JSONObject photoObject = new JSONObject();

        try {
            // Build File pointer
            photoObject.put("__type", "File");
            photoObject.put("name", fileName);

            Log.d(TAG, "photoObject:" + photoObject.toString());
            params.put("photo", photoObject.toString());

            return new RequestTemplate(PUT, url, params);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating photo pointer for where clause in addExpensePhoto()", e);
        }

        return null;
    }

    public static RequestTemplate getAllGroupByUserId(String userId) {
        String url = BASE_URL + "groups";
        Map<String, String> params = new HashMap<>();


        params.put("userId", userId);

//        params.put(WHERE, Helpers.encodeURIComponent(userIdObj.toString()));

        return new RequestTemplate(GET, url, params);
    }


    public static RequestTemplate getGroupByGroupname(String groupname) {
        String url = BASE_URL + "groups";

        Map<String, String> params = new HashMap<>();

        params.put("groupname", groupname);


//        params.put(WHERE, Helpers.encodeURIComponent(groupnameJSON.toString()));

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getGroupById(String id) {
        String url = BASE_URL + "groups" + "/" + id;

        return new RequestTemplate(GET, url, null);
    }

    public static RequestTemplate createGroup(Group group) {
        String url = BASE_URL + "groups";
        Map<String, String> params = new HashMap<>();

        params.put(Group.GROUPNAME_JSON_KEY, group.getGroupname());

        if (!TextUtils.isEmpty(group.getName())) {
            params.put(Group.NAME_JSON_KEY, group.getName());
        }

        if (!TextUtils.isEmpty(group.getAbout())) {
            params.put(Group.ABOUT_JSON_KEY, group.getAbout());
        }

        params.put(Group.WEEKLY_BUDGET_JSON_KEY, String.valueOf(group.getWeeklyBudget()));
        params.put(Group.MONTHLY_BUDGET_JSON_KEY, String.valueOf(group.getMonthlyBudget()));
        params.put(Group.USER_ID_JSON_KEY, group.getUserId());


//        JSONObject userIdObj=new JSONObject();
//
//        try {
//            userIdObj.put("__type", "Pointer");
//            userIdObj.put("className", "_User");
//            userIdObj.put("objectId", group.getUserId());
//            params.put("userId", userIdObj.toString());
//        } catch (JSONException e) {
//            Log.e(TAG, "Error creating user pointer in createGroup");
//        }

        return new RequestTemplate(POST, url, params);
    }

    public static RequestTemplate updateGroup(Group group) {
        String url = BASE_URL + "groups/" + group.getId();
        Map<String, String> params = new HashMap<>();

        params.put(Group.GROUPNAME_JSON_KEY, group.getGroupname());

        if (!TextUtils.isEmpty(group.getName())) {
            params.put(Group.ABOUT_JSON_KEY, group.getName());
        }

        if (!TextUtils.isEmpty(group.getAbout())) {
            params.put(Group.ABOUT_JSON_KEY, group.getAbout());
        }

        params.put(Group.WEEKLY_BUDGET_JSON_KEY, String.valueOf(group.getWeeklyBudget()));
        params.put(Group.MONTHLY_BUDGET_JSON_KEY, String.valueOf(group.getMonthlyBudget()));

        return new RequestTemplate(PATCH, url, params);
    }

    public static RequestTemplate deleteGroup(String groupId) {
        String url = BASE_URL + "groups/" + groupId;

        return new RequestTemplate(DELETE, url, null);
    }

    public static RequestTemplate getMemberByMemberId(String memberId) {
        String url = BASE_URL + "members/" + memberId;
        Map<String, String> params = new HashMap<>();

        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getMembersByUserId(String userId) {
        String url = BASE_URL + "members/search/findByUserId";
        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("projection", "groupMemberProjection");


        return new RequestTemplate(GET, url, params);
    }

    public static RequestTemplate getMembersByGroupId(String groupId) {
        String url = BASE_URL + "members/search/findByGroupId";
        Map<String, String> params = new HashMap<>();
        params.put("groupid", groupId);
        params.put("projection", "groupMemberProjection");


        return new RequestTemplate(GET, url, params);
    }

    // Join a group or invite a user
    public static RequestTemplate createMember(Member member) {
        String url = BASE_URL + "members";
        Map<String, String> params = new HashMap<>();
        JSONObject groupIdObject = new JSONObject();
        JSONObject userIdObj = new JSONObject();
        JSONObject createdByObj = new JSONObject();

        try {

            params.put(Member.GROUP_ID_KEY, BASE_URL + "groups/" + member.getGroupId());
            params.put(Member.USER_ID_KEY, BASE_URL + "users/" + member.getUserId());
            params.put(Member.IS_ACCEPTED_KEY, String.valueOf(member.isAccepted()));

            return new RequestTemplate(POST, url, params);

        } catch (Exception e) {
            Log.e(TAG, "Error creating groupId or userId or createdBy pointer object for where in createMember()", e);
        }

        return null;
    }

    public static RequestTemplate updateMember(Member member) {
        String url = BASE_URL + "members/" + member.getId();
        Map<String, String> params = new HashMap<>();

        params.put(Member.IS_ACCEPTED_KEY, String.valueOf(member.isAccepted()));

        return new RequestTemplate(PUT, url, params);
    }

    public static RequestTemplate deleteMember(String memberId) {
        String url = BASE_URL + "members/" + memberId;

        return new RequestTemplate(DELETE, url, null);
    }
}
