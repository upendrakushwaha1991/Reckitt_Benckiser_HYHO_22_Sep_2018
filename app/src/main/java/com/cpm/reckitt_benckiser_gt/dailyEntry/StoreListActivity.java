package com.cpm.reckitt_benckiser_gt.dailyEntry;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.delegates.CoverageBean;
import com.cpm.reckitt_benckiser_gt.download.DownloadActivity;
import com.cpm.reckitt_benckiser_gt.geotag.GeoTagStoreList;
import com.cpm.reckitt_benckiser_gt.getterSetter.CategoryMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMaster;
import com.cpm.reckitt_benckiser_gt.upload.Retrofit_method.UploadImageWithRetrofit;
import com.cpm.reckitt_benckiser_gt.upload.Retrofit_method.upload.UploadWithoutWaitActivity;
import com.cpm.reckitt_benckiser_gt.utilities.AlertandMessages;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by ashishc on 29-12-2016.
 */

public class StoreListActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context context;
    private String userId, rightname;
    private boolean ResultFlag = true;
    private ArrayList<CoverageBean> coverage = new ArrayList<>();
    private ArrayList<JourneyPlan> storelist = new ArrayList<>();
    private String date;
    private RBGTDatabase db;
    private ValueAdapter adapter;
    private RecyclerView recyclerView;
    private Button search_btn;
    private LinearLayout linearlay, storelist_ll;
    private String tag_from = "";
    private Dialog dialog;
    TextView txt_label;
    private boolean result_flag = false;
    private FloatingActionButton fab;
    private double lat;
    private double lon;
    int distanceGeoPhence;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private LocationRequest mLocationRequest;
    Intent geotagIntent;
    SearchListAdapter adapter2;
    ArrayList<JourneyPlan> filterdNames;
    ArrayList<JourneyPlan> searchList;
    Intent categortDbsrIntent;
    SharedPreferences preferences;
    private int downloadIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistfablayout);
        declaration();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Parinaam");
                builder.setMessage(getResources().getString(R.string.want_download_data)).setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent in = new Intent(context, DownloadActivity.class);
                                startActivity(in);
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.gps));
            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.gpsebale));
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

    }


    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        downloadIndex = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        setLitData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
    public void onClick(View v) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    //region ValueAdapter
    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;
        List<JourneyPlan> data = Collections.emptyList();

        public ValueAdapter(Context context, List<JourneyPlan> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.storeviewlist, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {

            final JourneyPlan current = data.get(position);
            viewHolder.chkbtn.setBackgroundResource(R.mipmap.checkout);
            viewHolder.txt.setText(current.getStoreName() + " - " + current.getStoreType());
            viewHolder.address.setText(current.getAddress1());

            if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_VALID)) {
                viewHolder.chkbtn.setVisibility(View.VISIBLE);
                viewHolder.imageview.setVisibility(View.INVISIBLE);
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_u);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_d);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_p);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.tick);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
            } else if (isValid(current) || (rightname.equalsIgnoreCase("DBSR")) && current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_VALID)) {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.chkbtn.setVisibility(View.VISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN) || db.getSpecificCoverageData(String.valueOf(current.getStoreId()), current.getVisitDate()).size() > 0) {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.green));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                boolean isVisitlater = false;
                for (int i = 0; i < coverage.size(); i++) {
                    if (current.getStoreId() == Integer.parseInt(coverage.get(i).getStoreId())) {
                        if (coverage.get(i).getReasonid().equalsIgnoreCase("11")
                                || coverage.get(i).getReason().equalsIgnoreCase("Visit Later")) {
                            isVisitlater = true;
                            break;
                        }
                    }
                }
                if (isVisitlater) {
                    viewHolder.imageview.setBackgroundResource(R.drawable.visit_later);
                } else {
                    viewHolder.imageview.setBackgroundResource(R.drawable.exclamation);
                }
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
            } else {
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(current.getColourCode()));
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
            }

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int store_id = current.getStoreId();

                    if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_data_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_checkout, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_again_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        boolean isVisitlater = false;
                        for (int i = 0; i < coverage.size(); i++) {
                            if (store_id == Integer.parseInt(coverage.get(i).getStoreId())) {
                                if (coverage.get(i).getReasonid().equalsIgnoreCase("11")
                                        || coverage.get(i).getReason().equalsIgnoreCase("Visit Later")) {
                                    isVisitlater = true;
                                    break;
                                }
                            }
                        }
                        if (isVisitlater) {

                            boolean entry_flag = false;
                            if (tag_from.equalsIgnoreCase("from_jcp")) {

                                //region Check for Checking in JCP stores
                                boolean entry_flag_from_jcp = true;
                                for (int j = 0; j < storelist.size(); j++) {
                                    if (storelist.get(j).getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                        if (store_id != storelist.get(j).getStoreId()) {
                                            entry_flag_from_jcp = false;
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }

                                //endregion
                                if (entry_flag_from_jcp) {
                                    entry_flag = true;
                                }
                            }

                            if (entry_flag) {
                                showMyDialog(current, isVisitlater);
                            } else {
                                Snackbar.make(v, R.string.title_store_list_checkout_current, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }
                        } else {
                            Snackbar.make(v, R.string.title_store_list_activity_already_store_closed, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }

                    } else {

                        boolean entry_flag = false;
                        String msg = "";

                        if (tag_from.equalsIgnoreCase("from_jcp")) {

                            //region Check for Checkin in JCP stores
                            boolean entry_flag_from_jcp = true;
                            for (int j = 0; j < storelist.size(); j++) {
                                if (storelist.get(j).getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                    if (store_id != storelist.get(j).getStoreId()) {
                                        entry_flag_from_jcp = false;
                                        msg = getResources().getString(R.string.title_store_list_checkout_current);
                                        break;
                                    } else {
                                        break;
                                    }
                                }
                            }
                            //endregion
                            if (entry_flag_from_jcp) {
                                entry_flag = true;
                            }
                        }

                        if (rightname.equalsIgnoreCase("DBSR")) {
                            entry_flag = false;
                            startActivity(categortDbsrIntent.putExtra(CommonString.TAG_OBJECT, current));
                        } /*else {
                            entry_flag = true;
                        }*/

                        if (entry_flag) {
                            showMyDialog(current, false);
                        } else {
                            AlertandMessages.showSnackbarMsg(v, msg);
                        }

                    }
                }
            });


            viewHolder.chkbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.wantcheckout)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (CheckNetAvailability()) {
                                        new checkoutData(current).execute();
                                    } else {
                                        Snackbar.make(recyclerView, R.string.nonetwork, Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.closed, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

        }

        @SuppressWarnings("deprecation")
        public boolean CheckNetAvailability() {
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED
                    || connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                // we are connected to a network
                connected = true;
            }
            return connected;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txt, address;
            RelativeLayout relativelayout;
            ImageView imageview;
            Button chkbtn;
            CardView Cardbtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.storelistviewxml_storename);
                address = (TextView) itemView.findViewById(R.id.storelistviewxml_storeaddress);
                relativelayout = (RelativeLayout) itemView.findViewById(R.id.storenamelistview_layout);
                //imageview = (ImageView) itemView.findViewById(R.id.imageView2);
                imageview = (ImageView) itemView.findViewById(R.id.storelistviewxml_storeico);
                chkbtn = (Button) itemView.findViewById(R.id.chkout);
                Cardbtn = (CardView) itemView.findViewById(R.id.card_view);
            }
        }

    }
    //endregion


    //region showMyDialog
    private void showMyDialog(final JourneyPlan current, final boolean isVisitLater) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {

                    if (!current.getGeoTag().equalsIgnoreCase("N")) {
                        boolean flag = true;
                        if (coverage.size() > 0) {
                            for (int i = 0; i < coverage.size(); i++) {
                                if (String.valueOf(current.getStoreId()).equals(coverage.get(i).getStoreId())) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                        boolean flag_entry = true;

                        if (!rightname.equalsIgnoreCase("DBSR")) {
                            int distance = 0;
                            if(flag){
                                double store_lat = Double.parseDouble(String.valueOf(current.getLatitude()));
                                double store_lon = Double.parseDouble(String.valueOf(current.getLongitude()));
                                distanceGeoPhence = current.getGeoFencing();
                                if(store_lat!=0.0 && store_lon!=0.0){

                                    if (checkPlayServices()) {
                                        // Building the GoogleApi client
                                        buildGoogleApiClient();

                                        createLocationRequest();
                                    }

                                    // Create an instance of GoogleAPIClient.
                                    if (mGoogleApiClient == null) {
                                        mGoogleApiClient = new GoogleApiClient.Builder(StoreListActivity.this)
                                                .addConnectionCallbacks(StoreListActivity.this)
                                                .addOnConnectionFailedListener(StoreListActivity.this)
                                                .addApi(LocationServices.API)
                                                .build();
                                    }

                                    distance = distFrom(store_lat, store_lon, lat, lon);

                                    if (/*true*/distance > distanceGeoPhence) {
                                        flag_entry = false;
                                    }
                                    else {
                                        String msg = getString(R.string.distance_from_the_store) + " " + distance + " meters";
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            if(flag_entry) {
                                Toast.makeText(context, "Distance - " + distance + " meters", Toast.LENGTH_SHORT).show();
                              gotoActivity(flag,isVisitLater,current);
                            }else{

                                String msg = getString(R.string.you_need_to_be_in_the_store) + "\n " + getString(R.string.distance_from_the_store)+ " - " + distance + " "+getString(R.string.meters);
                                dialog.cancel();
                                AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                                builder.setTitle(getResources().getString(R.string.dialog_title));
                                builder.setMessage(msg).setCancelable(false)
                                        .setPositiveButton(getResources().getString(R.string.ok),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog1,
                                                                        int id) {

                                                        dialog1.cancel();
                                                    }
                                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }else{
                            gotoActivity(flag,isVisitLater,current);
                        }
                    } else {
                        dialog.cancel();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(getResources().getString(R.string.dialog_title));
                        builder.setMessage(R.string.first_geotag_the_store).setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1,
                                                                int id) {
                                                dialog1.cancel();
                                                startActivity(geotagIntent);
                                            }
                                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    RBGTDatabase db = new RBGTDatabase(context);
                    db.open();
                    ArrayList<CoverageBean> coverage = db.getCoverageWithStoreIDAndVisitDate_Data(current.getStoreId() + "", current.getVisitDate());
                    if (current.getUploadStatus().equals(CommonString.KEY_CHECK_IN)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.yes),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {

                                                new DeleteCoverageData(String.valueOf(current.getStoreId()), String.valueOf(current.getVisitDate()), userId, true).execute();
                                                UpdateStore(current.getStoreId() + "");
                                                Intent in = new Intent(context, NonWorkingActivity.class);
                                                in.putExtra(CommonString.KEY_STORE_ID, current.getStoreId());
                                                in.putExtra(CommonString.TAG_FROM, tag_from);
                                                startActivity(in);
                                                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                                            }
                                        })
                                .setNegativeButton(getResources().getString(R.string.no),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {


                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();

                        alert.show();

                    } else {
                        //new DeleteCoverageData(String.valueOf(current.getStoreId()), String.valueOf(current.getVisitDate()), userId, false).execute();
                        UpdateStore(current.getStoreId() + "");
                        Intent in = new Intent(context, NonWorkingActivity.class);
                        in.putExtra(CommonString.KEY_STORE_ID, current.getStoreId());
                        in.putExtra(CommonString.TAG_FROM, tag_from);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }

                }
            }

        });


        dialog.show();
    }

    private void gotoActivity(boolean flag, boolean isVisitLater, JourneyPlan current) {
        if (flag) {
            Intent in = new Intent(context, StoreimageActivity.class);
            in.putExtra(CommonString.TAG_OBJECT, current);
            in.putExtra(CommonString.TAG_FROM, tag_from);
            startActivity(in);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        } else {
            Intent in = new Intent(context, EntryMenuActivity.class);
            in.putExtra(CommonString.TAG_OBJECT, current);
            in.putExtra(CommonString.TAG_FROM, tag_from);
            startActivity(in);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        }
        if (isVisitLater) {
            new DeleteCoverageData(String.valueOf(current.getStoreId()), String.valueOf(current.getVisitDate()), userId, false).execute();
            UpdateStore(current.getStoreId() + "");
            dialog.cancel();
        }
        dialog.cancel();
    }
    //endregion



    public static int distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        int dist = (int) (earthRadius * c);

        return dist;
    }

    //region UpdateStore
    private void UpdateStore(String storeid) {
        db.open();
        db.deleteTableWithStoreID(storeid);
        if (tag_from != null) {
            if (tag_from.equalsIgnoreCase("from_jcp")) {
                db.updateStoreStatus(storeid, storelist.get(0).getVisitDate(), "N");
            }
        }
    }
    //endregion


    public boolean checkleavestatus(String store_cd) {
/*
        if (coverage.size() > 0) {


            for (int i = 0; i < coverage.size(); i++) {
                if (store_cd.equals(coverage.get(i).getStoreId())) {
                    if (coverage.get(i).getStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        result_flag = true;
                        break;
                    }
                } else {

                    result_flag = false;
                }
            }
        }*/
        return result_flag;
    }


    private void declaration() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        linearlay = (LinearLayout) findViewById(R.id.no_data_lay_ll);
        storelist_ll = (LinearLayout) findViewById(R.id.storelist_ll);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        search_btn = (Button) findViewById(R.id.search_btn);
        userId = preferences.getString(CommonString.KEY_USERNAME, "");
        rightname = preferences.getString(CommonString.KEY_RIGHTNAME, "");
        txt_label = (TextView) findViewById(R.id.txt_label);
        context = this;
        tag_from = getIntent().getStringExtra(CommonString.TAG_FROM);
        db = new RBGTDatabase(context);
        db.open();
        getSupportActionBar().setTitle("");
        txt_label.setText("Store List - " + date);
        geotagIntent = new Intent(context, GeoTagStoreList.class);
        categortDbsrIntent = new Intent(context, DBSRCategoryActivity.class);

        if (rightname.equalsIgnoreCase("DBSR")) {
            search_btn.setVisibility(View.VISIBLE);
        } else {
            search_btn.setVisibility(View.GONE);
        }

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchList.size() > 0) {
                    if (isStoreValidInDBSR()) {
                        //region Search Dialog
                        final Dialog dialog = new Dialog(context);
                        dialog.setCancelable(true);
                        dialog.setContentView(R.layout.layout_search_store);
                        dialog.setTitle("DBSR");
                        final RecyclerView searchRecyclerView = dialog.findViewById(R.id.searchRecyclerView);
                        final EditText inputSearch = dialog.findViewById(R.id.inputSearch);
                        adapter2 = new SearchListAdapter(context, searchList, dialog);
                        searchRecyclerView.setAdapter(adapter2);
                        searchRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        dialog.show();

                        inputSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault()).replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                                adapter2.filter(text);
                            }

                            @Override
                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                                // TODO Auto-generated method stub
                                adapter2 = new SearchListAdapter(context, searchList, dialog);
                                searchRecyclerView.setAdapter(adapter2);
                                searchRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            }

                            @Override
                            public void afterTextChanged(Editable arg0) {
                                // TODO Auto-generated method stub

                            }
                        });
                        //endregion
                    } else {
                        AlertandMessages.showSnackbarMsg(linearlay, getResources().getString(R.string.title_store_list_checkout_current));
                    }
                } else {
                    Snackbar.make(linearlay, R.string.title_store_list_download_data, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });

    }

    class SearchListAdapter extends RecyclerView.Adapter<MyViewHolder> {

        LayoutInflater inflater;
        ArrayList<JourneyPlan> list;
        Dialog dialog;

        SearchListAdapter(Context context, ArrayList<JourneyPlan> list, Dialog dialog) {
            this.list = list;
            inflater = LayoutInflater.from(context);
            this.dialog = dialog;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_add_store_list_item_view2, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final JourneyPlan journeyPlan = list.get(position);

            holder.textView.setText(journeyPlan.getStoreId() + "\n" + journeyPlan.getStoreName() + "\n" + journeyPlan.getAddress1());

            if (journeyPlan.getWeeklyUpload() != null && journeyPlan.getWeeklyUpload().equalsIgnoreCase("Y")) {
                holder.img_storeImage.setVisibility(View.VISIBLE);
            } else {
                holder.img_storeImage.setVisibility(View.GONE);
            }
            holder.storelist_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (journeyPlan.getWeeklyUpload() != null && journeyPlan.getWeeklyUpload().equalsIgnoreCase("Y")) {
                        Snackbar.make(linearlay, "Store Already done", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    } else {
                        dialog.cancel();
                        boolean isvalid = true;
                        int store_id = journeyPlan.getStoreId();
                        ArrayList<JourneyPlan> jcp_dbsr_saved = db.getSpecificStore_DBSRSavedData(String.valueOf(journeyPlan.getStoreId()));
                        if (jcp_dbsr_saved.size() > 0) {
                            String status = jcp_dbsr_saved.get(0).getUploadStatus();
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).getStoreId() == (store_id) && (status.equalsIgnoreCase(CommonString.KEY_C) || status.equalsIgnoreCase(CommonString.KEY_U))) {
                                        isvalid = false;
                                        break;
                                    }
                                }
                            }

                        }
                        if (isvalid) {
                            // showMyDialog(distributorGetterSetter);
                            startActivity(categortDbsrIntent.putExtra(CommonString.TAG_OBJECT, journeyPlan));
                        } else {
                            Snackbar.make(linearlay, "Store Already done", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void filterList(ArrayList<JourneyPlan> filterdNames) {
            this.list = filterdNames;
            notifyDataSetChanged();
        }

        private void filter(String text) {
            //new array list that will hold the filtered data
            filterdNames = new ArrayList<>();
            //looping through existing elements
            for (JourneyPlan s : list) {
                //if the existing elements contains the search input
                if (s.getStoreName().toLowerCase().contains(text.toLowerCase())) {
                    //adding the element to filtered list
                    filterdNames.add(s);
                }
            }
            //calling a method of the adapter class and passing the filtered list
            adapter2.filterList(filterdNames);
            //notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RelativeLayout storelist_ll;
        Button checkoutbtn;
        ImageView img_storeImage;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.txt_storeName);
            //checkoutbtn = view.findViewById(R.id.checkoutbtn);
            storelist_ll = view.findViewById(R.id.storelist_ll);
            img_storeImage = view.findViewById(R.id.storelistviewxml_storeico);
        }
    }


    private boolean isValid(JourneyPlan journeyPlan) {
        boolean result = false;
        boolean flag_window = false, flag_category_dressing = false;
        boolean datain_window = false, datain_category_dressing = false;
        db.open();

        if (journeyPlan.getStoreTypeId() != 3) {

            //region window
            ArrayList<WindowMaster> windowlist = db.getWindowListData(journeyPlan);
            if (windowlist.size() > 0) {
                boolean isAllFilled = true;
                ArrayList<WindowMaster> windowMasterArrayList = db.getWindowData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                for (int j = 0; j < windowlist.size(); j++) {
                    if (!db.isWindowDataFilled(journeyPlan.getStoreId(), windowlist.get(j).getWindowId())) {
                        isAllFilled = false;
                        break;
                    }
                }
                if (windowMasterArrayList.size() > 0 && isAllFilled) {
                    flag_window = true;
                } else {
                    flag_window = false;
                }
            } else {
                flag_window = true;
            }
            //endregion

            //region Category Dressing
            ArrayList<CategoryMaster> categorylist = db.getCategoryDressingData(journeyPlan);
            if (categorylist.size() > 0) {
                ArrayList<CategoryMaster> categoryMasters = db.getCategoryDressingData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                if (categoryMasters.size() > 0) {
                    flag_category_dressing = true;
                } else {
                    flag_category_dressing = false;
                }
            } else {
                flag_category_dressing = true;
            }
            if (windowlist.size() == 0 && categorylist.size() == 0) {
                flag_window = false;
                flag_category_dressing = false;
            }
            //endregion

        } else {
            //region window
            ArrayList<WindowMaster> windowlist = db.getWindowListData(journeyPlan);
            if (windowlist.size() > 0) {
                boolean isAllFilled = true;
                ArrayList<WindowMaster> windowMasterArrayList = db.getWindowData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                for (int j = 0; j < windowlist.size(); j++) {
                    if (!db.isWindowDataFilled(journeyPlan.getStoreId(), windowlist.get(j).getWindowId())) {
                        isAllFilled = false;
                        break;
                    }
                }
                if (windowMasterArrayList.size() > 0 && isAllFilled) {
                    flag_window = true;
                } else {
                    flag_window = false;
                }
            } else {
                flag_window = true;
            }
            //endregion

            //region Category Dressing
            ArrayList<CategoryMaster> categorylist = db.getCategoryDressingData(journeyPlan);
            if (categorylist.size() > 0) {
                ArrayList<CategoryMaster> categoryMasters = db.getCategoryDressingData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                if (categoryMasters.size() > 0) {
                    flag_category_dressing = true;
                } else {
                    flag_category_dressing = false;
                }
            } else {
                flag_category_dressing = true;
            }
            //endregion
            if (windowlist.size() == 0 && categorylist.size() == 0) {
                flag_window = false;
                flag_category_dressing = false;
            }
        }

        if (flag_window && flag_category_dressing) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }


    public boolean isStoreValidInDBSR() {
        boolean isValid = true;
        if (storelist != null && storelist.size() > 0) {
            String status = storelist.get(0).getUploadStatus();
            for (int i = 0; i < storelist.size(); i++) {
                if (status != null && (status.equalsIgnoreCase(CommonString.KEY_CHECK_IN) || status.equalsIgnoreCase(CommonString.KEY_VALID))) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }


    //region checkoutData
    public class checkoutData extends AsyncTask<Void, Void, String> {
        private JourneyPlan cdata;

        checkoutData(JourneyPlan cdata) {
            this.cdata = cdata;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle(getResources().getString(R.string.dialog_title));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String strflag = null;
            try {
                RBGTDatabase db = new RBGTDatabase(context);
                db.open();
                // for failure
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("UserId", userId);
                jsonObject.put("StoreId", cdata.getStoreId());
                jsonObject.put("Latitude", lat);
                jsonObject.put("Longitude", lon);
                jsonObject.put("Checkout_Date", cdata.getVisitDate());

                String jsonString2 = jsonObject.toString();

                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                String result_str;
                if (rightname.equalsIgnoreCase("DBSR")) {
                    result_str = upload.downloadDataUniversal(jsonString2, CommonString.CHECKOUTDetail_CLIENT);
                } else {
                    result_str = upload.downloadDataUniversal(jsonString2, CommonString.CHECKOUTDetail);
                }

                if (result_str.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                    throw new IOException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                    throw new SocketTimeoutException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                    throw new JsonSyntaxException("Check out Upload");
                } else if (result_str.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    throw new Exception();
                } else {
                    ResultFlag = true;
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

            } catch (JsonSyntaxException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_INVALID_JSON;

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
            dialog.dismiss();
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                long id = 0;
                if (rightname.equalsIgnoreCase("DBSR")) {
                    id = db.updateCheckoutStatus(String.valueOf(cdata.getStoreId()), CommonString.KEY_C, CommonString.TABLE_Journey_Plan_DBSR_Saved);
                } else {
                    id = db.updateCheckoutStatus(String.valueOf(cdata.getStoreId()), CommonString.KEY_C, CommonString.TABLE_Journey_Plan);
                }
                if (id > 0) {
                    recyclerView.invalidate();
                    adapter.notifyDataSetChanged();
                    AlertandMessages.showSnackbarMsg(fab, "Store checked out successfully");
                    setLitData();
                    Intent i = new Intent(context, UploadWithoutWaitActivity.class);
                    startActivity(i);
                }
            } else {
                showAlert(getString(R.string.datanotfound) + " " + result);
            }
        }

    }
    //endregion

    public class DeleteCoverageData extends AsyncTask<Void, Void, String> {

        String storeID, visitDate, userId;
        boolean showDeleteCoverageMsg;

        DeleteCoverageData(String storeId, String visitDate, String userId, boolean showDeleteCoverageMsg) {
            this.storeID = storeId;
            this.visitDate = visitDate;
            this.userId = userId;
            this.showDeleteCoverageMsg = showDeleteCoverageMsg;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle(getResources().getString(R.string.dialog_title));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String strflag = null;
            try {
                RBGTDatabase db = new RBGTDatabase(context);
                db.open();
                // for failure
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StoreId", storeID);
                jsonObject.put("VisitDate", visitDate);
                jsonObject.put("UserId", userId);

                String jsonString2 = jsonObject.toString();

                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                String result_str = upload.downloadDataUniversal(jsonString2, CommonString.DELETE_COVERAGE);

                if (result_str.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                    throw new IOException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                    throw new SocketTimeoutException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                    throw new JsonSyntaxException("Check out Upload");
                } else if (result_str.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    throw new Exception();
                } else {
                    ResultFlag = true;
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

            } catch (JsonSyntaxException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_INVALID_JSON;

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
            dialog.dismiss();
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                if (showDeleteCoverageMsg) {
                    AlertandMessages.showToastMsg(context, "Store Coverage Deleted Successfully.");
                }
            } else {
                showAlert(getString(R.string.NodataAvailable) + " " + result);
            }
        }

    }

    private void showAlert(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressWarnings("deprecation")
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                AlertandMessages.showToastMsg(context, getResources().getString(R.string.notsuppoted));
                finish();
            }
            return false;
        }
        return true;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 500;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FATEST_INTERVAL = 100;
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        int DISPLACEMENT = 5;
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void startLocationUpdates() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lon = mLastLocation.getLongitude();

            }
        }
        // if (mRequestingLocationUpdates) {
        startLocationUpdates();
        // }
        // startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //  Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        // AppIndex.AppIndexApi.end(client, getIndexApiAction());
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    private void setLitData() {
        /*if (tag_from != null) {
            if (tag_from.equalsIgnoreCase("from_jcp")) {
                //jcp data for current visit date
                storelist = db.getStoreData(date);
                coverage = db.getCoverageData(date);

            }
        }*/
        if (rightname.equalsIgnoreCase("DBSR")) {
            searchList = db.getStoreData_DBSR(date);
            storelist = db.getStoreData_DBSR_Saved(date);
        } else {
            storelist = db.getStoreData(date);
        }

        coverage = db.getCoverageData(date);
        if (storelist.size() > 0 && downloadIndex == 0) {
            adapter = new ValueAdapter(getApplicationContext(), storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            recyclerView.setVisibility(View.VISIBLE);
            storelist_ll.setVisibility(View.VISIBLE);
            linearlay.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);

        } else {
            if (rightname.equalsIgnoreCase("DBSR") && searchList.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                storelist_ll.setVisibility(View.VISIBLE);
                linearlay.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                storelist_ll.setVisibility(View.GONE);
                linearlay.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            }


        }

    }

}

