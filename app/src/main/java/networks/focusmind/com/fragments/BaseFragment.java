package networks.focusmind.com.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import networks.focusmind.com.model.BaseDataModel;

public class BaseFragment extends Fragment {

    protected ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);

        return super.onCreateView(inflater, container, savedInstanceState);
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

    protected void updateFragment(BaseDataModel baseDataModel) {

    }
}
