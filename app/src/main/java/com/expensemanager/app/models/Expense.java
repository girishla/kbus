package com.expensemanager.app.models;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by Girish Lakshmanan on 8/16/16.
 */

@RealmClass
public class Expense implements RealmModel {
    private static final String TAG = Expense.class.getSimpleName();

    // Keys in JSON response
    public static final String OBJECT_ID_JSON_KEY = "id";
    public static final String AMOUNT_JSON_KEY = "amount";
    public static final String NOTE_JSON_KEY = "note";
    public static final String CREATED_AT_JSON_KEY = "createdDate";
    public static final String EXPENSE_DATE_JSON_KEY = "expenseDate";   // Different from local
    public static final String ISO_EXPENSE_DATE_JSON_KEY = "iso";
    public static final String CATEGORY_JSON_KEY = "categoryId";
    public static final String USER_JSON_KEY = "userId";
    public static final String GROUP_JSON_KEY = "groupId";
    public static final String USER_OBJ_KEY = "user";
    public static final String GROUP_OBJ_KEY = "group";
    public static final String CATEGORY_OBJ_KEY = "category";
    public static final String LINKS_OBJ_KEY = "_links";


    public static final String NO_CATEGORY_JSON_VALUE = "undefined";

    // Property name key
    public static final String ID_KEY = "id";
    public static final String EXPENSE_DATE_KEY = "expenseDate";
    public static final String CATEGORY_ID_KEY = "categoryId";
    public static final String GROUP_KEY = "groupId";

    // Property
    @PrimaryKey
    private String id;
    private String photos;
    private String note;
    private double amount;
    private Date createdDate;
    private Date expenseDate;
    private boolean isSynced;
    private String userId;
    private String categoryId;
    private String groupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getcreatedDate() {
        return createdDate;
    }

    public void setcreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public @Nullable
    Category getCategory() {
        return Category.getCategoryById(this.categoryId);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void mapFromJSON(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString(OBJECT_ID_JSON_KEY);
            this.amount = jsonObject.getDouble(AMOUNT_JSON_KEY);

            this.note = jsonObject.optString(NOTE_JSON_KEY, "");
            this.groupId= jsonObject.getJSONObject(GROUP_OBJ_KEY).getString(OBJECT_ID_JSON_KEY);
            this.userId= jsonObject.getJSONObject(USER_OBJ_KEY).getString(OBJECT_ID_JSON_KEY);
            if( !(jsonObject.isNull(CATEGORY_OBJ_KEY))){
                this.categoryId = jsonObject.getJSONObject(CATEGORY_OBJ_KEY).getString(OBJECT_ID_JSON_KEY);

            }

            // Parse createdDate and convert UTC time to local time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            this.createdDate = simpleDateFormat.parse(jsonObject.getString(CREATED_AT_JSON_KEY));
            this.expenseDate = simpleDateFormat.parse(jsonObject.getString(EXPENSE_DATE_JSON_KEY));

        } catch (JSONException e) {
            Log.e(TAG, "Error in parsing expense.", e);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing createdDate.", e);
        }
    }

    public static void mapFromJSONArray(JSONArray jsonArray) {
        RealmList<Expense> expenses = new RealmList<>();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject expenseJson = jsonArray.getJSONObject(i);
                Expense expense = new Expense();
                expense.mapFromJSON(expenseJson);
                expenses.add(expense);
            } catch (JSONException e) {
                Log.e(TAG, "Error in parsing expense.", e);
            }
        }

        realm.copyToRealmOrUpdate(expenses);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * @return all expenses
     */
    public static RealmResults<Expense> getAllExpensesByGroupId(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = realm.where(Expense.class).equalTo(GROUP_KEY, groupId).findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by category
     */
    public static RealmResults<Expense> getAllExpensesByMemberAndGroupId(Member member, String groupId) {
        if (member == null) {
            return null;
        }

        String userId = member.getUserId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = realm.where(Expense.class).equalTo(USER_JSON_KEY, userId).equalTo(GROUP_KEY, groupId).findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by category
     */
    public static RealmResults<Expense> getAllExpensesByCategoryAndGroupId(Category category, String groupId) {
        String categoryId = category != null ? category.getId() : null;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = realm.where(Expense.class).equalTo(CATEGORY_ID_KEY, categoryId).equalTo(GROUP_KEY, groupId).findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by start date and/or end date
     */
    public static RealmResults<Expense> getAllExpensesByDateAndGroupId(Date startDate, Date endDate, String groupId) {
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0) {
            return null;
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = null;

        if (startDate != null && endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (startDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        }

        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by category and start date and/or end date
     */
    public static RealmResults<Expense> getAllExpensesByDateAndCategoryAndGroupId(Date startDate, Date endDate, Category category, String groupId) {
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0) {
            return null;
        }

        String categoryId = category != null ? category.getId() : null;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = null;

        if (startDate != null && endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .equalTo(CATEGORY_ID_KEY, categoryId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (startDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .equalTo(CATEGORY_ID_KEY, categoryId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .equalTo(CATEGORY_ID_KEY, categoryId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        }

        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by member and category
     */
    public static RealmResults<Expense> getAllExpensesByMemberAndCategoryAndGroupId(Member member, Category category, String groupId) {
        if (member == null) {
            return null;
        }

        String categoryId = category != null ? category.getId() : null;
        String userId = member.getUserId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = null;

        expenses = realm.where(Expense.class)
                .equalTo(GROUP_KEY, groupId)
                .equalTo(USER_JSON_KEY, userId)
                .equalTo(CATEGORY_ID_KEY, categoryId)
                .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);

        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by member and start date and/or end date
     */
    public static RealmResults<Expense> getAllExpensesByMemberAndDateAndGroupId(Member member, Date startDate, Date endDate, String groupId) {
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0 || member == null) {
            return null;
        }

        String userId = member.getUserId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = null;

        if (startDate != null && endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .equalTo(USER_JSON_KEY, userId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (startDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .equalTo(USER_JSON_KEY, userId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .equalTo(USER_JSON_KEY, userId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        }

        realm.close();

        return expenses;
    }

    /**
     * @return all expenses by member and category and start date and/or end date
     */
    public static RealmResults<Expense> getAllExpensesByMemberAndDateAndCategoryAndGroupId(Member member, Date startDate, Date endDate, Category category, String groupId) {
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0 || member == null) {
            return null;
        }

        String categoryId = category != null ? category.getId() : null;
        String userId = member.getUserId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = null;

        if (startDate != null && endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .equalTo(USER_JSON_KEY, userId)
                    .equalTo(CATEGORY_ID_KEY, categoryId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (startDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startDate)
                    .equalTo(USER_JSON_KEY, userId)
                    .equalTo(CATEGORY_ID_KEY, categoryId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        } else if (endDate != null) {
            expenses = realm.where(Expense.class)
                    .equalTo(GROUP_KEY, groupId)
                    .lessThanOrEqualTo(EXPENSE_DATE_KEY, endDate)
                    .equalTo(USER_JSON_KEY, userId)
                    .equalTo(CATEGORY_ID_KEY, categoryId)
                    .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        }

        realm.close();

        return expenses;
    }

    /**
     * @param id
     * @return Expense object if exist, otherwise return null.
     */
    public static @Nullable
    Expense getExpenseById(String id) {
        Realm realm = Realm.getDefaultInstance();
        Expense expense = realm.where(Expense.class).equalTo(ID_KEY, id).findFirst();
        realm.close();

        return expense;
    }

    /**
     * @return Earliest expense
     */
    public static @Nullable
    Expense getOldestExpenseByGroupId(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        List<Expense> expenses = realm.where(Expense.class)
                .equalTo(GROUP_KEY, groupId)
                .findAllSorted(EXPENSE_DATE_KEY, Sort.ASCENDING);
        realm.close();

        return expenses.size() > 0 ? expenses.get(0) : null;
    }

    /**
     * @return Most recent expense
     */
    public static @Nullable
    Expense getMostRecentExpenseByGroupId(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        List<Expense> expenses = realm.where(Expense.class)
                .equalTo(GROUP_KEY, groupId)
                .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return expenses.size() > 0 ? expenses.get(0) : null;
    }

    public static void delete(String id) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Expense> expenses = realm.where(Expense.class).equalTo(ID_KEY, id).findAll();
        if (expenses.size() > 0) {
            expenses.deleteFromRealm(0);
        }
        realm.commitTransaction();
        realm.close();
    }

    public static RealmResults<Expense> getExpensesByRangeAndGroupId(Date[] startEnd, String groupId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Expense> expenses = realm.where(Expense.class)
                .equalTo(GROUP_KEY, groupId)
                .greaterThanOrEqualTo(EXPENSE_DATE_KEY, startEnd[0])
                .lessThanOrEqualTo(EXPENSE_DATE_KEY, startEnd[1])
                .findAllSorted(EXPENSE_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return expenses;
    }
}
