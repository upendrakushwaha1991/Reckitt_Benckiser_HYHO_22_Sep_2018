package com.cpm.reckitt_benckiser_gt.dailyEntry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.getterSetter.AnswerChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.ChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowNonReasonGetterSetter;
import com.cpm.reckitt_benckiser_gt.utilities.AlertandMessages;
import com.cpm.reckitt_benckiser_gt.utilities.CommonFunctions;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class WindowActivity extends AppCompatActivity {

    ExpandableListAdapter adapter;
    JourneyPlan journeyPlan;
    RBGTDatabase db;
    Context context;
    String _pathforcheck = "", image = "", path = "", msg = "";
    ArrayList<WindowMaster> listDataHeader;
    HashMap<ChecklistGetterSetter, ArrayList<AnswerChecklistGetterSetter>> answerListData;
    HashMap<WindowMaster, ArrayList<ChecklistGetterSetter>> listDataChild;
    ArrayAdapter reason_adapter;
    String username, metadata_global;
    private SharedPreferences preferences;
    ArrayList<WindowNonReasonGetterSetter> reasondata;
    ArrayList<ChecklistGetterSetter> answered_list = new ArrayList<>();
    FloatingActionButton fab;
    Activity activity;
    ExpandableListView expandableListView;
    boolean errorFlag = false;
    int global_position = 0, global_position_for_spinner;
    ArrayList<Integer> error_position_header = new ArrayList();
    ArrayList<Integer> error_position_child = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);
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
                                                long commonId = db.InsertWindowsData(username, journeyPlan, listDataHeader, listDataChild);
                                                if (commonId > 0) {
                                                    AlertandMessages.showToastMsg(context, "Data Saved");
                                                    finish();
                                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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

    void prepareList() {
        reasondata = new ArrayList<>();
        reasondata = db.getWindowNonReasonData();
        listDataHeader = db.getWindowListData(journeyPlan);
        listDataChild = new HashMap<>();
        answerListData = new HashMap<>();
        for (int i = 0; i < listDataHeader.size(); i++) {
            ArrayList<ChecklistGetterSetter> data = db.getChecklistData(String.valueOf(listDataHeader.get(i).getWindowId()), journeyPlan);
            listDataChild.put(listDataHeader.get(i), data);
            for (int j = 0; j < data.size(); j++) {
                ChecklistGetterSetter answered_temp = new ChecklistGetterSetter();
                answered_temp.setCHECKLIST_CD(data.get(j).getChecklist_cd());
                answered_temp.setANSWER_CD(data.get(j).getANSWER_CD());
                answered_list.add(answered_temp);

                ArrayList<AnswerChecklistGetterSetter> ans;
                ans = db.getChecklistAnswerData(data.get(j).getChecklist_cd());
                AnswerChecklistGetterSetter ans_temp = new AnswerChecklistGetterSetter();
                ans_temp.setAnswer("-Select-");
                ans_temp.setAnswer_cd("0");
                ans.add(0, ans_temp);
                answerListData.put(data.get(j), ans);
            }
        }

        adapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
        expandableListView.setAdapter(adapter);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private ArrayList<WindowMaster> listDataHeader; // header titles
        private HashMap<WindowMaster, ArrayList<ChecklistGetterSetter>> listDataChild;

        public ExpandableListAdapter(Context context, ArrayList<WindowMaster> listDataHeader, HashMap<WindowMaster, ArrayList<ChecklistGetterSetter>> listDataChild) {
            this._context = context;
            this.listDataHeader = listDataHeader;
            this.listDataChild = listDataChild;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @SuppressLint("NewApi")
        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChecklistGetterSetter checkList = (ChecklistGetterSetter) getChild(groupPosition, childPosition);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.item_child_expandable_checklist, null);
                holder = new ViewHolder();
                holder.textview = (TextView) convertView.findViewById(R.id.tv_checklist);
                holder.item_ll = (LinearLayout) convertView.findViewById(R.id.lay_window);
                holder.spinner = (Spinner) convertView.findViewById(R.id.spin_checklist_ans);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textview.setText(checkList.getChecklist());

            final ArrayList<AnswerChecklistGetterSetter> answerList = answerListData.get(checkList);
            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), answerList);
            holder.spinner.setAdapter(customSpinnerAdapter);
            final ViewHolder finalHolder = holder;
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    answered_list.get(childPosition).setANSWER_CD(answerList.get(pos).getAnswer_cd());
                    listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).setANSWER_CD(answerList.get(pos).getAnswer_cd());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (listDataHeader.get(groupPosition).isExist()) {
                boolean isSelected = false;
                for (int i = 0; i < answerList.size(); i++) {
                    if (answerList.get(i).getAnswer_cd().equalsIgnoreCase(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getANSWER_CD())) {
                        holder.spinner.setSelection(i);
                        isSelected = true;
                        break;
                    }
                }
                if (!isSelected) {
                    holder.spinner.setSelection(0);
                }
            }

            if (errorFlag) {
                if (error_position_header.contains(groupPosition) && error_position_child.contains(childPosition)) {
                    holder.item_ll.setBackgroundResource(R.color.red);
                } else {
                    holder.item_ll.setBackgroundResource(R.color.white);
                }
            } else {
                holder.item_ll.setBackgroundResource(R.color.white);
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (this.listDataHeader.get(groupPosition).isExist()) {
                return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
            } else {
                return 0;
            }
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
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.item_header_expandable_window, parent, false);
            }

            final WindowMaster headerTitle = (WindowMaster) getGroup(groupPosition);

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.txt_header);
            CardView cardView = (CardView) convertView.findViewById(R.id.cardview_exists);
            final LinearLayout lay_Camera = (LinearLayout) convertView.findViewById(R.id.lay_Camera);
            final LinearLayout lay_reason = (LinearLayout) convertView.findViewById(R.id.lay_reason);
            final ImageView camerabtn = (ImageView) convertView.findViewById(R.id.image_window);
            final Spinner reason_spinner = (Spinner) convertView.findViewById(R.id.reason_spinner);
            ToggleButton switch_exists = (ToggleButton) convertView.findViewById(R.id.switch_exists);
            lblListHeader.setText(headerTitle.getBrand() + " - " + headerTitle.getWindow());

            reason_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
            for (int i = 0; i < reasondata.size(); i++) {
                reason_adapter.add(reasondata.get(i).getWREASON().get(0));
            }
            reason_spinner.setAdapter(reason_adapter);

            for (int i = 0; i < reasondata.size(); i++) {
                if (reasondata.get(i).getWREASON_CD().get(0).equalsIgnoreCase(String.valueOf(headerTitle.getReasonId()))) {
                    reason_spinner.setSelection(i);
                    break;
                }
            }

            reason_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        headerTitle.setReasonId(Integer.parseInt(reasondata.get(position).getWREASON_CD().get(0)));
                        headerTitle.setReason(reasondata.get(position).getWREASON().get(0));
                    } else {
                        headerTitle.setReasonId(0);
                        headerTitle.setReason("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandableListView.isGroupExpanded(groupPosition)) {
                        expandableListView.collapseGroup(groupPosition);
                    } else {
                        expandableListView.expandGroup(groupPosition);
                    }
                }
            });

            switch_exists.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ToggleButton) v).isChecked()) {
                        lay_Camera.setVisibility(View.VISIBLE);
                        lay_reason.setVisibility(View.GONE);
                        headerTitle.setExist(true);
                        reason_spinner.setSelection(0);
                        expandableListView.invalidateViews();
                        expandableListView.expandGroup(groupPosition);
                    } else {
                        lay_Camera.setVisibility(View.GONE);
                        lay_reason.setVisibility(View.VISIBLE);
                        global_position_for_spinner = groupPosition;
                        headerTitle.setExist(false);
                        if (headerTitle.getImage() != null
                                && !headerTitle.getImage().equalsIgnoreCase("")) {
                            if (new File(CommonString.FILE_PATH + headerTitle.getImage()).exists()) {
                                new File(CommonString.FILE_PATH + headerTitle.getImage()).delete();
                            }
                            headerTitle.setImage("");
                        }

                        ArrayList<ChecklistGetterSetter> checklist = listDataChild.get(listDataHeader.get(groupPosition));
                        for (int i = 0; i < checklist.size(); i++) {
                            checklist.get(i).setANSWER_CD("0");
                        }
                        expandableListView.invalidateViews();
                        expandableListView.collapseGroup(groupPosition);
                    }
                }
            });

            camerabtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    global_position = groupPosition;
                    String intime = CommonFunctions.getCurrentTime();
                    _pathforcheck = journeyPlan.getStoreId() + "_Window-" + groupPosition + "_" + journeyPlan.getVisitDate().replace("/", "") + "_" + intime.replace(":", "") + ".jpg";
                    path = CommonString.FILE_PATH + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(context, path, null,false);
                }
            });

            if (headerTitle.isExist() == true) {
                switch_exists.setChecked(true);
                lay_Camera.setVisibility(View.VISIBLE);
                lay_reason.setVisibility(View.GONE);
                expandableListView.expandGroup(groupPosition);
            } else {
                switch_exists.setChecked(false);
                expandableListView.collapseGroup(groupPosition);
                lay_Camera.setVisibility(View.GONE);
                lay_reason.setVisibility(View.VISIBLE);
            }

            if (headerTitle.getImage() != null && !headerTitle.getImage().equalsIgnoreCase("")) {
                camerabtn.setBackgroundResource(R.mipmap.camera_green);
            } else {
                camerabtn.setBackgroundResource(R.mipmap.camera_pink);
            }

            if (errorFlag) {
                if (error_position_header.contains(groupPosition)) {
                    cardView.setCardBackgroundColor(Color.RED);
                } else {
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.lightpink));
                }
            } else {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.lightpink));
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
        CardView cardView;
        TextView textview;
        Spinner spinner;
        LinearLayout item_ll;
        CustomSpinnerAdapter customSpinnerAdapter;
    }


    public Boolean validatedata() {
        boolean isgood = true;
        expandableListView.clearFocus();
        errorFlag = false;
        error_position_header.clear();
        error_position_child.clear();
        listheaderloop:
        for (int i = 0; i < listDataHeader.size(); i++) {
            WindowMaster windowMaster = listDataHeader.get(i);
            if (windowMaster.isExist() == true) {
                if (windowMaster.getImage() == null || windowMaster.getImage().equalsIgnoreCase("")) {
                    isgood = false;
                    errorFlag = true;
                    error_position_header.add(i);
                    msg = "Please click image";
                    expandableListView.invalidateViews();
                    break listheaderloop;
                } else {
                    ArrayList<ChecklistGetterSetter> checklist = listDataChild.get(windowMaster);
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
                    }
                }
            } else if (windowMaster.isExist() == false) {
                if (windowMaster.getReasonId() == 0) {
                    isgood = false;
                    errorFlag = true;
                    error_position_header.add(i);
                    msg = "Please Select Reason";
                    expandableListView.invalidateViews();
                    break listheaderloop;
                }
            }
        }
        return isgood;
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    //region ChecklistAdapter
    /*class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {

        private LayoutInflater inflator;

        ArrayList<ChecklistGetterSetter> data;

        public ChecklistAdapter(Context context, ArrayList<ChecklistGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.item_checklist, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.mItem = data.get(position);
            holder.tv_checklist.setText(holder.mItem.getChecklist());
            holder.ans = listDataChild.get(holder.mItem);
            holder.customAdapter = new CustomSpinnerAdapter(getApplicationContext(), holder.ans);
            holder.spinner.setAdapter(holder.customAdapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    answered_list.get(position).setANSWER_CD(holder.ans.get(pos).getAnswer_cd());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            for (int i = 0; i < holder.ans.size(); i++) {
                if (holder.ans.get(i).getAnswer_cd().equalsIgnoreCase(answered_list.get(position).getANSWER_CD())) {
                    holder.spinner.setSelection(i);
                    break;
                }
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_checklist;
            Spinner spinner;
            ChecklistGetterSetter mItem;
            ArrayList<AnswerChecklistGetterSetter> ans;
            CustomSpinnerAdapter customAdapter;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_checklist = (TextView) itemView.findViewById(R.id.tv_checklist);
                spinner = (Spinner) itemView.findViewById(R.id.spin_checklist_ans);
            }
        }
    }*/
    //endregion

    public class CustomSpinnerAdapter extends BaseAdapter {
        Context context;
        // int flags[];
        ArrayList<AnswerChecklistGetterSetter> ans;
        LayoutInflater inflter;

        public CustomSpinnerAdapter(Context applicationContext, ArrayList<AnswerChecklistGetterSetter> ans) {
            this.context = applicationContext;
            //this.flags = flags;
            this.ans = ans;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return ans.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.custom_spinner_item, null);
            //ImageView icon = (ImageView) view.findViewById(R.id.imageView);
            TextView names = (TextView) view.findViewById(R.id.tv_ans);
            //icon.setImageResource(flags[i]);
            names.setText(ans.get(i).getAnswer());
            return view;
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
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        //metadata_global = preferences.getString(CommonString.KEY_META_DATA, "");
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        }
        if (journeyPlan.getStoreTypeId() == 3) {
            getSupportActionBar().setTitle("Category Visibility");

        } else {
            getSupportActionBar().setTitle("Window/Asset");
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }


}
