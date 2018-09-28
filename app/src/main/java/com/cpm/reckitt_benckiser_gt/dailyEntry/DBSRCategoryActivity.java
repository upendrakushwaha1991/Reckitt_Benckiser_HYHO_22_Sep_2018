package com.cpm.reckitt_benckiser_gt.dailyEntry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.delegates.CoverageBean;
import com.cpm.reckitt_benckiser_gt.getterSetter.CategoryMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.upload.Retrofit_method.UploadImageWithRetrofit;
import com.cpm.reckitt_benckiser_gt.utilities.AlertandMessages;
import com.cpm.reckitt_benckiser_gt.utilities.CommonFunctions;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class DBSRCategoryActivity extends AppCompatActivity {

    ExpandableListAdapter adapter;
    JourneyPlan journeyPlan;
    RBGTDatabase db;
    Context context;
    String _pathforcheck = "", image = "", path = "", msg = "";
    ArrayList<CategoryMaster> listDataHeader;
    String username, metadata_global;
    private SharedPreferences preferences;
    FloatingActionButton fab;
    Activity activity;
    ExpandableListView expandableListView;
    String app_ver;
    int global_position = 0;
    double lat, lon;
    boolean errorFlag = false;
    ArrayList<Integer> error_position_header = new ArrayList();
    ArrayList<Integer> error_position_child = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbsrcategory);
        declaration();
        prepareList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatedata()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(
                            "Are you sure you want to save your data?")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @SuppressWarnings("resource")
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            try {
                                                if (isCoverageFilled()) {
                                                    long commonId = db.InsertCategoryDBSRData(username, journeyPlan, listDataHeader);
                                                    if (commonId > 0) {
                                                        db.updateCheckoutStatus(String.valueOf(journeyPlan.getStoreId()), CommonString.KEY_VALID, CommonString.TABLE_Journey_Plan_DBSR_Saved);
                                                        AlertandMessages.showToastMsg(context, "Data Saved");
                                                        finish();
                                                        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                                    } else {
                                                        AlertandMessages.showToastMsg(context, "Error in Data Saving");
                                                    }
                                                } else {
                                                    AlertandMessages.showToastMsg(context, "Error in Data Saving");
                                                }
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }

                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert1 = builder1.create();
                    alert1.show();
                } else {
                    AlertandMessages.showToastMsg(context, msg);
                }
            }
        });
    }

    public boolean isCoverageFilled() {
        boolean isvalid = false;
        db.open();
        ArrayList<CoverageBean> coverageBeans = db.getSpecificCoverageData(journeyPlan.getVisitDate(), String.valueOf(journeyPlan.getStoreId()));
        if (coverageBeans.size() > 0) {
            if (db.insertJCP_DBSRSavedData(journeyPlan)) {
                isvalid = true;
            }
        } else {
            if (db.insertJCP_DBSRSavedData(journeyPlan)) {
                isvalid = true;
            }
            CoverageBean cdata = new CoverageBean();

            cdata.setStoreId(String.valueOf(journeyPlan.getStoreId()));
            cdata.setVisitDate(journeyPlan.getVisitDate());
            cdata.setUserId(username);
            cdata.setReason("");
            cdata.setReasonid("0");
            cdata.setLatitude(String.valueOf(lat));
            cdata.setLongitude(String.valueOf(lon));
            cdata.setImage("");
            cdata.setRemark("");
            cdata.setCkeckout_image("");

            //CoverageUpload(cdata);
            db.open();
            if ((db.InsertCoverageData(cdata) > 0) && (db.updateCheckoutStatus(String.valueOf(journeyPlan.getStoreId()), CommonString.KEY_CHECK_IN, CommonString.TABLE_Journey_Plan_DBSR_Saved) > 0)) {
                isvalid = true;
                new GeoTagUpload(cdata).execute();
            } else {
                AlertandMessages.showToastMsg(context, "Error in saving data");
                isvalid = false;
            }
        }
        return isvalid;
    }

    public class GeoTagUpload extends AsyncTask<Void, Void, String> {

        private CoverageBean cdata;
        boolean ResultFlag = true;
        String strflag = "";

        GeoTagUpload(CoverageBean cdata) {
            this.cdata = cdata;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                RBGTDatabase db = new RBGTDatabase(context);
                db.open();

                //coverage = db.getCoverageWithStoreID_Data(store_id);
                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                JSONObject jsonObject;
                String jsonString2 = "", result = "5";

                //region Coverage Data
                jsonObject = new JSONObject();

                jsonObject.put("StoreId", cdata.getStoreId());
                jsonObject.put("VisitDate", cdata.getVisitDate());
                jsonObject.put("Latitude", cdata.getLatitude());
                jsonObject.put("Longitude", cdata.getLongitude());
                jsonObject.put("ReasonId", cdata.getReasonid());
                jsonObject.put("SubReasonId", "0");
                jsonObject.put("Remark", cdata.getRemark());
                jsonObject.put("ImageName", cdata.getImage());
                jsonObject.put("AppVersion", app_ver);
                jsonObject.put("UploadStatus", CommonString.KEY_CHECK_IN);
                jsonObject.put("Checkout_Image", cdata.getCkeckout_image());
                jsonObject.put("UserId", username);

                jsonString2 = jsonObject.toString();
                result = upload.downloadDataUniversal(jsonString2, CommonString.COVERAGE_DETAIL_CLIENT);

                if (result.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                    throw new SocketTimeoutException();
                } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                    throw new IOException();
                } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                    throw new JsonSyntaxException("Invalid json");
                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    throw new Exception();
                } else {
                    int mid = 0;
                    try {
                        mid = Integer.parseInt(result);
                        if (mid > 0) {
                            return CommonString.KEY_SUCCESS;
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        throw new NumberFormatException();
                    }
                }

            } catch (MalformedURLException e) {

                ResultFlag = false;
                strflag = CommonString.MESSAGE_EXCEPTION;

            } catch (SocketTimeoutException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_SOCKETEXCEPTION;

            } catch (InterruptedIOException e) {

                ResultFlag = false;
                strflag = CommonString.MESSAGE_EXCEPTION;

            } catch (IOException e) {

                ResultFlag = false;
                strflag = CommonString.MESSAGE_SOCKETEXCEPTION;

            } catch (NumberFormatException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_NUMBER_FORMATE_EXEP;

            } catch (Exception e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_EXCEPTION;
            }
            if (ResultFlag) {
                return CommonString.KEY_SUCCESS;
            } else {
                return strflag;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

            } else {
                RBGTDatabase db = new RBGTDatabase(context);
                db.open();
                db.deleteTableWithStoreID(cdata.getStoreId());
                AlertandMessages.showToastMsg(context, getString(R.string.datanotfound) + " " + result);
            }
        }

    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private ArrayList<CategoryMaster> listDataHeader; // header titles

        public ExpandableListAdapter(Context context, ArrayList<CategoryMaster> listDataHeader) {
            this._context = context;
            this.listDataHeader = listDataHeader;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.listDataHeader.get(groupPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressLint("NewApi")
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final CategoryMaster headerTitle = (CategoryMaster) getGroup(groupPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.item_child_dbsr_category, null);
                holder = new ViewHolder();
                holder.switch_exists = (ToggleButton) convertView.findViewById(R.id.switch_exists);
                holder.image_window = (ImageButton) convertView.findViewById(R.id.image_window);
                holder.cardview_exists = (CardView) convertView.findViewById(R.id.cardview_exists);
                holder.lay_Camera = (LinearLayout) convertView.findViewById(R.id.lay_Camera);
                holder.txt_header = (TextView) convertView.findViewById(R.id.txt_header);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txt_header.setText(headerTitle.getCategory());
            final ViewHolder finalHolder = holder;
            holder.switch_exists.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ToggleButton) v).isChecked()) {
                        finalHolder.lay_Camera.setVisibility(View.VISIBLE);
                        //lay_reason.setVisibility(View.GONE);
                        headerTitle.setExist(true);
                        //reason_spinner.setSelection(0);
                        if (headerTitle.getImage() != null
                                && !headerTitle.getImage().equalsIgnoreCase("")) {
                            if (new File(CommonString.FILE_PATH + headerTitle.getImage()).exists()) {
                                new File(CommonString.FILE_PATH + headerTitle.getImage()).delete();
                            }
                            headerTitle.setImage("");
                        }
                        expandableListView.invalidateViews();
                    } else {
                        finalHolder.lay_Camera.setVisibility(View.VISIBLE);
                        //lay_reason.setVisibility(View.VISIBLE);
                        headerTitle.setExist(false);
                        if (headerTitle.getImage() != null
                                && !headerTitle.getImage().equalsIgnoreCase("")) {
                            if (new File(CommonString.FILE_PATH + headerTitle.getImage()).exists()) {
                                new File(CommonString.FILE_PATH + headerTitle.getImage()).delete();
                            }
                            headerTitle.setImage("");
                        }
                        expandableListView.invalidateViews();
                    }
                }
            });

            holder.image_window.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    global_position = groupPosition;
                    String intime = CommonFunctions.getCurrentTime();
                    _pathforcheck = journeyPlan.getStoreId() + "_Cat_dbsr-" + groupPosition + "_" + journeyPlan.getVisitDate().replace("/", "") + "_" + intime.replace(":", "") + ".jpg";
                    path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(context, path, null,false);
                }
            });

            if (headerTitle.isExist() == true) {
                holder.switch_exists.setChecked(true);
                holder.lay_Camera.setVisibility(View.VISIBLE);
                //lay_reason.setVisibility(View.GONE);
                expandableListView.expandGroup(groupPosition);
            } else {
                holder.switch_exists.setChecked(false);
                expandableListView.collapseGroup(groupPosition);
                holder.lay_Camera.setVisibility(View.INVISIBLE);
                //lay_reason.setVisibility(View.VISIBLE);
            }

            if (headerTitle.getImage() != null && !headerTitle.getImage().equalsIgnoreCase("")) {
                holder.image_window.setBackgroundResource(R.mipmap.camera_green);
            } else {
                holder.image_window.setBackgroundResource(R.mipmap.camera_pink);
            }

            if (headerTitle.isExist() == true) {
                holder.switch_exists.setChecked(true);
            } else {
                holder.switch_exists.setChecked(false);
            }

            if (errorFlag) {
                if (error_position_header.contains(groupPosition)) {
                    holder.cardview_exists.setCardBackgroundColor(Color.RED);
                } else {
                    holder.cardview_exists.setCardBackgroundColor(getResources().getColor(R.color.lightpink));
                }
            } else {
                holder.cardview_exists.setCardBackgroundColor(getResources().getColor(R.color.lightpink));
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public class ViewHolder {
        ToggleButton switch_exists;
        ImageButton image_window;
        CardView cardview_exists;
        LinearLayout lay_Camera;
        TextView txt_header;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                        if (listDataHeader.get(global_position).getImage() != null
                                && !listDataHeader.get(global_position).getImage().equalsIgnoreCase("")) {
                            if (new File(CommonString.FILE_PATH + listDataHeader.get(global_position).getImage()).exists()) {
                                new File(CommonString.FILE_PATH + listDataHeader.get(global_position).getImage()).delete();
                            }
                            listDataHeader.get(global_position).setImage("");
                        }
                        //String metadata = CommonFunctions.getMetadataAtImagesFromPref(metadata_global, "Window Image");
                        //CommonFunctions.addMetadataAndTimeStampToImage(context, CommonString1.FILE_PATH + _pathforcheck, metadata);
                        image = _pathforcheck;
                        listDataHeader.get(global_position).setImage(image);
                        _pathforcheck = "";
                        adapter.notifyDataSetChanged();
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    void prepareList() {
        listDataHeader = db.getCategoryDBSRData(journeyPlan);
        if (listDataHeader.size() > 0) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
        adapter = new ExpandableListAdapter(context, listDataHeader);
        expandableListView.setAdapter(adapter);
    }

    public Boolean validatedata() {
        boolean isgood = true;
        expandableListView.clearFocus();
        errorFlag = false;
        error_position_header.clear();
        error_position_child.clear();
        listheaderloop:
        for (int i = 0; i < listDataHeader.size(); i++) {
            CategoryMaster windowMaster = listDataHeader.get(i);
            if (windowMaster.isExist() == true) {
                if (windowMaster.getImage() == null || windowMaster.getImage().equalsIgnoreCase("")) {
                    isgood = false;
                    errorFlag = true;
                    error_position_header.add(i);
                    msg = "Please click image";
                    expandableListView.invalidateViews();
                    break listheaderloop;
                } else if (windowMaster.isExist() == true) {
                    /*ArrayList<ChecklistGetterSetter> checklist = listDataChild.get(windowMaster);
                    for (int j = 0; j < checklist.size(); j++) {
                        ChecklistGetterSetter checkgetset = checklist.get(j);
                        if (checkgetset.getANSWER_CD() == null || checkgetset.getANSWER_CD().equalsIgnoreCase("") || checkgetset.getANSWER_CD().equalsIgnoreCase("0")) {
                            isgood = false;
                            errorFlag = true;
                            error_position_header.add(i);
                            error_position_child.add(j);
                            msg = "Please select answer from checklist";
                            expandableListView.invalidateViews();
                            break listheaderloop;
                        }
                    }*/
                }
            } /*else if (windowMaster.isExist() == false) {
                if (windowMaster.getReasonId() == 0) {
                    isgood = false;
                    errorFlag = true;
                    error_position_header.add(i);
                    msg = "Please Select Reason";
                    expandableListView.invalidateViews();
                    break listheaderloop;
                }
            }*/
        }
        return isgood;
    }


    void declaration() {
        activity = this;
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        db = new RBGTDatabase(context);
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        //metadata_global = preferences.getString(CommonString.KEY_META_DATA, "");
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        }
        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        expandableListView.setGroupIndicator(null);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


}
