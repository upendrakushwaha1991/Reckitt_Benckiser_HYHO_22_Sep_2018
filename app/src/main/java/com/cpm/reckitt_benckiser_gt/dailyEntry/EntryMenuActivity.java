package com.cpm.reckitt_benckiser_gt.dailyEntry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.cpm.reckitt_benckiser_gt.getterSetter.CategoryMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.getterSetter.MenuGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMaster;
import com.cpm.reckitt_benckiser_gt.utilities.AlertandMessages;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;

import java.util.ArrayList;
import java.util.List;

public class EntryMenuActivity extends AppCompatActivity {

    RBGTDatabase database;
    Context context;
    JourneyPlan journeyPlan;
    String visit_date = "";
    List<MenuGetterSetter> data = new ArrayList<>();
    RecyclerView recyclerView;
    ValueAdapter adapter;
    TextView txt_label;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_menu);
        declaration();
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null
                && getIntent().getSerializableExtra(CommonString.TAG_FROM) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            getSupportActionBar().setTitle("");
            txt_label.setText("Entry Menu" + " - " + visit_date);
        }


        if (journeyPlan.getStoreTypeId() == 3) {
            MenuGetterSetter recData = new MenuGetterSetter();
            recData.setIconName("Window/Asset");
            data.add(recData);

            recData = new MenuGetterSetter();
            recData.setIconName("Category Dressing");
            data.add(recData);
        } else {
            MenuGetterSetter recData = new MenuGetterSetter();
            recData.setIconName("Window/Asset");
            data.add(recData);

            recData = new MenuGetterSetter();
            recData.setIconName("Category Dressing");
            data.add(recData);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        filldata();
    }

    private void filldata() {

        for (int i = 0; i < 1; i++) {

            if (journeyPlan.getStoreTypeId() == 3) {

                //region For Window/Assets(Category Visibility)
                ArrayList<WindowMaster> windowlist = database.getWindowListData(journeyPlan);
                if (windowlist.size() > 0) {
                    boolean isAllFilled = true;
                    ArrayList<WindowMaster> windowMasterArrayList = database.getWindowData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                    for (int j = 0; j < windowlist.size(); j++) {
                        if (!database.isWindowDataFilled(journeyPlan.getStoreId(), windowlist.get(j).getWindowId())) {
                            isAllFilled = false;
                            break;
                        }
                    }
                    if (windowMasterArrayList.size() > 0 && isAllFilled) {
                        data.get(0).setIconImage(R.drawable.window_execution_done);
                    } else {
                        data.get(0).setIconImage(R.drawable.window_execution);
                    }
                } else {
                    data.get(0).setIconImage(R.drawable.window_execution_gray);
                }
                //endregion

                //region For Category Dressing_SS
                ArrayList<CategoryMaster> categorylist = database.getCategoryDressingData(journeyPlan);
                if (categorylist.size() > 0) {
                    ArrayList<CategoryMaster> categoryMasters = database.getCategoryDressingData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                    if (categoryMasters.size() > 0) {
                        data.get(1).setIconImage(R.drawable.category_execution_done);
                    } else {
                        data.get(1).setIconImage(R.drawable.category_execution);
                    }

                } else {
                    data.get(1).setIconImage(R.drawable.category_execution_gray);
                }
                //endregion

            } else {
                //region For Window/Assets
                ArrayList<WindowMaster> windowlist = database.getWindowListData(journeyPlan);
                if (windowlist.size() > 0) {
                    boolean isAllFilled = true;
                    ArrayList<WindowMaster> windowMasterArrayList = database.getWindowData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                    for (int j = 0; j < windowlist.size(); j++) {
                        if (!database.isWindowDataFilled(journeyPlan.getStoreId(), windowlist.get(j).getWindowId())) {
                            isAllFilled = false;
                            break;
                        }
                    }
                    if (windowMasterArrayList.size() > 0 && isAllFilled) {
                        data.get(0).setIconImage(R.drawable.window_execution_done);
                    } else {
                        data.get(0).setIconImage(R.drawable.window_execution);
                    }
                } else {
                    data.get(0).setIconImage(R.drawable.window_execution_gray);
                }
                //endregion

                //region for Category Dressing
                ArrayList<CategoryMaster> categorylist = database.getCategoryDressingData(journeyPlan);
                if (categorylist.size() > 0) {
                    ArrayList<CategoryMaster> categoryMasters = database.getCategoryDressingData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
                    if (categoryMasters.size() > 0) {
                        data.get(1).setIconImage(R.drawable.category_execution_done);
                    } else {
                        data.get(1).setIconImage(R.drawable.category_execution);
                    }

                } else {
                    data.get(1).setIconImage(R.drawable.category_execution_gray);
                }
                //endregion
            }

            adapter = new ValueAdapter(context, data);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        }
    }

    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<MenuGetterSetter> data;

        public ValueAdapter(Context context, List<MenuGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_menu_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final MenuGetterSetter current = data.get(position);
            // viewHolder.txt.setText(current.getIconName());
            viewHolder.txt.setVisibility(View.GONE);
            viewHolder.icon.setImageResource(current.getIconImage());
            viewHolder.lay_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getIconName().equalsIgnoreCase("Window/Asset")) {
                        if (current.getIconImage() == R.drawable.window_execution_gray) {
                            AlertandMessages.showToastMsg(context, "No data");
                        } else {
                            startActivity(new Intent(context, WindowMenuActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan));
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }

                    } else if (current.getIconName().equalsIgnoreCase("Category Dressing")) {
                        if (current.getIconImage() == R.drawable.category_execution_gray) {
                            AlertandMessages.showToastMsg(context, "No data");
                        } else {
                            startActivity(new Intent(context, CategoryDressingActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan));
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        }

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt;
            ImageView icon;
            LinearLayout lay_menu;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.list_txt);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
                lay_menu = (LinearLayout) itemView.findViewById(R.id.lay_menu);
            }
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


    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        txt_label = (TextView) findViewById(R.id.txt_label);
        recyclerView = (RecyclerView) findViewById(R.id.rec_menu);
        database = new RBGTDatabase(context);
        database.open();
    }
}
