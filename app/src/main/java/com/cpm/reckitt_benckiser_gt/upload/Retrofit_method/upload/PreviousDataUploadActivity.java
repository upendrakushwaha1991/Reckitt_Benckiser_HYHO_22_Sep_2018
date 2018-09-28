package com.cpm.reckitt_benckiser_gt.upload.Retrofit_method.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.delegates.CoverageBean;
import com.cpm.reckitt_benckiser_gt.getterSetter.CategoryMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMaster;
import com.cpm.reckitt_benckiser_gt.upload.Retrofit_method.UploadImageWithRetrofit;
import com.cpm.reckitt_benckiser_gt.utilities.AlertandMessages;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class PreviousDataUploadActivity extends AppCompatActivity {

    RBGTDatabase db;
    ArrayList<CoverageBean> coverageList;
    String date, userId, app_version;
    String Path;
    Data data;
    boolean uploadflag = false;
    ArrayList<JourneyPlan> storeList;
    Context context;
    boolean ResultFlag = true;
    Object result = "";
    private Dialog dialog;
    private ProgressBar pb;
    private ProgressDialog pd;
    private TextView percentage, message;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);

        db = new RBGTDatabase(this);
        db.open();
        context = this;
        Path = CommonString.FILE_PATH;
        isDataValid();
    }

    void isDataValid() {

        boolean flag_invalid = false;
        String from = "";
        db.open();
        JourneyPlan jcp = null;
        ArrayList<CoverageBean> coverage_list = db.getCoverageDataPrevious(date);
        for (int i = 0; i < coverage_list.size(); i++) {

            jcp = db.getSpecificStoreDataPrevious(date, coverage_list.get(i).getStoreId());
            if (jcp.getUploadStatus() != null && jcp.getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                flag_invalid = true;
                break;
            }
        }

        if (flag_invalid) {
            if (isValid(jcp)) {
                new checkoutData(jcp, from).execute();
            } else {
                new DeleteCoverageData(jcp.getStoreId().toString(), jcp.getVisitDate(), userId).execute();
            }
        } else {
            //start upload
            db.open();
            coverageList = db.getCoverageDataPrevious(date);
            if (coverageList.size() > 0) {
                pd = new ProgressDialog(context);
                pd.setCancelable(false);
                pd.setMessage("Uploading Data");
                pd.show();
                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pd, CommonString.TAG_FROM_PREVIOUS);
                upload.uploadDataUsingCoverageRecursive(coverageList, 0);
            }
        }

    }

    public boolean isValid(JourneyPlan journeyPlan) {
        boolean result = false;
        boolean flag_window = false, flag_category_dressing = false;
        db.open();

        ArrayList<WindowMaster> windowMasterArrayList = db.getWindowData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
        if (windowMasterArrayList.size() > 0) {
            flag_window = true;
        } else {
            flag_window = false;
        }

        ArrayList<CategoryMaster> categoryMasters = db.getCategoryDressingData(String.valueOf(journeyPlan.getStoreId()), journeyPlan.getVisitDate());
        if (categoryMasters.size() > 0) {
            flag_category_dressing = true;
        } else {
            flag_category_dressing = false;
        }

        if (flag_window && flag_category_dressing) {
            result = true;
        } else {
            result = false;
        }
        return result;

    }

    //region Validation for Self service

    public void showAlert(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //endregion

    class Data {
        int value;
        String name;
    }

    public class checkoutData extends AsyncTask<Void, Void, String> {

        private JourneyPlan cdata;
        private String from;

        checkoutData(JourneyPlan cdata, String from) {
            this.cdata = cdata;
            this.from = from;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle(getResources().getString(R.string.dialog_title));
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
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
                jsonObject.put("Latitude", "0");
                jsonObject.put("Longitude", "0");
                jsonObject.put("Checkout_Date", cdata.getVisitDate());

                String jsonString2 = jsonObject.toString();

                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                String result_str = upload.downloadDataUniversal(jsonString2, CommonString.CHECKOUTDetail);

                if (result_str.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                    throw new IOException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                    throw new SocketTimeoutException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                    throw new JsonSyntaxException("Check out Upload");
                } else if (result_str.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
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
                db.open();
                if (from.equalsIgnoreCase(CommonString.TAG_FROM_JCP)) {
                    db.updateCheckoutStatus(String.valueOf(cdata.getStoreId()), CommonString.KEY_C, CommonString.TABLE_Journey_Plan);
                } else {
                    db.updateCheckoutStatus(String.valueOf(cdata.getStoreId()), CommonString.KEY_C, CommonString.TABLE_Deviation_Journey_Plan);
                }
                //start upload
                // new UploadTask(context).execute();
                coverageList = db.getCoverageDataPrevious(date);
                if (coverageList.size() > 0) {
                    pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                    pd.setMessage("Uploading Data");
                    pd.show();
                    UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pd, CommonString.TAG_FROM_PREVIOUS);
                    upload.uploadDataUsingCoverageRecursive(coverageList, 0);
                }
            } else {
                showAlert(getString(R.string.datanotfound) + " " + result);
            }
        }

    }

    public class DeleteCoverageData extends AsyncTask<Void, Void, String> {

        String storeID, visitDate, userId;

        DeleteCoverageData(String storeId, String visitDate, String userId) {
            this.storeID = storeId;
            this.visitDate = visitDate;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle(getResources().getString(R.string.dialog_title));
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
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
                } else if (result_str.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
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
                db.open();
                db.deleteTableWithStoreID(storeID);
                //start upload
                // new UploadTask(context).execute();
                coverageList = db.getCoverageDataPrevious(date);
                if (coverageList.size() > 0) {
                    pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                    pd.setMessage("Uploading Data");
                    pd.show();
                    UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pd, CommonString.TAG_FROM_PREVIOUS);
                    upload.uploadDataUsingCoverageRecursive(coverageList, 0);
                } else {
                    AlertandMessages.showAlert((Activity) context, "Data Uploaded Successfully", true);
                }

            } else {
                showAlert(getString(R.string.datanotfound) + " " + result);
            }
        }

    }

}
