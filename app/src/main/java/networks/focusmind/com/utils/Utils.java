package networks.focusmind.com.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

}
