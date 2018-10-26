package com.cpm.reckitt_benckiser_gt.dailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
import com.cpm.reckitt_benckiser_gt.utilities.CamTestActivity;
import com.cpm.reckitt_benckiser_gt.utilities.CommonFunctions;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WindowChecklistActivity extends AppCompatActivity {

    RBGTDatabase db;
    Context context;
    SharedPreferences preferences;
    String username;
    JourneyPlan journeyPlan;
    Intent i;
    WindowMaster current;
    FloatingActionButton fab;
    ToggleButton switch_exists;
    Spinner reason_spinner;
    ImageButton camerabtn, camerabtn2;
    LinearLayout lay_Camera, lay_reason;
    ArrayList<ChecklistGetterSetter> data;
    CardView cardView;
    TextView lblListHeader;
    RecyclerView rec_checklist;
    ArrayList<WindowNonReasonGetterSetter> reasondata;
    ArrayAdapter reason_adapter;
    String _pathforcheck = "", _pathforcheck2 = "", path = "", msg = "", image = "";
    boolean errorFlag;
    ArrayList<Integer> error_position_child = new ArrayList();
    ChecklistAdapter adapter;
    ArrayList<ChecklistGetterSetter> answered_list = new ArrayList<>();
    HashMap<ChecklistGetterSetter, ArrayList<AnswerChecklistGetterSetter>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_checklist);
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
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                long commonId = db.InsertWindowsCheckListData(username, journeyPlan, current, data);
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

    public Boolean validatedata() {
        boolean isgood = true;
        rec_checklist.clearFocus();
        errorFlag = false;
        error_position_child.clear();
        WindowMaster windowMaster = current;
        if (windowMaster.isExist() == true) {
            if (windowMaster.getImage() == null || windowMaster.getImage().equalsIgnoreCase("")) {
                isgood = false;
                errorFlag = true;
                msg = "Please click image";
            } else if (windowMaster.getImage2() == null || windowMaster.getImage2().equalsIgnoreCase("")) {
                isgood = false;
                errorFlag = true;
                msg = "Please click image from camera 2";
            } else {
                ArrayList<ChecklistGetterSetter> checklist = data;
                for (int j = 0; j < checklist.size(); j++) {
                    ChecklistGetterSetter checkgetset = checklist.get(j);
                    if (checkgetset.getANSWER_CD() == null || checkgetset.getANSWER_CD().equalsIgnoreCase("") || checkgetset.getANSWER_CD().equalsIgnoreCase("0")) {
                        isgood = false;
                        errorFlag = true;
                        error_position_child.add(j);
                        msg = "Please select answer from checklist";
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } else if (windowMaster.isExist() == false) {
            if (windowMaster.getReasonId() == 0) {
                isgood = false;
                errorFlag = true;
                msg = "Please Select Reason";
                adapter.notifyDataSetChanged();
            }
        }
        return isgood;
    }


    void prepareList() {
        reasondata = new ArrayList<>();
        reasondata = db.getWindowNonReasonData();
        reason_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item);
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getWREASON().get(0));
        }
        reason_spinner.setAdapter(reason_adapter);

        for (int i = 0; i < reasondata.size(); i++) {
            if (reasondata.get(i).getWREASON_CD().get(0).equalsIgnoreCase(String.valueOf(current.getReasonId()))) {
                reason_spinner.setSelection(i);
                break;
            }
        }
        reason_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    current.setReasonId(Integer.parseInt(reasondata.get(position).getWREASON_CD().get(0)));
                    current.setReason(reasondata.get(position).getWREASON().get(0));
                } else {
                    current.setReasonId(0);
                    current.setReason("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        switch_exists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {
                    lay_Camera.setVisibility(View.VISIBLE);
                    lay_reason.setVisibility(View.GONE);
                    current.setExist(true);
                    reason_spinner.setSelection(0);
                    adapter.notifyDataSetChanged();
                    rec_checklist.setVisibility(View.VISIBLE);
                } else {
                    lay_Camera.setVisibility(View.GONE);
                    lay_reason.setVisibility(View.VISIBLE);
                    current.setExist(false);
                    if (current.getImage() != null
                            && !current.getImage().equalsIgnoreCase("")) {
                        if (new File(CommonString.FILE_PATH + current.getImage()).exists()) {
                            new File(CommonString.FILE_PATH + current.getImage()).delete();
                        }
                        current.setImage("");
                        camerabtn.setBackgroundResource(R.mipmap.camera_pink);
                    }
                    else {
                        current.setImage("");
                    }
                    if (current.getImage2() != null
                            && !current.getImage2().equalsIgnoreCase("")) {
                        if (new File(CommonString.FILE_PATH + current.getImage2()).exists()) {
                            new File(CommonString.FILE_PATH + current.getImage2()).delete();
                        }
                        current.setImage2("");
                        camerabtn2.setBackgroundResource(R.mipmap.camera_pink);
                    }
                    else {
                        current.setImage2("");
                    }
                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setANSWER_CD("0");
                    }

                    ArrayList<ChecklistGetterSetter> checklist = answered_list;
                    for (int i = 0; i < checklist.size(); i++) {
                        checklist.get(i).setANSWER_CD("0");
                    }
                    adapter.notifyDataSetChanged();
                    rec_checklist.setVisibility(View.GONE);
                }
            }
        });

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intime = CommonFunctions.getCurrentTime();
                _pathforcheck = journeyPlan.getStoreId() + "_Window-" + current.getWindowId() + "_" + journeyPlan.getVisitDate().replace("/", "") + "_" + intime.replace(":", "") + ".jpg";
                path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(context, path, null, false);
            }
        });

        camerabtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivityForResult(i, CommonString.GRID_CAMERA_REQUEST_CODE);
                String intime = CommonFunctions.getCurrentTime();
                _pathforcheck2 = journeyPlan.getStoreId() + "_WINDOW_withGrid-" + current.getWindowId() + "_" + journeyPlan.getVisitDate().replace("/", "") + "_" + intime.replace(":", "") + ".jpg";
                path = CommonString.FILE_PATH + _pathforcheck2;
                CommonFunctions.startAnncaCameraActivity(context, path, null, true);

            }
        });

        if (current.isExist() == true) {
            switch_exists.setChecked(true);
            lay_Camera.setVisibility(View.VISIBLE);
            lay_reason.setVisibility(View.GONE);
            rec_checklist.setVisibility(View.VISIBLE);
        } else {
            switch_exists.setChecked(false);
            rec_checklist.setVisibility(View.GONE);
            lay_Camera.setVisibility(View.GONE);
            lay_reason.setVisibility(View.VISIBLE);
        }

        if (current.getImage() != null && !current.getImage().equalsIgnoreCase("")) {
            camerabtn.setBackgroundResource(R.mipmap.camera_green);
        } else {
            camerabtn.setBackgroundResource(R.mipmap.camera_pink);
        }

        data = db.getChecklistData(String.valueOf(current.getWindowId()), journeyPlan);
        listDataChild = new HashMap<>();
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
            listDataChild.put(data.get(j), ans);
        }
        adapter = new ChecklistAdapter(context, data);
        rec_checklist.setAdapter(adapter);
        rec_checklist.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        if (requestCode == CommonString.GRID_CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result != null && !result.equals("")) {
                    camerabtn2.setBackgroundResource(R.mipmap.camera_green);
                    //this.img_path = imgpath;
                    current.setImage2(result);
                } else {
                    camerabtn2.setBackgroundResource(R.mipmap.camera_pink);
                    current.setImage2("");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                camerabtn2.setBackgroundResource(R.mipmap.camera_pink);
                current.setImage2("");
            }
        } else {
            switch (resultCode) {
                case 0:
                    Log.i("MakeMachine", "User cancelled");
                    break;
                case -1:
                    if (_pathforcheck != null && !_pathforcheck.equals("")) {
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            if (current.getImage() != null
                                    && !current.getImage().equalsIgnoreCase("")) {
                                if (new File(CommonString.FILE_PATH + current.getImage()).exists()) {
                                    new File(CommonString.FILE_PATH + current.getImage()).delete();
                                }
                                current.setImage("");
                            }
                            //String metadata = CommonFunctions.getMetadataAtImagesFromPref(metadata_global, "Window Image");
                            //CommonFunctions.addMetadataAndTimeStampToImage(context, CommonString1.FILE_PATH + _pathforcheck, metadata);
                            image = _pathforcheck;
                            current.setImage(image);
                            camerabtn.setBackgroundResource(R.mipmap.camera_green);
                            _pathforcheck = "";
                            image = "";
                        }
                    } else if (_pathforcheck2 != null && !_pathforcheck2.equals("")) {
                        if (new File(CommonString.FILE_PATH + _pathforcheck2).exists()) {
                            if (current.getImage2() != null
                                    && !current.getImage2().equalsIgnoreCase("")) {
                                if (new File(CommonString.FILE_PATH + current.getImage2()).exists()) {
                                    new File(CommonString.FILE_PATH + current.getImage2()).delete();
                                }
                                current.setImage2("");
                            }
                            //String metadata = CommonFunctions.getMetadataAtImagesFromPref(metadata_global, "Window Image");
                            //CommonFunctions.addMetadataAndTimeStampToImage(context, CommonString1.FILE_PATH + _pathforcheck, metadata);
                            image = _pathforcheck2;
                            current.setImage2(image);
                            camerabtn2.setBackgroundResource(R.mipmap.camera_green);
                            _pathforcheck2 = "";
                            image = "";
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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


    class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<ChecklistGetterSetter> data;

        public ChecklistAdapter(Context context, List<ChecklistGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.item_child_expandable_checklist, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            ChecklistGetterSetter checkList = (ChecklistGetterSetter) data.get(position);

            holder.tv_checklist.setText(checkList.getChecklist());

            final ArrayList<AnswerChecklistGetterSetter> answerList = listDataChild.get(checkList);
            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(context, answerList);
            holder.spinner.setAdapter(customSpinnerAdapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    data.get(position).setANSWER_CD(answerList.get(pos).getAnswer_cd());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (current.isExist()) {
                boolean isSelected = false;
                for (int i = 0; i < answerList.size(); i++) {
                    if (answerList.get(i).getAnswer_cd().equalsIgnoreCase(data.get(position).getANSWER_CD())) {
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
                if (error_position_child.contains(position)) {
                    holder.item_ll.setBackgroundResource(R.color.red);
                } else {
                    holder.item_ll.setBackgroundResource(R.color.white);
                }
            } else {
                holder.item_ll.setBackgroundResource(R.color.white);
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
            LinearLayout item_ll;
            ArrayList<AnswerChecklistGetterSetter> ans;
            CustomSpinnerAdapter customAdapter;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_checklist = (TextView) itemView.findViewById(R.id.tv_checklist);
                spinner = (Spinner) itemView.findViewById(R.id.spin_checklist_ans);
                item_ll = (LinearLayout) itemView.findViewById(R.id.lay_window);
            }
        }


    }

    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch_exists = (ToggleButton) findViewById(R.id.switch_exists);
        reason_spinner = (Spinner) findViewById(R.id.reason_spinner);
        camerabtn = (ImageButton) findViewById(R.id.image_window);
        camerabtn2 = (ImageButton) findViewById(R.id.image_window2);
        lblListHeader = (TextView) findViewById(R.id.txt_header);
        cardView = (CardView) findViewById(R.id.cardview_exists);
        lay_Camera = (LinearLayout) findViewById(R.id.lay_Camera);
        lay_reason = (LinearLayout) findViewById(R.id.lay_reason);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        db = new RBGTDatabase(context);
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        reasondata = new ArrayList<>();
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null
                && getIntent().getSerializableExtra(CommonString.TAG_WINDOW_OBJECT) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            current = (WindowMaster) getIntent().getSerializableExtra(CommonString.TAG_WINDOW_OBJECT);
        }
        getSupportActionBar().setTitle(current.getWindow());
        lblListHeader.setText(current.getBrand() + " - " + current.getWindow());
        rec_checklist = (RecyclerView) findViewById(R.id.rec_checklist);
        i = new Intent(context, CamTestActivity.class);
        i.putExtra(CommonString.TAG_OBJECT, journeyPlan);
    }

}
