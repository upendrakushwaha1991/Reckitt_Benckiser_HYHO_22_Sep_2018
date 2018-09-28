package com.cpm.reckitt_benckiser_gt.dailyEntry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMaster;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;

import java.util.ArrayList;
import java.util.List;

public class WindowMenuActivity extends AppCompatActivity {

    RBGTDatabase db;
    Context context;
    SharedPreferences preferences;
    String username;
    RecyclerView rec_window;
    ValueAdapter adapter;
    JourneyPlan journeyPlan;
    ArrayList<WindowMaster> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_menu);
        declaration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        data = db.getWindowListData(journeyPlan);
        if (data.size() > 0) {

            for (int i = 0; i < data.size(); i++) {
                if (db.isWindowDataFilled(journeyPlan.getStoreId(), data.get(i).getWindowId())) {
                    data.get(i).setFilled(true);
                } else {
                    data.get(i).setFilled(false);
                }
            }

            adapter = new ValueAdapter(context, data);
            rec_window.setAdapter(adapter);
            rec_window.setLayoutManager(new GridLayoutManager(context, 2));
        }

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
        //super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;
        List<WindowMaster> data;

        public ValueAdapter(Context context, List<WindowMaster> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.item_window_list_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {

            final WindowMaster current = data.get(position);
            viewHolder.tv_window.setText(current.getBrand() + " - " + current.getWindow());
            viewHolder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.isFilled()) {
                        Snackbar.make(v, "Data already filled", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Intent in = new Intent(context, WindowChecklistActivity.class);
                        in.putExtra(CommonString.TAG_OBJECT, journeyPlan);
                        in.putExtra(CommonString.TAG_WINDOW_OBJECT, current);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }
                }
            });

            if (current.isFilled()) {
                viewHolder.window_icon.setBackgroundResource(R.drawable.window_done);
            } else {
                viewHolder.window_icon.setBackgroundResource(R.drawable.window);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_window;
            LinearLayout parent;
            ImageView window_icon;


            public MyViewHolder(View itemView) {
                super(itemView);
                tv_window = (TextView) itemView.findViewById(R.id.tv_window);
                parent = (LinearLayout) itemView.findViewById(R.id.lay_window);
                window_icon = (ImageView) itemView.findViewById(R.id.icon_window);
            }
        }

    }


    void declaration() {
        context = this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new RBGTDatabase(context);
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        }
        rec_window = (RecyclerView) findViewById(R.id.rec_window);
    }
}
