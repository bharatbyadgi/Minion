package networks.focusmind.com.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by bharat on 24/3/17.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

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

    @Override
    public void onClick(View v) {

    }
}
