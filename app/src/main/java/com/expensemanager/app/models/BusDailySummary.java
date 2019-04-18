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
 * Created by Girish Lakshmanan on 8/16/19.
 */

@RealmClass
public class BusDailySummary implements RealmModel {
    private static final String TAG = BusDailySummary.class.getSimpleName();

    // Keys in JSON response
    public static final String OBJECT_ID_JSON_KEY = "id";
    public static final String SINGLE1COLLECTION_JSON_KEY = "single1Collection";
    public static final String SINGLE2COLLECTION_JSON_KEY = "single2Collection";
    public static final String SINGLE3COLLECTION_JSON_KEY = "single3Collection";
    public static final String SINGLE4COLLECTION_JSON_KEY = "single4Collection";
    public static final String SINGLE5COLLECTION_JSON_KEY = "single5Collection";
    public static final String SINGLE6COLLECTION_JSON_KEY = "single6Collection";
    public static final String SINGLE7COLLECTION_JSON_KEY = "single7Collection";
    public static final String SINGLE8COLLECTION_JSON_KEY = "single8Collection";
    public static final String SINGLE9COLLECTION_JSON_KEY = "single9Collection";
    public static final String SINGLE10COLLECTION_JSON_KEY = "single10Collection";


    public static final String DIESELEXPENSE_JSON_KEY = "dieselExpense";
    public static final String OILEXPENSE_JSON_KEY = "oilExpense";
    public static final String WATEREXPENSE_JSON_KEY = "waterExpense";
    public static final String DRIVERPATHAEXPENSE_JSON_KEY = "driverPathaExpense";
    public static final String DRIVERSALARYEXPENSE_JSON_KEY = "driverSalaryAllowanceExpense";
    public static final String CONDUCTORPATHAEXPENSE_JSON_KEY = "conductorPathaExpense";
    public static final String CONDUCTORSALARYEXPENSE_JSON_KEY = "conductorSalaryAllowanceExpense";
    public static final String CHECKINGPATHAEXPENSE_JSON_KEY = "checkingPathaExpense";
    public static final String COMMISSIONEXPENSE_JSON_KEY = "commissionExpense";
    public static final String OTHEREXPENSE_JSON_KEY = "otherExpense";
    public static final String UNIONEXPENSE_JSON_KEY = "unionExpense";
    public static final String CLEANEREXPENSE_JSON_KEY = "cleanerExpense";


    public static final String CREATED_AT_JSON_KEY = "createdDate";
    public static final String DATEID_JSON_KEY = "dateId";   // Different from local

    public static final String SUBMITTEDBY_JSON_KEY = "submittedbyid";

    public static final String DRIVER_JSON_KEY = "driverId";
    public static final String CONDUCTOR_JSON_KEY = "conductorId";
    public static final String GROUP_JSON_KEY = "groupId";

    public static final String SUBMITTEDBY_OBJ_KEY = "submittedBy";

    public static final String DRIVER_OBJ_KEY = "driver";
    public static final String CONDUCTOR_OBJ_KEY = "conductor";
    public static final String GROUP_OBJ_KEY = "group";
    public static final String IS_APPROVED_KEY = "approved";

    // Property name key
    public static final String ID_KEY = "id";


    private Long dateId;
    private Date summaryDate;

    private String groupId;
    private String driverId;
    private String conductorId;
    private String submittedById;

    // Property
    @PrimaryKey
    private String id;
    private boolean isApproved = false;
    private boolean isSynced = false;

    private double single1Collection;
    private double single2Collection;
    private double single3Collection;
    private double single4Collection;
    private double single5Collection;
    private double single6Collection;
    private double single7Collection;
    private double single8Collection;
    private double single9Collection;
    private double single10Collection;

    private double dieselExpense;
    private double oilExpense;
    private double waterExpense;
    private double driverPathaExpense;
    private double driverSalaryAllowanceExpense;
    private double conductorPathaExpense;
    private double conductorSalaryAllowanceExpense;
    private double checkingPathaExpense;
    private double commissionExpense;
    private double otherExpense;
    private double unionExpense;
    private double cleanerExpense;

    private Date createdDate;

    // Property name key
    public static final String CATEGORY_ID_KEY = "categoryId";
    public static final String GROUP_KEY = "groupId";
    public static final String SUMMARY_DATE_KEY = "summaryDate";

    public double getTotalCollection() {

        return getSingle1Collection() + getSingle2Collection() + getSingle3Collection() + getSingle4Collection() + getSingle5Collection()
                + getSingle6Collection();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDateId() {
        return dateId;
    }

    public Date getSummaryDate() {
        return summaryDate;
    }

    public void setSummaryDate(Date summaryDate) {
        this.summaryDate = summaryDate;
    }


    public void setDateId(Long dateId) {
        this.dateId = dateId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }

    public String getSubmittedById() {
        return submittedById;
    }

    public void setSubmittedById(String submittedById) {
        this.submittedById = submittedById;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }


    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public double getSingle1Collection() {
        return single1Collection;
    }

    public void setSingle1Collection(double single1Collection) {
        this.single1Collection = single1Collection;
    }

    public double getSingle2Collection() {
        return single2Collection;
    }

    public void setSingle2Collection(double single2Collection) {
        this.single2Collection = single2Collection;
    }

    public double getSingle3Collection() {
        return single3Collection;
    }

    public void setSingle3Collection(double single3Collection) {
        this.single3Collection = single3Collection;
    }

    public double getSingle4Collection() {
        return single4Collection;
    }

    public void setSingle4Collection(double single4Collection) {
        this.single4Collection = single4Collection;
    }

    public double getSingle5Collection() {
        return single5Collection;
    }

    public void setSingle5Collection(double single5Collection) {
        this.single5Collection = single5Collection;
    }

    public double getSingle6Collection() {
        return single6Collection;
    }

    public void setSingle6Collection(double single6Collection) {
        this.single6Collection = single6Collection;
    }

    public double getSingle7Collection() {
        return single7Collection;
    }

    public void setSingle7Collection(double single7Collection) {
        this.single7Collection = single7Collection;
    }

    public double getSingle8Collection() {
        return single8Collection;
    }

    public void setSingle8Collection(double single8Collection) {
        this.single8Collection = single8Collection;
    }

    public double getSingle9Collection() {
        return single9Collection;
    }

    public void setSingle9Collection(double single9Collection) {
        this.single9Collection = single9Collection;
    }

    public double getSingle10Collection() {
        return single10Collection;
    }

    public void setSingle10Collection(double single10Collection) {
        this.single10Collection = single10Collection;
    }

    public double getDieselExpense() {
        return dieselExpense;
    }

    public void setDieselExpense(double dieselExpense) {
        this.dieselExpense = dieselExpense;
    }

    public double getOilExpense() {
        return oilExpense;
    }

    public void setOilExpense(double oilExpense) {
        this.oilExpense = oilExpense;
    }

    public double getWaterExpense() {
        return waterExpense;
    }

    public void setWaterExpense(double waterExpense) {
        this.waterExpense = waterExpense;
    }

    public double getDriverPathaExpense() {
        return driverPathaExpense;
    }

    public void setDriverPathaExpense(double driverPathaExpense) {
        this.driverPathaExpense = driverPathaExpense;
    }

    public double getDriverSalaryAllowanceExpense() {
        return driverSalaryAllowanceExpense;
    }

    public void setDriverSalaryAllowanceExpense(double driverSalaryAllowanceExpense) {
        this.driverSalaryAllowanceExpense = driverSalaryAllowanceExpense;
    }

    public double getConductorPathaExpense() {
        return conductorPathaExpense;
    }

    public void setConductorPathaExpense(double conductorPathaExpense) {
        this.conductorPathaExpense = conductorPathaExpense;
    }

    public double getConductorSalaryAllowanceExpense() {
        return conductorSalaryAllowanceExpense;
    }

    public void setConductorSalaryAllowanceExpense(double conductorSalaryAllowanceExpense) {
        this.conductorSalaryAllowanceExpense = conductorSalaryAllowanceExpense;
    }

    public double getCheckingPathaExpense() {
        return checkingPathaExpense;
    }

    public void setCheckingPathaExpense(double checkingPathaExpense) {
        this.checkingPathaExpense = checkingPathaExpense;
    }

    public double getCommissionExpense() {
        return commissionExpense;
    }

    public void setCommissionExpense(double commissionExpense) {
        this.commissionExpense = commissionExpense;
    }

    public double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public double getUnionExpense() {
        return unionExpense;
    }

    public void setUnionExpense(double unionExpense) {
        this.unionExpense = unionExpense;
    }

    public double getCleanerExpense() {
        return cleanerExpense;
    }

    public void setCleanerExpense(double cleanerExpense) {
        this.cleanerExpense = cleanerExpense;
    }


    public String getSubmittedBy() {
        return submittedById;
    }

    public void setSubmittedBy(String submittedById) {
        this.submittedById = submittedById;
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
            this.single1Collection = jsonObject.getDouble(SINGLE1COLLECTION_JSON_KEY);
            this.single2Collection = jsonObject.getDouble(SINGLE2COLLECTION_JSON_KEY);
            this.single3Collection = jsonObject.getDouble(SINGLE3COLLECTION_JSON_KEY);
            this.single4Collection = jsonObject.getDouble(SINGLE4COLLECTION_JSON_KEY);
            this.single5Collection = jsonObject.getDouble(SINGLE5COLLECTION_JSON_KEY);
            this.single6Collection = jsonObject.getDouble(SINGLE6COLLECTION_JSON_KEY);
            this.single7Collection = jsonObject.getDouble(SINGLE7COLLECTION_JSON_KEY);
            this.single8Collection = jsonObject.getDouble(SINGLE8COLLECTION_JSON_KEY);
            this.single9Collection = jsonObject.getDouble(SINGLE9COLLECTION_JSON_KEY);
            this.single10Collection = jsonObject.getDouble(SINGLE10COLLECTION_JSON_KEY);


            this.dieselExpense = jsonObject.getDouble(DIESELEXPENSE_JSON_KEY);
            this.oilExpense = jsonObject.getDouble(OILEXPENSE_JSON_KEY);
            this.waterExpense = jsonObject.getDouble(WATEREXPENSE_JSON_KEY);
            this.driverPathaExpense = jsonObject.getDouble(DRIVERPATHAEXPENSE_JSON_KEY);
            this.driverSalaryAllowanceExpense = jsonObject.getDouble(DRIVERSALARYEXPENSE_JSON_KEY);
            this.conductorPathaExpense = jsonObject.getDouble(CONDUCTORPATHAEXPENSE_JSON_KEY);
            this.conductorSalaryAllowanceExpense = jsonObject.getDouble(CONDUCTORSALARYEXPENSE_JSON_KEY);
            this.checkingPathaExpense = jsonObject.getDouble(CHECKINGPATHAEXPENSE_JSON_KEY);
            this.commissionExpense = jsonObject.getDouble(COMMISSIONEXPENSE_JSON_KEY);
            this.otherExpense = jsonObject.getDouble(OTHEREXPENSE_JSON_KEY);
            this.unionExpense = jsonObject.getDouble(UNIONEXPENSE_JSON_KEY);
            this.cleanerExpense = jsonObject.getDouble(CLEANEREXPENSE_JSON_KEY);
            this.isApproved = jsonObject.getBoolean(IS_APPROVED_KEY);


            this.groupId = jsonObject.getJSONObject(GROUP_OBJ_KEY).getString(OBJECT_ID_JSON_KEY);

            if (!(jsonObject.getString(DRIVER_OBJ_KEY).equals("null"))) {
                this.driverId = jsonObject.getJSONObject(DRIVER_OBJ_KEY).getString(OBJECT_ID_JSON_KEY);

            }

            if (!(jsonObject.getString(CONDUCTOR_OBJ_KEY).equals("null"))) {
                this.conductorId = jsonObject.getJSONObject(CONDUCTOR_OBJ_KEY).getString(OBJECT_ID_JSON_KEY);

            }


            // Parse createdDate and convert UTC time to local time
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            this.createdDate = simpleDateFormat.parse(jsonObject.getString(CREATED_AT_JSON_KEY));
            this.summaryDate = simpleDateFormat.parse(jsonObject.getString(SUMMARY_DATE_KEY));

            this.dateId = jsonObject.getLong(DATEID_JSON_KEY);

        } catch (JSONException e) {
            Log.e(TAG, "Error in parsing busdailysummary.", e);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing createdDate.", e);
        }
    }

    public static void mapFromJSONArray(JSONArray jsonArray) {
        RealmList<BusDailySummary> busDailySummaries = new RealmList<>();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject busdailysummaryJson = jsonArray.getJSONObject(i);
                BusDailySummary busdailysummary = new BusDailySummary();
                busdailysummary.mapFromJSON(busdailysummaryJson);
                busDailySummaries.add(busdailysummary);
            } catch (JSONException e) {
                Log.e(TAG, "Error in parsing busdailysummary.", e);
            }
        }

        realm.copyToRealmOrUpdate(busDailySummaries);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * @return all busDailySummaries
     */
    public static RealmResults<BusDailySummary> getAllBusDailySummariesByGroupId(String groupId) {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<BusDailySummary> busDailySummaries = realm.where(BusDailySummary.class).findAllSorted(SUMMARY_DATE_KEY, Sort.DESCENDING);
        realm.close();
        return busDailySummaries;
    }


    /**
     * @param id
     * @return BusDailySummary object if exist, otherwise return null.
     */
    public static @Nullable
    BusDailySummary getBusDailySummaryById(String id) {
        Realm realm = Realm.getDefaultInstance();
        BusDailySummary busdailysummary = realm.where(BusDailySummary.class).equalTo(ID_KEY, id).findFirst();
        realm.close();

        return busdailysummary;
    }

    /**
     * @return Earliest busdailysummary
     */
    public static @Nullable
    BusDailySummary getOldestBusDailySummaryByGroupId(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        List<BusDailySummary> busDailySummaries = realm.where(BusDailySummary.class)
                .equalTo(GROUP_KEY, groupId)
                .findAllSorted(SUMMARY_DATE_KEY, Sort.ASCENDING);
        realm.close();

        return busDailySummaries.size() > 0 ? busDailySummaries.get(0) : null;
    }

    /**
     * @return Most recent busdailysummary
     */
    public static @Nullable
    BusDailySummary getMostRecentBusDailySummaryByGroupId(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        List<BusDailySummary> busDailySummaries = realm.where(BusDailySummary.class)
                .equalTo(GROUP_KEY, groupId)
                .findAllSorted(SUMMARY_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return busDailySummaries.size() > 0 ? busDailySummaries.get(0) : null;
    }

    public static void delete(String id) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<BusDailySummary> busDailySummaries = realm.where(BusDailySummary.class).equalTo(ID_KEY, id).findAll();
        if (busDailySummaries.size() > 0) {
            busDailySummaries.deleteFromRealm(0);
        }
        realm.commitTransaction();
        realm.close();
    }

    public static RealmResults<BusDailySummary> getBusDailySummariesByRangeAndGroupId(Date[] startEnd, String groupId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<BusDailySummary> busDailySummaries = realm.where(BusDailySummary.class)
                .equalTo(GROUP_KEY, groupId)
                .greaterThanOrEqualTo(SUMMARY_DATE_KEY, startEnd[0])
                .lessThanOrEqualTo(SUMMARY_DATE_KEY, startEnd[1])
                .findAllSorted(SUMMARY_DATE_KEY, Sort.DESCENDING);
        realm.close();

        return busDailySummaries;
    }
}
