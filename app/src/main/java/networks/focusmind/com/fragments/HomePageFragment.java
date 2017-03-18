package networks.focusmind.com.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.HomePageEventDataModel;
import networks.focusmind.com.model.HomePageFacilityDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.utils.Utils;

import static android.app.Activity.RESULT_OK;


public class HomePageFragment extends BaseFragment {

    private static final String LOG_TAG = "Barcode Scanner API";
    private static final int PHOTO_REQUEST = 10;
    private RecyclerView mEventRecyclerView;
    private RecyclerView mFacilityRecyclerView;
    private BarcodeDetector detector;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private Uri imageUri;

    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        mEventRecyclerView = (RecyclerView) rootView.findViewById(R.id.event_recycler_view);
        mFacilityRecyclerView = (RecyclerView) rootView.findViewById(R.id.facility_recycler_view);

        Button button = (Button) rootView.findViewById(R.id.scancode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityCompat.requestPermissions(getActivity(), new
//                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                takePicture();
            }
        });
        detector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            Toast.makeText(getContext(), "Could not set up the detector!", Toast.LENGTH_LONG).show();
        }
        handleDataCall();
        return rootView;
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        getContext().sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                Bitmap bitmap = decodeBitmapUri(getContext(), imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        Toast.makeText(getContext(), "Code Value : " + code.displayValue, Toast.LENGTH_LONG).show();

                        //Required only if you need to extract the type of barcode
                        int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.CONTACT_INFO:
                                Log.i(LOG_TAG, code.contactInfo.title);
                                break;
                            case Barcode.EMAIL:
                                Log.i(LOG_TAG, code.email.address);
                                break;
                            case Barcode.ISBN:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.PHONE:
                                Log.i(LOG_TAG, code.phone.number);
                                break;
                            case Barcode.PRODUCT:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.SMS:
                                Log.i(LOG_TAG, code.sms.message);
                                break;
                            case Barcode.TEXT:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                            case Barcode.URL:
                                Log.i(LOG_TAG, "url: " + code.url.url);
                                break;
                            case Barcode.WIFI:
                                Log.i(LOG_TAG, code.wifi.ssid);
                                break;
                            case Barcode.GEO:
                                Log.i(LOG_TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Log.i(LOG_TAG, code.calendarEvent.description);
                                break;
                            case Barcode.DRIVER_LICENSE:
                                Log.i(LOG_TAG, code.driverLicense.licenseNumber);
                                break;
                            default:
                                Log.i(LOG_TAG, code.rawValue);
                                break;
                        }
                    }
                    if (barcodes.size() == 0) {
                        Toast.makeText(getContext(), "Scan Failed: Found nothing to scan", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Could not set up the detector!", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void handleDataCall() {
        showProgressDialog("Fetching..");
        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();
            JSONObject responseContainer = Utils.loadJSONFromAsset(getContext(), R.raw.home_page_response);
            onPageResponseReceived(responseContainer);
        } else {
            HandleVolleyRequest.getEventsData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    cancelProgressDialog();
                    onPageResponseReceived(response);
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

    private void onPageResponseReceived(JSONObject responseContainer) {

        if (null != responseContainer) {
            ArrayList<HomePageEventDataModel> eventDetailDataModelList = new ArrayList<>();
            ArrayList<HomePageFacilityDataModel> facilityDetailDataModelList = new ArrayList<>();
            try {
                if (responseContainer.has("eventdetails")) {
                    JSONArray dataArray = responseContainer.getJSONArray("eventdetails");
                    for (int idx = 0; idx < dataArray.length(); idx++) {
                        Gson gson = new Gson();
                        JSONObject jsonObject = dataArray.getJSONObject(idx);
                        eventDetailDataModelList.add(gson.fromJson(jsonObject.toString(), HomePageEventDataModel.class));
                    }
                }
                if (responseContainer.has("facilityDetails")) {
                    JSONArray dataArray = responseContainer.getJSONArray("facilityDetails");
                    for (int idx = 0; idx < dataArray.length(); idx++) {
                        Gson gson = new Gson();
                        JSONObject jsonObject = dataArray.getJSONObject(idx);
                        facilityDetailDataModelList.add(gson.fromJson(jsonObject.toString(), HomePageFacilityDataModel.class));
                    }
                }
                setUpRecyclerView(eventDetailDataModelList, facilityDetailDataModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpRecyclerView(final ArrayList<HomePageEventDataModel> homePageEventDataModelArrayList, final ArrayList<HomePageFacilityDataModel> homePageFacilityDataModelArrayList) {
        HomePageEventRecyclerAdapter homePageEventRecyclerAdapter = new HomePageEventRecyclerAdapter(homePageEventDataModelArrayList);
        mEventRecyclerView.setAdapter(homePageEventRecyclerAdapter);
        HomePageFacilityRecyclerAdapter homePageFacilityRecyclerAdapter = new HomePageFacilityRecyclerAdapter(homePageFacilityDataModelArrayList);
        mFacilityRecyclerView.setAdapter(homePageFacilityRecyclerAdapter);
        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mEventRecyclerView.setLayoutManager(layoutManager1);
        mFacilityRecyclerView.setLayoutManager(layoutManager2);
        HorizontalSpaceItemDecoration horizontalSpaceItemDecoration = new HorizontalSpaceItemDecoration(25);
        mFacilityRecyclerView.addItemDecoration(horizontalSpaceItemDecoration);
        mEventRecyclerView.addItemDecoration(horizontalSpaceItemDecoration);
    }


    private class HomePageEventRecyclerAdapter extends RecyclerView.Adapter<HomePageChildViewHolder> {

        ArrayList<HomePageEventDataModel> mHomePageEventDataModelArrayList;

        public HomePageEventRecyclerAdapter(ArrayList<HomePageEventDataModel> homePageEventDataModelArrayList) {
            mHomePageEventDataModelArrayList = homePageEventDataModelArrayList;
        }

        @Override
        public HomePageChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_detail_list_item, parent, false);
            return new HomePageChildViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HomePageChildViewHolder holder, final int position) {
            holder.name.setText(mHomePageEventDataModelArrayList.get(position).getEventName());
//            holder.desc.setText(mHomePageEventDataModelArrayList.get(position).getDescription());
            holder.creator.setText(mHomePageEventDataModelArrayList.get(position).getEventCreator());
            holder.scheduleTime.setText(mHomePageEventDataModelArrayList.get(position).getEventScheduleTime());
        }

        @Override
        public int getItemCount() {
            return mHomePageEventDataModelArrayList.size();
        }

    }

    private class HomePageChildViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView desc;
        public TextView creator;
        public TextView scheduleTime;

        public HomePageChildViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            creator = (TextView) itemView.findViewById(R.id.creator);
            scheduleTime = (TextView) itemView.findViewById(R.id.schedule_time);
        }
    }


    private class HomePageFacilityRecyclerAdapter extends RecyclerView.Adapter<HomePageChildViewHolder> {

        ArrayList<HomePageFacilityDataModel> mHomePageFacilityDataModelArrayList;

        public HomePageFacilityRecyclerAdapter(final ArrayList<HomePageFacilityDataModel> homePageFacilityDataModelArrayList) {
            mHomePageFacilityDataModelArrayList = homePageFacilityDataModelArrayList;
        }

        @Override
        public HomePageChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_detail_list_item, parent, false);
            return new HomePageChildViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HomePageChildViewHolder holder, final int position) {

            holder.name.setText(mHomePageFacilityDataModelArrayList.get(position).getName());
//            holder.desc.setText(mHomePageFacilityDataModelArrayList.get(position).getDescription());
            holder.scheduleTime.setText(mHomePageFacilityDataModelArrayList.get(position).getBookingTime());
        }

        @Override
        public int getItemCount() {
            return mHomePageFacilityDataModelArrayList.size();
        }

    }

    public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int horizontalSpaceHeight;

        private HorizontalSpaceItemDecoration(int horizontalSpaceHeight) {
            this.horizontalSpaceHeight = horizontalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.left = horizontalSpaceHeight;
        }
    }
}
