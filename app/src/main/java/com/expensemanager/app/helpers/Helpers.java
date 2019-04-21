package com.expensemanager.app.helpers;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.expensemanager.app.R;
import com.expensemanager.app.main.EApplication;
import com.expensemanager.app.models.BusDailySummary;
import com.expensemanager.app.models.Category;
import com.expensemanager.app.models.Expense;
import com.expensemanager.app.models.Group;
import com.expensemanager.app.models.PhotoSource;
import com.expensemanager.app.models.User;
import com.expensemanager.app.service.enums.EColor;
import com.expensemanager.app.service.enums.EIcon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.expensemanager.app.service.Constant.PICK_PHOTO;
import static com.expensemanager.app.service.Constant.TAKE_PHOTO;

/**
 * Created by Girish Lakshmanan on 8/19/19.
 */

public class Helpers {
    public static final String TAG = Helpers.class.getSimpleName();

    /**
     * Return a readable date format. For example,
     * Today at 10:50 AM
     * Yesterday at 07:13 PM
     * Monday at 06:07 PM
     * 8/16/16 at 10:35 AM
     *
     * @param spentAt
     * @return
     */
    public static String formatCreateAt(Date spentAt) {
        if (spentAt == null) {
            return "";
        }

        StringBuilder readableDate = new StringBuilder();

        // Current calendar
        Calendar currentCalendar = Calendar.getInstance();
        int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_WEEK);

        // Created At calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(spentAt);
        int createdAtWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int createdAtYear = calendar.get(Calendar.YEAR);

        // Use Today, Yesterday and Weekday if createdAt date is in current week.
        if (currentWeek == createdAtWeek && currentYear == createdAtYear) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (currentDay == dayOfWeek) {
                readableDate.append("Today");
            } else if (currentDay == dayOfWeek + 1 || currentDay == dayOfWeek - 7) {
                readableDate.append("Yesterday");
            } else {
                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.US);
                readableDate.append(dayOfWeekFormat.format(spentAt));
            }
        } else {
            SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.US);
            readableDate.append(dayOfWeekFormat.format(spentAt));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
            readableDate.append(" " + dateFormat.format(spentAt));
        }


        return readableDate.toString();
    }

    public static String getYearStringFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        StringBuilder sb = new StringBuilder("Year ")
            .append(calendar.get(Calendar.YEAR));

        return sb.toString();
    }

    public static String getMonthStringFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        StringBuilder sb = new StringBuilder();
        sb.append(calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.US))
            .append(", ")
            .append(calendar.get(Calendar.YEAR));

        return sb.toString();
    }

    public static String getShortMonthStringOnlyFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        SimpleDateFormat format = new SimpleDateFormat("MMM", Locale.US);

        return format.format(date);
    }

    public static String getWeekStartEndString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar weekStart = (Calendar) calendar.clone();
        weekStart.add(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek() - weekStart.get(Calendar.DAY_OF_WEEK));

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_YEAR, 6);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

        return simpleDateFormat.format(weekStart.getTime()) + " - " + simpleDateFormat.format(weekEnd.getTime());
    }

    public static Date[] getDayStartEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(year, month, dayOfMonth, 0, 0, 0);
        Date startDate = monthCalendar.getTime();

        monthCalendar.set(year, month, dayOfMonth, 23, 59, 59);
        Date endDate = monthCalendar.getTime();

        return new Date[]{startDate, endDate};
    }

    public static Date[] getWeekStartEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar weekStart = (Calendar) calendar.clone();
        weekStart.add(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek() - weekStart.get(Calendar.DAY_OF_WEEK));

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_YEAR, 6);

        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(weekStart.get(Calendar.YEAR), weekStart.get(Calendar.MONTH), weekStart.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Date startDate = monthCalendar.getTime();

        monthCalendar.set(weekEnd.get(Calendar.YEAR), weekEnd.get(Calendar.MONTH), weekEnd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date endDate = monthCalendar.getTime();

        return new Date[]{startDate, endDate};
    }

    public static Date[] getMonthStartEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(year, month, 1, 0, 0, 0);

        int numOfDaysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date startDate = monthCalendar.getTime();

        monthCalendar.set(year, month, numOfDaysInMonth, 23, 59, 59);
        Date endDate = monthCalendar.getTime();

        return new Date[]{startDate, endDate};
    }

    public static Date[] getYearStartEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);

        Calendar yearCalendar = Calendar.getInstance();
        yearCalendar.set(year, 0, 1, 0, 0, 0);

        Date startDate = yearCalendar.getTime();

        yearCalendar.set(year, 11, 31, 23, 59, 59);
        Date endDate = yearCalendar.getTime();

        return new Date[]{startDate, endDate};
    }

    public static String getMonthDayYear(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);

        return simpleDateFormat.format(date);
    }

    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String dateToString (Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(date);
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] inputData, int offset, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(inputData, 0, inputData.length, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(inputData, 0, inputData.length, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int getCenterCropDimensionForBitmap(Bitmap bitmap) {
        int dimension;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width >= height) {
            dimension = height;
        } else {
            dimension = width;
        }

        return dimension;
    }

    public static Bitmap padBitmap(Bitmap input, int paddingX, int paddingY) {
        Bitmap outputBitmap = Bitmap.createBitmap(input.getWidth() + 2 * paddingX, input.getHeight() + paddingY * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawBitmap(input, paddingX, paddingY, null);

        return outputBitmap;
    }

    public static Bitmap getCameraIconBitmap(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_camera_alt_white_24dp);
        Bitmap originalBitmap = ((BitmapDrawable)drawable).getBitmap();
        Paint paint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);
        Bitmap cameraIconBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(cameraIconBitmap);
        canvas.drawBitmap(originalBitmap, -1, 1, paint);

        return Helpers.padBitmap(cameraIconBitmap, 20, 20);
    }

    // Get used color set
    public static Set<String> getUsedColorSet(String groupId) {
        Set<String> newUsedColors = new HashSet<>();

        for (Category c : Category.getAllCategoriesByGroupId(groupId)) {
            if (c != null) {
                newUsedColors.add(c.getColor());
            }
        }

        return newUsedColors;
    }

    // Get used icon set
    public static Set<String> getUsedIconSet(String groupId) {
        Set<String> newUsedIcons = new HashSet<>();

        for (Category c : Category.getAllCategoriesByGroupId(groupId)) {
            if (c != null) {
                newUsedIcons.add(c.getIcon());
            }
        }

        return newUsedIcons;
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            Log.e(TAG, "Error checking internet.", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "Error checking internet.", e);
        }
        return false;
    }

    public static int getDayOfWeek(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayOfMonth(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthOfYear(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.MONTH);
    }

    public static int getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentMonthOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public static void getStartEndDateOfMonth(Date[] startend, Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        startend[0] = calendar.getTime();
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, maxDays);
        startend[1] = calendar.getTime();
    }

    public static int[] getStartEndDay(Date[] startEnd) {
        int[] startEndDay = new int[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startEnd[0]);
        startEndDay[0] = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(startEnd[1]);
        startEndDay[1] = calendar.get(Calendar.DAY_OF_MONTH);

        return startEndDay;
    }

    public static ArrayList<Date[]> getAllYears(String groupId) {
        ArrayList<Date[]> years = new ArrayList<>();

        Date startDate = Calendar.getInstance().getTime();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        BusDailySummary oldestBusDailySummary = BusDailySummary.getOldestBusDailySummaryByGroupId(groupId);
        
        if (oldestBusDailySummary == null) {
            return null;
        }

        Date endDate = oldestBusDailySummary.getSummaryDate();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        int endYear = endCalendar.get(Calendar.YEAR);

        while (startCalendar.get(Calendar.YEAR) >= endYear) {
            Date[] startEnd = Helpers.getYearStartEndDate(startCalendar.getTime());
            years.add(startEnd);
            startCalendar.add(Calendar.YEAR, -1);
        }

        return years;
    }

    public static ArrayList<Date[]> getAllMonths(String groupId) {
        ArrayList<Date[]> months = new ArrayList<>();

        Date startDate = Calendar.getInstance().getTime();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        BusDailySummary oldestBusDailySummary = BusDailySummary.getOldestBusDailySummaryByGroupId(groupId);

        if (oldestBusDailySummary == null) {
            return null;
        }

        Date endDate = oldestBusDailySummary.getSummaryDate();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endMonth = endCalendar.get(Calendar.MONTH);

        while (startCalendar.get(Calendar.YEAR) > endYear ||
            (startCalendar.get(Calendar.YEAR) == endYear && startCalendar.get(Calendar.MONTH) >= endMonth)) {

            Date[] startEnd = Helpers.getMonthStartEndDate(startCalendar.getTime());
            months.add(startEnd);
            startCalendar.add(Calendar.MONTH, -1);
        }

        return months;
    }

    public static ArrayList<Date[]> getAllWeeks(String groupId) {
        ArrayList<Date[]> weeks = new ArrayList<>();

        Date startDate = Calendar.getInstance().getTime();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);

        BusDailySummary oldestBusDailySummary = BusDailySummary.getOldestBusDailySummaryByGroupId(groupId);

        if (oldestBusDailySummary == null) {
            return null;
        }

        Date endDate = oldestBusDailySummary.getSummaryDate();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        int endYear = endCalendar.get(Calendar.YEAR);
        int endWeekOfYear = endCalendar.get(Calendar.WEEK_OF_YEAR);

        while (startCalendar.get(Calendar.YEAR) > endYear ||
            (startCalendar.get(Calendar.YEAR) == endYear && startCalendar.get(Calendar.WEEK_OF_YEAR) >= endWeekOfYear)) {

            Date[] startEnd = Helpers.getWeekStartEndDate(startCalendar.getTime());
            weeks.add(startEnd);
            startCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        return weeks;
    }

    public static float formatNumToFloat(double num) {
        float newNum = (int) (num * 100);
        return newNum / 100;
    }

    public static double formatNumToDouble(double num) {
        double newNum = (int) (num * 100);
        return newNum / 100;
    }

    public static String getDayOfWeekString(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.US);
        return format.format(calendar.getTime());
    }

    public static String getDayOfMonthString(int day) {
        switch (day % 10) {
            case 1:
                return String.valueOf(day) + "st";
            case 2:
                return String.valueOf(day) + "nd";
            case 3:
                return String.valueOf(day) + "rd";
            default:
                return String.valueOf(day) + "th";
        }
    }

    public static String getMonthOfYearString(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        SimpleDateFormat format = new SimpleDateFormat("MMM", Locale.US);
        return format.format(calendar.getTime());
    }

    public static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static Date getLastWeekOfYear(Date date) {
        // Set time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Go to last week
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static String getLoginUserId() {
        Context context = EApplication.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_session_key), 0);
        return sharedPreferences.getString(User.USER_ID, null);
    }

    public static String getRandomColor(Set<String> usedColors) {
        List<String> colors = colors = EColor.getAllColors();
        Random ran = new Random();
        int pos = ran.nextInt(colors.size());
        String color = colors.get(pos);
        while (usedColors != null && usedColors.contains(color)) {
            pos = ran.nextInt(colors.size());
            color = colors.get(pos);
        }
        return color;
    }

    public static String getRandomIcon(Set<String> usedIcons) {
        List<String> icons = EIcon.getAllIcons();
        Random ran = new Random();
        int pos = ran.nextInt(icons.size());
        String icon = icons.get(pos);
        while (usedIcons != null && usedIcons.contains(icon)) {
            pos = ran.nextInt(icons.size());
            icon = icons.get(pos);
        }
        return icon;
    }

    public static boolean hasGroup() {
        String groupId = Helpers.getCurrentGroupId();
        if (groupId == null) {
            Toast.makeText(EApplication.getInstance(), R.string.select_group_hint, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void loadIconPhoto(ImageView view, String url) {
        Glide.with(view.getContext())
            .load(url)
            .placeholder(R.drawable.default_profile_image)
            .fitCenter()
            .bitmapTransform(new RoundedCornersTransformation(view.getContext(), 100, 0))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view);
    }

    public static void loadProfilePhoto(ImageView view, String url) {
        Glide.with(view.getContext())
            .load(url)
            .placeholder(R.drawable.default_profile_image)
            .fitCenter()
            .bitmapTransform(new RoundedCornersTransformation(view.getContext(), 200, 0))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view);
    }

    public static void loadProfilePhoto(ImageView view, String url, Drawable placeholder) {
        Glide.with(view.getContext())
            .load(url)
            .placeholder(placeholder)
            .fitCenter()
            .bitmapTransform(new RoundedCornersTransformation(view.getContext(), 200, 0))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view);
    }

    public static void closeSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = ((Activity) context).getCurrentFocus();
        if (inputMethodManager != null && view != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static @Nullable String getSyncTimeKey(String classTag, String groupId) {
        if (classTag == null || groupId == null) {
            return null;
        }
        return classTag + groupId;
    }

    public static long getSyncTimeInMillis(String syncTimeKey) {
        Context context = EApplication.getInstance();
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_session_key), 0);

        return sharedPreferences.getLong(syncTimeKey, 0);
    }

    public static final long DAY_IN_MILLIS = 86400000L;
    public static boolean needToSync(long lastTimeInMillis) {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        return currentTimeInMillis - lastTimeInMillis >= DAY_IN_MILLIS;
    }

    public static void saveSyncTime(Activity activity, String syncTimeKey, long syncTimeInMillis) {
        SharedPreferences.Editor sharedPreferencesEditor = activity.getSharedPreferences(activity.getString(R.string.shared_preferences_session_key),
            Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putLong(syncTimeKey, syncTimeInMillis).apply();
    }

    public static String doubleToCurrency(double amount) {
        return String.format("₹%.0f", amount);
    }

    public static PhotoSourceAdapter getPhotoSourceAdapter(Context context) {
        ArrayList<PhotoSource> photoSources = new ArrayList<>();

        PhotoSource takePhoto = new PhotoSource();
        takePhoto.setIconResId(R.drawable.ic_camera_alt_white_24dp);
        takePhoto.setTitle(TAKE_PHOTO);

        PhotoSource pickPhoto = new PhotoSource();
        pickPhoto.setIconResId(R.drawable.ic_photo_white_24dp);
        pickPhoto.setTitle(PICK_PHOTO);

        photoSources.add(takePhoto);
        photoSources.add(pickPhoto);

        return new PhotoSourceAdapter(context, photoSources);
    }

    public static String getCurrentGroupId() {
        Context context = EApplication.getInstance();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(context.getString(R.string.shared_preferences_session_key), 0);


        Log.d(TAG,"^^^^^^^^^^getCurrentGroupId()" + sharedPreferences.getString(Group.GROUP_ID_KEY, null));
        return sharedPreferences.getString(Group.GROUP_ID_KEY, null);
    }

    public static  void centerRevealEnter(View view) {
        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }
}
