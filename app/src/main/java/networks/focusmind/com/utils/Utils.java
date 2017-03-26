package networks.focusmind.com.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by raghu.desai on 01/12/16.
 */

public class Utils {

    public static JSONObject loadJSONFromAsset(Context context, int resId) {

        JSONObject jsonObject = null;
        try {
            InputStream is = context.getResources().openRawResource(resId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonObject;
    }


    public static HashMap<String, String> getArrayListFromJson(Context context, int resId, String dataKey) {
        HashMap<String, String> map = new HashMap<>();
        JSONObject jsonObject = null;
        try {
            InputStream is = context.getResources().openRawResource(resId);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            try {
                jsonObject = new JSONObject(json);
                JSONArray dataArray = jsonObject.getJSONArray(dataKey);
                for (int idx = 0; idx < dataArray.length(); idx++) {
                    map.put(dataArray.getJSONObject(idx).getString("displayName"), dataArray.getJSONObject(idx).getString("key"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return map;
    }

    public static Map<String, String> jsonObjectToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            String value = object.get(key).toString();
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    public static void setAnimation(final View showView, final View hideView) {

        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
        hideAnimation.setDuration(100);
        hideAnimation.setFillAfter(false);
        hideView.startAnimation(hideAnimation);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                final AlphaAnimation showAnimation = new AlphaAnimation(0.0f, 1.0f);
                showAnimation.setDuration(100);
                showAnimation.setFillAfter(false);
                showView.startAnimation(showAnimation);
                showAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        hideView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        showView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static String getDataAndTime(String timestamp) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        try {
//            Date date = sdf.parse(timestamp);
//            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
//            sdf2.setTimeZone(TimeZone.getTimeZone("IST"));
//            return sdf2.format(date); // Output: 15-02-2014 10:48:08 AM
//        } catch (ParseException exception) {
//            return "Error";
//        }
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp);

    }


    /**
     *
    created by bharat
     */
    @SuppressLint("SimpleDateFormat")
    public static String convertTimestampToDate(long timestamp,Context context) {
        Timestamp tStamp = new Timestamp(timestamp);
        SimpleDateFormat simpleDateFormat;
        if (DateUtils.isToday(timestamp)) {
            simpleDateFormat = new SimpleDateFormat("hh:mm a");
            return /*context.getString(R.string.today)+" " + */simpleDateFormat.format(tStamp);
        } else {
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            return simpleDateFormat.format(tStamp);
        }
    }


    public static void showToast(Context context, String str) {

        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


    public static void launchBrowser(Context context, String url) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static void makeCall(Context context,String phoneNo){

        Intent intentCall = new Intent(Intent.ACTION_DIAL);
        intentCall.setData(Uri.parse("tel:" + phoneNo));
        context.startActivity(intentCall);
    }

    public static void shareViaEmail(Context context, String emailId,String subject,String body) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailId, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        return sdf.format(date);
    }

    public static int differenceInTwoDates(String dateString2, String dateString1) {
        System.out.println("am in differenceInTwoDates: "+"dateString2: "+ dateString2 +", dateString1: "+dateString1 );

        SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
        int no_of_days = 0;

        try {

            Date d2 = formatter2.parse(dateString2);
            Date d1 = formatter1.parse(dateString1);

            long diff = d2.getTime() - d1.getTime();
            // System.out.println("am in d2.getTime: "+d2.getTime());
            // System.out.println("am in d1.getTime: "+d1.getTime());
            // System.out.println("am in diff: "+diff);
            no_of_days = (int)(diff/(1000*24*60*60));
            System.out.println("am in no_of_days: "+no_of_days);

            // System.out.println(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return no_of_days;
    }


    public static void hideKeyboard(Context context) {

        try {
            InputMethodManager inputManager =
                    (InputMethodManager) context.
                            getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(
                    ((Activity) context).getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){}
    }



    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);


            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static String getTime(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
            OurDate = OurDate.replace("z","").replace("Z","");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }


    public static String getDate(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
            OurDate = OurDate.replace("z","").replace("Z","");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }
}
