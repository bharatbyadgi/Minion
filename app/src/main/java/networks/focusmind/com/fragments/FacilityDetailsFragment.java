package networks.focusmind.com.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.activity.HomeActivity;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.BaseDataModel;
import networks.focusmind.com.model.BookingDateDataModel;
import networks.focusmind.com.model.FacilityBookingDataModel;
import networks.focusmind.com.model.FacilityDetailDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.utils.Utils;


public class FacilityDetailsFragment extends BaseFragment {


    private Context mContext;
    private String mPageDataUrl;
    private RecyclerView mRecyclerView;
    private HorizontalDateViewHolder selectedDateCard = null;
    private TextView mStartTimeTv;
    private TextView mDurationTv;
    private ArrayList<FacilityDetailDataModel> mfacilityDetailDataModel;
    private FacilityDetailRecyclerAdapter mFacilityDetailRecyclerAdapter;

    public static FacilityDetailsFragment newInstance(Context context, String pageDataUrl) {
        FacilityDetailsFragment fragment = new FacilityDetailsFragment();
        fragment.mContext = context;
        fragment.mPageDataUrl = pageDataUrl;
        return fragment;
    }

    @Override
    protected void updateFragment(BaseDataModel baseDataModel) {
        if (baseDataModel instanceof FacilityDetailDataModel) {
            FacilityDetailDataModel facilityDetailDataModel = (FacilityDetailDataModel) baseDataModel;
            mfacilityDetailDataModel.add(facilityDetailDataModel);
            if (null != mFacilityDetailRecyclerAdapter) {
                mFacilityDetailRecyclerAdapter.notifyDataSetChanged();
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_event_layout, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.event_page_recycler_view);
        handleDataCall();
        return rootView;
    }

    private void handleDataCall() {
        showProgressDialog("Fetching..");
        if (VolleyConstants.IS_OFFLINE) {
            JSONObject responseContainer = Utils.loadJSONFromAsset(getContext(), R.raw.facility_detail_response);
            onPageResponseReceived(responseContainer);
        } else {
            HandleVolleyRequest.getFacilityData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
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
            try {
                JSONArray dataArray = responseContainer.getJSONArray("facilityDetails");
                ArrayList<FacilityDetailDataModel> facilityDetailDataModelList = new ArrayList<>();
                for (int idx = 0; idx < dataArray.length(); idx++) {
                    Gson gson = new Gson();
                    JSONObject jsonObject = dataArray.getJSONObject(idx);
                    facilityDetailDataModelList.add(gson.fromJson(jsonObject.toString(), FacilityDetailDataModel.class));
                }
                setUpRecyclerView(facilityDetailDataModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpRecyclerView(final ArrayList<FacilityDetailDataModel> facilityDetailDataModelArrayList) {
        mFacilityDetailRecyclerAdapter = new FacilityDetailRecyclerAdapter(facilityDetailDataModelArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(25);
        mRecyclerView.setAdapter(mFacilityDetailRecyclerAdapter);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

    }

    private class FacilityDetailRecyclerAdapter extends RecyclerView.Adapter<FacilityDetailViewHolder> {


        private FacilityDetailRecyclerAdapter(ArrayList<FacilityDetailDataModel> eventDetailDataModelList) {
            mfacilityDetailDataModel = eventDetailDataModelList;
        }

        @Override
        public FacilityDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_detail_list_item, parent, false);
            return new FacilityDetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FacilityDetailViewHolder holder, final int position) {
            holder.facilityName.setText(mfacilityDetailDataModel.get(position).getName());
            final View currentView = holder.itemView;

            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setUpBookingCard(currentView, mfacilityDetailDataModel.get(position));
                }
            });


            holder.deleteFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFacility(mfacilityDetailDataModel.get(position).getFacilityID(), position);
                }
            });

            holder.editFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editFacility(mfacilityDetailDataModel.get(position));
                }
            });

        }

        @Override
        public int getItemCount() {
            return mfacilityDetailDataModel.size();
        }

    }

    private void deleteFacility(int facilityId, final int position) {
        HandleVolleyRequest.deleteDataFromServer(VolleyConstants.GET_FACILITY + "/facility", facilityId);
        mfacilityDetailDataModel.remove(position);
        mFacilityDetailRecyclerAdapter = new FacilityDetailRecyclerAdapter(mfacilityDetailDataModel);
        mRecyclerView.setAdapter(mFacilityDetailRecyclerAdapter);
        Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
    }

    private void editFacility(FacilityDetailDataModel facilityDetailDataModel) {
        HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postFacility");
        Fragment frag = AddDetailsFragment.newInstance(editTextMap, facilityDetailDataModel, "Edit Facility");
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.openAddEditFragment(frag, "Edit Facility");
    }

    private void setUpBookingCard(final View view, final FacilityDetailDataModel facilityDetailDataModel) {
        if (view.findViewById(R.id.collapsed_facility_card).getVisibility() == View.VISIBLE) {
            view.findViewById(R.id.detail_card).setVisibility(View.VISIBLE);
            view.findViewById(R.id.collapsed_facility_card).setVisibility(View.GONE);
            TextView facilityDetailName = (TextView) view.findViewById(R.id.facility_detail_name);
            facilityDetailName.setText(facilityDetailDataModel.getName());
            RecyclerView horizontalRecyclerView = (RecyclerView) view.findViewById(R.id.horizontal_date_list);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            HorizontalDateRecyclerAdapter horizontalDateRecyclerAdapter = new HorizontalDateRecyclerAdapter(prepareDummyDateData());
            horizontalRecyclerView.setLayoutManager(layoutManager);
            horizontalRecyclerView.setAdapter(horizontalDateRecyclerAdapter);
            HorizontalSpaceItemDecoration horizontalSpaceItemDecoration = new HorizontalSpaceItemDecoration(25);
            horizontalRecyclerView.addItemDecoration(horizontalSpaceItemDecoration);
            setUpTimeView(view);
            Button bookButton = (Button) view.findViewById(R.id.book_facility);
            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view2) {
                    view.findViewById(R.id.collapsed_facility_card).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.detail_card).setVisibility(View.GONE);
                    FacilityBookingDataModel facilityBookingDataModel = new FacilityBookingDataModel();
                    facilityBookingDataModel.setFacilityID(facilityDetailDataModel.getFacilityID());
                    facilityBookingDataModel.setStartTime(Long.parseLong("1484631000000"));
                    facilityBookingDataModel.setEndTime(Long.parseLong("1484634600000"));
                    facilityBookingDataModel.setUsername("amsuser");
                    sendBookingRequest(facilityBookingDataModel);
                }
            });

        } else {
            view.findViewById(R.id.detail_card).setVisibility(View.GONE);
            view.findViewById(R.id.collapsed_facility_card).setVisibility(View.VISIBLE);
        }
    }

    private void sendBookingRequest(FacilityBookingDataModel facilityBookingDataModel) {
        showProgressDialog("Booking..");
        String startTimestamp = getBookingStartTimeStamp(selectedDateCard.bookMonth.getText().toString(), mStartTimeTv.getText().toString());
        String endTimestamp = getBookingEndTimeStamp(selectedDateCard.bookMonth.getText().toString(), mStartTimeTv.getText().toString(), mDurationTv.getText().toString());


        JSONObject bookingObject = new JSONObject();
        try {
            bookingObject.put("facilityID", facilityBookingDataModel.getFacilityID());
            bookingObject.put("startTime", startTimestamp);
            bookingObject.put("endTime", endTimestamp);
            bookingObject.put("username", facilityBookingDataModel.getUsername());
        } catch (JSONException exception) {

        }
        HandleVolleyRequest.postDataToServer(VolleyConstants.BOOK_FACILITY + "/" + facilityBookingDataModel.getFacilityID(), bookingObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                cancelProgressDialog();
                Toast.makeText(getContext(), "Booking Successfull !!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cancelProgressDialog();
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getBookingStartTimeStamp(String date, String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        Date d = null;
        try {
            d = sdf.parse(date + " " + startTime);
        } catch (ParseException e) {
            Log.i("", e.getMessage());
        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.getTimeInMillis() + "";
    }

    private String getBookingEndTimeStamp(String date, String startTime, String endTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date dateObject = null;
        try {
            dateObject = df.parse(startTime);
        } catch (ParseException e) {

        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObject);
        String[] endTimeArr = endTime.split(":");
        cal.add(Calendar.HOUR, Integer.parseInt(endTimeArr[0]));
        cal.add(Calendar.MINUTE, Integer.parseInt(endTimeArr[1]));
        String newTime = df.format(cal.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        Date d = null;
        try {
            d = sdf.parse(date + " " + newTime);
        } catch (ParseException e) {

        }
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.getTimeInMillis() + "";
    }

    private void setUpTimeView(View view) {

        mStartTimeTv = (TextView) view.findViewById(R.id.start_time);
        Calendar c = new GregorianCalendar();
        c.setTime(c.getTime());

        if (c.get(Calendar.MINUTE) >= 30) {
            c.add(Calendar.HOUR_OF_DAY, 1);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
        } else {
            c.add(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 30 - Calendar.MINUTE);
            c.set(Calendar.SECOND, 0);
        }
        String hour = c.get(Calendar.HOUR_OF_DAY) + "";
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String min = c.get(Calendar.MINUTE) + "";
        if (min.length() == 1) {
            min = "0" + min;
        }

        mStartTimeTv.setText(hour + ":" + min);
        mDurationTv = (TextView) view.findViewById(R.id.durtion);
        mDurationTv.setText("00:30");

        ImageView startTimeAdd = (ImageView) view.findViewById(R.id.start_time_add);
        startTimeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dispTime = "";
                String[] timeArray = mStartTimeTv.getText().toString().split(":");
                if (timeArray[1].equals("00")) {
                    if (timeArray[0].length() == 1) {
                        timeArray[0] = "0" + timeArray[0];
                    }
                    dispTime = timeArray[0] + ":30";
                } else {
                    if (Integer.parseInt(timeArray[0]) < 24) {
                        String timeHr = (Integer.parseInt(timeArray[0]) + 1) + "";
                        if (timeHr.length() == 1) {
                            timeHr = "0" + timeHr;
                        }
                        dispTime = timeHr + ":00";
                    } else {
                        dispTime = "00:00";
                    }
                }
                mStartTimeTv.setText(dispTime);
            }
        });
        ImageView startTimeSub = (ImageView) view.findViewById(R.id.start_time_sub);
        startTimeSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dispTime = "";
                String[] timeArray = mStartTimeTv.getText().toString().split(":");
                if (timeArray[1].equals("30")) {
                    if (timeArray[0].length() == 1) {
                        timeArray[0] = "0" + timeArray[0];
                    }
                    dispTime = timeArray[0] + ":00";
                } else {
                    if (Integer.parseInt(timeArray[0]) > 0) {
                        String timeHr = (Integer.parseInt(timeArray[0]) - 1) + "";
                        if (timeHr.length() == 1) {
                            timeHr = "0" + timeHr;
                        }
                        dispTime = timeHr + ":00";
                    } else {
                        dispTime = "23:00";
                    }
                }
                mStartTimeTv.setText(dispTime);
            }
        });
        ImageView durationAdd = (ImageView) view.findViewById(R.id.durtion_add);
        durationAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dur = "";
                boolean shouldChange = false;
                String[] duration = mDurationTv.getText().toString().split(":");
                if (duration[1].equals("00")) {
                    shouldChange = true;
                    if (duration[0].length() == 1) {
                        duration[0] = "0" + duration[0];
                    }
                    dur = duration[0] + ":30";
                } else {
                    if (Integer.parseInt(duration[0]) < 3) {
                        String timeHr = (Integer.parseInt(duration[0]) + 1) + "";
                        if (timeHr.length() == 1) {
                            timeHr = "0" + timeHr;
                        }
                        shouldChange = true;
                        dur = timeHr + ":00";
                    }
                }
                if (shouldChange) {
                    mDurationTv.setText(dur);
                }
            }
        });
        ImageView durationSub = (ImageView) view.findViewById(R.id.durtion_sub);
        durationSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dur = "";
                boolean shouldChange = false;
                String[] duration = mDurationTv.getText().toString().split(":");
                if (duration[1].equals("30")) {
                    shouldChange = true;
                    if (duration[0].length() == 1) {
                        duration[0] = "0" + duration[0];
                    }
                    dur = duration[0] + ":00";
                } else {
                    if (Integer.parseInt(duration[0]) > 0) {
                        shouldChange = true;
                        String timeHr = (Integer.parseInt(duration[0]) - 1) + "";
                        if (timeHr.length() == 1) {
                            timeHr = "0" + timeHr;
                        }
                        dur = timeHr + ":30";
                    }
                }
                if (shouldChange) {
                    mDurationTv.setText(dur);
                }
            }
        });
    }

    private ArrayList<BookingDateDataModel> prepareDummyDateData() {
        ArrayList<BookingDateDataModel> bookingDateDataModelArrayList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
        for (int i = 0; i < 7; i++) {
            BookingDateDataModel bookingDateDataModel = new BookingDateDataModel();
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            calendar.add(Calendar.MONTH, 0);
            String date = sdf1.format(calendar.getTime());
            String day = sdf.format(calendar.getTime());
            bookingDateDataModel.setName(day);
            bookingDateDataModel.setMonth(date);
            bookingDateDataModelArrayList.add(bookingDateDataModel);
        }
        return bookingDateDataModelArrayList;
    }

    private class HorizontalDateRecyclerAdapter extends RecyclerView.Adapter<HorizontalDateViewHolder> {

        ArrayList<BookingDateDataModel> mBookingDateDataModelList;

        private HorizontalDateRecyclerAdapter(ArrayList<BookingDateDataModel> bookingDateDataModelList) {
            mBookingDateDataModelList = bookingDateDataModelList;
        }

        @Override
        public HorizontalDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_horizontal_list_item, parent, false);
            view.setTag(view.getId());
            return new HorizontalDateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final HorizontalDateViewHolder holder, int position) {
            if (position == 0) {
                selectedDateCard = holder;
                holder.dateCardLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            holder.bookDate.setText(mBookingDateDataModelList.get(position).getName());
            holder.bookMonth.setText(mBookingDateDataModelList.get(position).getMonth());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedDateCard.dateCardLayout.setBackgroundColor(Color.parseColor("#F0F0F0"));
                    holder.dateCardLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    selectedDateCard = holder;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mBookingDateDataModelList.size();
        }
    }

    private class HorizontalDateViewHolder extends RecyclerView.ViewHolder {

        private TextView bookDate;
        private TextView bookMonth;
        private RelativeLayout dateCardLayout;

        private HorizontalDateViewHolder(View itemView) {
            super(itemView);
            bookDate = (TextView) itemView.findViewById(R.id.book_avl_date);
            bookMonth = (TextView) itemView.findViewById(R.id.book_avl_month);
            dateCardLayout = (RelativeLayout) itemView.findViewById(R.id.facility_calender);
        }
    }

    private class FacilityDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView facilityName;
        private ImageView deleteFacility;
        private ImageView editFacility;

        private FacilityDetailViewHolder(View itemView) {
            super(itemView);
            facilityName = (TextView) itemView.findViewById(R.id.facility_name);
            deleteFacility = (ImageView) itemView.findViewById(R.id.delete_asset);
            editFacility = (ImageView) itemView.findViewById(R.id.edit_asset);
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        private VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
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


    private void listItemSwipeActions() {
        //        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                    // Get RecyclerView item from the ViewHolder
//                    View itemView = viewHolder.itemView;
//
//                    Paint p = new Paint();
//                    if (dX > 0) {
//                        p.setColor(getResources().getColor(R.color.off_white));
//                        Bitmap icon = BitmapFactory.decodeResource(
//                                getContext().getResources(), android.R.drawable.ic_menu_edit);
//                        c.drawBitmap(icon,
//                                (float) itemView.getRight() - 20 - icon.getWidth(),
//                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
//                                p);
//                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
//                                (float) itemView.getBottom(), p);
//                    } else {
//                        p.setColor(getResources().getColor(R.color.off_white));
//                        Bitmap icon = BitmapFactory.decodeResource(
//                                getContext().getResources(), android.R.drawable.ic_menu_delete);
//                        c.drawBitmap(icon,
//                                (float) itemView.getLeft() + 20,
//                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
//                                p);
//                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
//                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
//                    }
//                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
//                    viewHolder.itemView.setAlpha(alpha);
//                    viewHolder.itemView.setTranslationX(dX);
//                } else {
//                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                }
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                if (mShouldSwipe) {
//                    if (swipeDir == LEFT) {
//                        // make api call to delete userDetailDataModelArrayList.get(viewHolder.getOldPosition());
//                        HandleVolleyRequest.deleteDataFromServer(VolleyConstants.GET_FACILITY, facilityDetailDataModelArrayList.get(viewHolder.getAdapterPosition()).getFacilityID(), new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.i("success", "Success");
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e("err", "Error");
//                            }
//                        });
//                        facilityDetailDataModelArrayList.remove(viewHolder.getAdapterPosition());
//                        facilityDetailRecyclerAdapter.notifyDataSetChanged();
//                        mRecyclerView.setAdapter(facilityDetailRecyclerAdapter);
//                    } else if (swipeDir == RIGHT) {
//                        mRecyclerView.setAdapter(facilityDetailRecyclerAdapter);
//                        HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postFacility");
//                        DialogFragment frag = AddDetailsDialogFragment.newInstance(editTextMap, "Update Facility");
//                        frag.setCancelable(false);
//                        frag.show(getFragmentManager(), "dialog");
//                    }
//                }
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


}
