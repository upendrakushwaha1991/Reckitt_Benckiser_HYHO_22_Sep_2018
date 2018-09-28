package com.cpm.reckitt_benckiser_gt.geotag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.download.DownloadActivity;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;

import java.util.ArrayList;
import java.util.List;

public class GeoTagStoreList extends AppCompatActivity {

    private SharedPreferences preferences;
    ArrayList<JourneyPlan> storelist = new ArrayList<JourneyPlan>();
    String date, visit_status;
    RBGTDatabase db;
    ValueAdapter adapter;
    RecyclerView recyclerView;
    private SharedPreferences.Editor editor = null;
    LinearLayout linearlay;
    FloatingActionButton fab;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tag_store_list);
        declaration();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;
        List<JourneyPlan> data;

        public ValueAdapter(Context context, List<JourneyPlan> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.geotagstorelist, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final JourneyPlan current = data.get(position);
            //viewHolder.txt.setText(current.txt);
            viewHolder.txt.setText(current.getStoreName());
            viewHolder.txt_storeAddress.setText(current.getAddress1());
            viewHolder.card_view.setCardBackgroundColor(getResources().getColor(current.getColourCode()));

            if (current.getGeoTag().equalsIgnoreCase("Y")) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.marker);
            } else if (current.getGeoTag().equalsIgnoreCase("N")) {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.entry_grey);
            } else {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
            }

            viewHolder.relativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getGeoTag().equalsIgnoreCase("Y")) {
                        Snackbar.make(v, R.string.title_geo_tag_activity_geo_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getGeoTag().equalsIgnoreCase("N")) {
                        editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_ID, String.valueOf(current.getStoreId()));
                        editor.putString(CommonString.KEY_STORE_NAME, current.getStoreName());
                        editor.putString(CommonString.KEY_VISIT_DATE, current.getVisitDate());
                        editor.commit();

                        Intent in = new Intent(context, GeoTaggingActivity.class);
                        //in.putExtra(CommonString.TAG_FROM, tag_from);
                        in.putExtra(CommonString.KEY_STORE_ID, String.valueOf(current.getStoreId()));
                        startActivity(in);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt, txt_storeAddress;
            ImageView icon;
            RelativeLayout relativelayout;
            ImageView imageview;
            CardView card_view;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.geolistviewxml_storename);
                relativelayout = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
                imageview = (ImageView) itemView.findViewById(R.id.imageView1);
                txt_storeAddress = (TextView) itemView.findViewById(R.id.txt_storeAddress);
                card_view = (CardView) itemView.findViewById(R.id.card_view);
            }
        }
    }

    protected void onResume() {
        super.onResume();
        storelist = db.getStoreData(date);
        if (storelist.size() > 0) {
            adapter = new ValueAdapter(getApplicationContext(), storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            linearlay.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        GeoTagStoreList.this.finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

    }


    void declaration() {
        context = this;
        // activity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        linearlay = (LinearLayout) findViewById(R.id.no_data_lay);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        date = preferences.getString(CommonString.KEY_DATE, "");
        visit_status = preferences.getString(CommonString.KEY_STOREVISITED_STATUS, "");
        TextView txt_label = (TextView) findViewById(R.id.txt_label);
        txt_label.setText(getResources().getString(R.string.title_activity_geo_tag_store_list) + " - " + date);
        db = new RBGTDatabase(context);
        db.open();
    }

}
