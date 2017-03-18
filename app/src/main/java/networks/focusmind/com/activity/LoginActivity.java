package networks.focusmind.com.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.storage.SharedPreferenceManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mUserName, mPassword;
    private SharedPreferenceManager mSharedpref;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);
        mSharedpref = new SharedPreferenceManager(this);

        if (!TextUtils.isEmpty(mSharedpref.getUserSpecificTokenPreference())) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        mUserName = (EditText) findViewById(R.id.uname);
        mPassword = (EditText) findViewById(R.id.pword);

        Button register = (Button) findViewById(R.id.register);
        if (null != register) {
            register.setOnClickListener(this);
        }

    }

    private class handleOAuthOperation extends AsyncTask<String, Void, String> {
        String userName = "";
        String passWord = "";

        @Override
        protected String doInBackground(String... params) {
            String accessToken = "";
            userName = params[0];
            passWord = params[1];
            OkHttpJsonRequest jsonRequest = new OkHttpJsonRequest();
            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", userName)
                    .add("password", passWord)
                    .add("client_id", "admin-cli")
                    .build();
            try {
                JSONObject jsonObject = jsonRequest.post(VolleyConstants.URL.trim() + ":9080/auth/realms/ams/protocol/openid-connect/token", requestBody);
                if (jsonObject.has("access_token") && !jsonObject.isNull("access_token")) {
                    accessToken = jsonObject.getString("access_token");
                } else if (jsonObject.has("error_description") && jsonObject.getString("error_description").equals("Account is not fully set up")) {
                    return "error";
                } else {
                    return "";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (!TextUtils.isEmpty(response)) {
                Log.i("TAG", response);
                if (response.equals("error")) {
                    showResetPasswordDialog();
                } else {
                    mSharedpref.setUserSpecificTokenPreference(response);
                    mSharedpref.setUserName(userName);
                    showHomeScreen();
                }
            } else{
                Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showProgressDialog(String message) {
        if (null != mProgressDialog) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void cancelProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showResetPasswordDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("New Password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != input.getText() && !TextUtils.isEmpty(input.getText().toString())) {
                            makeResetSetPasswordApiCall(input.getText().toString());
                        } else {
                            Toast.makeText(LoginActivity.this, "Please enter a valid password", Toast.LENGTH_LONG).show();
                            showResetPasswordDialog();
                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void makeResetSetPasswordApiCall(String newPassword) {
        showProgressDialog("Saving..");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password-new", newPassword);
            jsonObject.put("password-confirm", newPassword);
        } catch (JSONException exception) {

        }
        HandleVolleyRequest.makeResetPasswordCall(mSharedpref.getUserName(), jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                cancelProgressDialog();
                Toast.makeText(LoginActivity.this, "Saved Successfully..", Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cancelProgressDialog();
                Toast.makeText(LoginActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class OkHttpJsonRequest {
        OkHttpClient client = new OkHttpClient();

        JSONObject post(String url, RequestBody body) throws IOException, JSONException {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            String userName = "";
            String passWord = "";
            if (VolleyConstants.IS_OFFLINE) {
                showHomeScreen();
            } else {
                if (null != mUserName && !TextUtils.isEmpty(mUserName.getText().toString()) && null != mPassword && !TextUtils.isEmpty(mPassword.getText().toString())) {
                    userName = mUserName.getText().toString();
                    passWord = mPassword.getText().toString();
                    mSharedpref.setUserName(userName);
                    new handleOAuthOperation().execute(userName, passWord);
                } else {
                    Toast.makeText(this, "Please enter a valid credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void showHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private class OnError implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {
            return false;
        }
    }
}
