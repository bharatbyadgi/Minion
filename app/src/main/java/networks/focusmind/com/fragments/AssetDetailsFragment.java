package networks.focusmind.com.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.activity.HomeActivity;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.AmcDetailDataModel;
import networks.focusmind.com.model.AssetDetailDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.utils.Utils;


public class AssetDetailsFragment extends BaseFragment {


    private Context mContext;
    private RecyclerView mRecyclerView;

    public static AssetDetailsFragment newInstance(Context context, String pageDataUrl) {
        AssetDetailsFragment fragment = new AssetDetailsFragment();
        fragment.mContext = context;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_layout, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.event_page_recycler_view);
        handleDataCall(false, 0, null);
        return rootView;
    }

    private void handleDataCall(final boolean isAmcCall, final int amcId, final AssetDetailViewHolder holder) {
        showProgressDialog("Fetching..");
        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();
            JSONObject responseContainer = Utils.loadJSONFromAsset(mContext, R.raw.asset_detail_response);
            onPageResponseReceived(responseContainer, isAmcCall, null);
        } else {
            if (isAmcCall) {
                HandleVolleyRequest.getAmcData(amcId, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cancelProgressDialog();
                        onPageResponseReceived(response, isAmcCall, holder);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProgressDialog();
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                HandleVolleyRequest.getAssetData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cancelProgressDialog();
                        onPageResponseReceived(response, isAmcCall, null);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        cancelProgressDialog();
                        Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    private void onPageResponseReceived(JSONObject responseContainer, boolean isAmcCall, AssetDetailViewHolder holder) {

        if (!isAmcCall && null != responseContainer && responseContainer.has("assetDetails")) {
            try {
                JSONArray dataArray = responseContainer.getJSONArray("assetDetails");
                Gson gson = new Gson();
                ArrayList<AssetDetailDataModel> assetDetailDataModelList = new ArrayList<>();
                for (int idx = 0; idx < dataArray.length(); idx++) {

                    JSONObject jsonObject = dataArray.getJSONObject(idx);
                    assetDetailDataModelList.add(gson.fromJson(jsonObject.toString(), AssetDetailDataModel.class));
                }
                setUpRecyclerView(assetDetailDataModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (isAmcCall && null != responseContainer && responseContainer.has("amcdetails")) {
            try {
                JSONArray dataArray = responseContainer.getJSONArray("amcdetails");
                Gson gson = new Gson();
                JSONObject jsonObject = dataArray.getJSONObject(0);
                AmcDetailDataModel amcDetailDataModel = gson.fromJson(jsonObject.toString(), AmcDetailDataModel.class);
                showHideAssetAmcDetails(amcDetailDataModel, holder, 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpRecyclerView(final ArrayList<AssetDetailDataModel> assetDetailDataModelArrayList) {
        AssetDetailRecyclerAdapter assetDetailRecyclerAdapter = new AssetDetailRecyclerAdapter(assetDetailDataModelArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(25);
        mRecyclerView.setAdapter(assetDetailRecyclerAdapter);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
    }


    private class AssetDetailRecyclerAdapter extends RecyclerView.Adapter<AssetDetailViewHolder> {

        ArrayList<AssetDetailDataModel> mAssetDetailDataModelList;

        public AssetDetailRecyclerAdapter(ArrayList<AssetDetailDataModel> assetDetailDataModelList) {
            mAssetDetailDataModelList = assetDetailDataModelList;
        }

        @Override
        public AssetDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_detail_list_item, parent, false);
            return new AssetDetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AssetDetailViewHolder holder, final int position) {
            holder.assetName.setText("Asset Name : " + mAssetDetailDataModelList.get(position).getName());
            holder.assetDesc.setText("Asset Desc : " + mAssetDetailDataModelList.get(position).getDescription());
            holder.assetMake.setText("Asset Make : " + mAssetDetailDataModelList.get(position).getMake());
            holder.assetModel.setText("Asset Model : " + mAssetDetailDataModelList.get(position).getModel());
            final View currentView = holder.itemView;

            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showHideAssetAmcDetails(null, holder, mAssetDetailDataModelList.get(position).getAmcid());
                }
            });

            RelativeLayout cardView = (RelativeLayout) currentView.findViewById(R.id.amc_add);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attachAmc(mAssetDetailDataModelList.get(position).getAssetid());
                }
            });

            TextView addReportView = (TextView) currentView.findViewById(R.id.amc_add_report);
            addReportView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addAmcReport(mAssetDetailDataModelList.get(position).getAmcid());
                }
            });

            holder.deleteFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAsset(mAssetDetailDataModelList, position);
                }
            });

            holder.editFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editAsset(mAssetDetailDataModelList.get(position));
                }
            });

        }

        @Override
        public int getItemCount() {
            return mAssetDetailDataModelList.size();
        }

    }

    private void showHideAssetAmcDetails(AmcDetailDataModel amcDetailDataModel, AssetDetailViewHolder holder, int amcId) {
        if (holder.amcDetailLayout.getVisibility() == View.VISIBLE || holder.amcAddLayout.getVisibility() == View.VISIBLE) {
            holder.amcDetailLayout.setVisibility(View.GONE);
            holder.amcAddLayout.setVisibility(View.GONE);
            holder.viewMoreText.setText("View More");
        } else {
            holder.viewMoreText.setText("View Less");
            if (amcId > 0) {
                handleDataCall(true, amcId, holder);
            } else {
                if (null != amcDetailDataModel) {
                    holder.amcDetailLayout.setVisibility(View.VISIBLE);
                    holder.amcAddLayout.setVisibility(View.GONE);
                    holder.amcVendorName.setText("Vendor Name : " + amcDetailDataModel.getVendorName());
                    holder.amcVendorContact.setText("Vendor Contact : " + amcDetailDataModel.getDescription());
                } else {
                    holder.amcDetailLayout.setVisibility(View.GONE);
                    holder.amcAddLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void deleteAsset(ArrayList<AssetDetailDataModel> assetDetailDataModelArrayList, final int position) {
        showProgressDialog("Deleting..");
        HandleVolleyRequest.deleteDataFromServer(VolleyConstants.DELETE_ASSET, assetDetailDataModelArrayList.get(position).getAssetid());
        assetDetailDataModelArrayList.remove(position);
        AssetDetailRecyclerAdapter assetDetailRecyclerAdapter = new AssetDetailRecyclerAdapter(assetDetailDataModelArrayList);
        mRecyclerView.setAdapter(assetDetailRecyclerAdapter);
        cancelProgressDialog();
        Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
    }

    private void editAsset(AssetDetailDataModel assetDetailDataModel) {
        HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postAsset");
        Fragment frag = AddDetailsFragment.newInstance(editTextMap, assetDetailDataModel, "Edit Asset");
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.openAddEditFragment(frag, "Edit Asset");
    }

    private class AssetDetailViewHolder extends RecyclerView.ViewHolder {

        public TextView assetName;
        public TextView assetDesc;
        public TextView assetMake;
        public TextView assetModel;
        public ImageView deleteFacility;
        public ImageView editFacility;
        public TextView amcVendorName;
        public TextView amcVendorContact;
        public TextView viewMoreText;
        public RelativeLayout amcDetailLayout;
        public RelativeLayout amcAddLayout;

        public AssetDetailViewHolder(View itemView) {
            super(itemView);
            assetName = (TextView) itemView.findViewById(R.id.asset_name);
            assetDesc = (TextView) itemView.findViewById(R.id.asset_desc);
            assetMake = (TextView) itemView.findViewById(R.id.asset_make);
            assetModel = (TextView) itemView.findViewById(R.id.asset_model);
            deleteFacility = (ImageView) itemView.findViewById(R.id.delete_asset);
            editFacility = (ImageView) itemView.findViewById(R.id.edit_asset);
            amcVendorName = (TextView) itemView.findViewById(R.id.amc_name);
            amcVendorContact = (TextView) itemView.findViewById(R.id.amc_contact);
            amcDetailLayout = (RelativeLayout) itemView.findViewById(R.id.amc_detail);
            amcAddLayout = (RelativeLayout) itemView.findViewById(R.id.amc_add);
            viewMoreText = (TextView) itemView.findViewById(R.id.seperator_drawable);
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    private void attachAmc(int amcId) {
        String title = "Attach AMC";
        HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postAttachAmc");
        if (null != editTextMap && editTextMap.size() > 0) {
            Fragment frag = AddDetailsFragment.newInstance(editTextMap, null, title, amcId);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.openAddEditFragment(frag, title);
        }
    }

    private void addAmcReport(int amcId) {
        String title = "Add AMC Report";
        HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postAddReport");
        if (null != editTextMap && editTextMap.size() > 0) {
            Fragment frag = AddDetailsFragment.newInstance(editTextMap, null, title);
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.openAddEditFragment(frag, title);
        }
    }


}
