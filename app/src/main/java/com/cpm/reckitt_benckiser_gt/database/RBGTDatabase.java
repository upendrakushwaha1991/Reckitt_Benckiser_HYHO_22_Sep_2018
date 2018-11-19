package com.cpm.reckitt_benckiser_gt.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.delegates.CoverageBean;
import com.cpm.reckitt_benckiser_gt.getterSetter.AnswerChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.BrandMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.BrandMasterGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.CategoryMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.CategoryMasterGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.ChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.GeotaggingBeans;
import com.cpm.reckitt_benckiser_gt.getterSetter.JCPGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingCategoryChecklist;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingCategoryChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingCategoryDressing;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingCategoryDressingGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingInitiativeGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingVisibilityInitiative;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingWindChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.MappingWindowChecklist;
import com.cpm.reckitt_benckiser_gt.getterSetter.NonCategoryReason;
import com.cpm.reckitt_benckiser_gt.getterSetter.NonCategoryReasonGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.NonWindowReason;
import com.cpm.reckitt_benckiser_gt.getterSetter.NonWindowReasonGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.NonWorkingReason;
import com.cpm.reckitt_benckiser_gt.getterSetter.NonWorkingReasonGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowCheckAnswerGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowChecklist;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowChecklistAnswer;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowChecklistGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMaster;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowMasterGetterSetter;
import com.cpm.reckitt_benckiser_gt.getterSetter.WindowNonReasonGetterSetter;
import com.cpm.reckitt_benckiser_gt.utilities.CommonString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RBGTDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RB_HYHO_DB1";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    Context context;

    public RBGTDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //jeevan
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_WINDOWS_DATA);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_CHECKLIST_DATA);
            db.execSQL(CommonString.CREATE_TABLE_CATEGORY_DRESSING_DATA);
            db.execSQL(CommonString.CREATE_TABLE_INSERT_CATEGORY_DRESSING_CHECKLIST_DATA);
            db.execSQL(CommonString.CREATE_TABLE_STORE_GEOTAGGING);
            db.execSQL(CommonString.CREATE_TABLE_CATEGORY_DBSR_DATA);
            db.execSQL(CommonString.CREATE_TABLE_Journey_Plan_DBSR_Saved);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int createtable(String sqltext) {
        try {
            db.execSQL(sqltext);
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    public void deleteTableWithStoreID(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_WINDOWS_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_CHECKLIST_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_CATEGORY_DRESSING_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_INSERT_CATEGORY_DRESSING_CHECKLIST_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        //db.delete(CommonString.TABLE_Journey_Plan_DBSR_Saved, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_CATEGORY_DBSR_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
    }

    public void updateStatus(String id, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put("GEO_TAG", status);
            db.update(CommonString.KEY_JOURNEY_PLAN, values, CommonString.KEY_STORE_ID + "='" + id + "'", null);
        } catch (Exception ex) {
        }
    }

    public long InsertSTOREgeotag(String storeid, double lat, double longitude, String path, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put("STORE_ID", storeid);
            values.put("LATITUDE", Double.toString(lat));
            values.put("LONGITUDE", Double.toString(longitude));
            values.put("FRONT_IMAGE", path);
            values.put("GEO_TAG", status);
            values.put("STATUS", status);

            return db.insert(CommonString.TABLE_STORE_GEOTAGGING, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }

    public long updateInsertedGeoTagStatus(String id, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put("GEO_TAG", status);
            values.put("STATUS", status);
            return db.update(CommonString.TABLE_STORE_GEOTAGGING, values, CommonString.KEY_STORE_ID + "='" + id + "'", null);
        } catch (Exception ex) {
            return 0;
        }
    }

    public ArrayList<CoverageBean> getCoverageDataPrevious(String visitdate) {

        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                            + CommonString.KEY_VISIT_DATE + " <> '" + visitdate + "'",
                    null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    //sb.setCkeckout_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKOUT_IMAGE)));

                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }


    public ArrayList<CoverageBean> getCoverageWithStoreIDAndVisitDate_Data(String store_id, String visitdate) {

        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_STORE_ID + "='" + store_id + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setVisitDate((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setImage((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    if (dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)) == null) {
                        sb.setRemark("");
                    } else {
                        sb.setRemark((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    }
                    sb.setReason((((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON))))));

                   /* sb.setInTime(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IN_TIME)))));
                    sb.setOutTime(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_OUT_TIME)))));*/
                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))))));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<WindowNonReasonGetterSetter> getWindowNonReasonData() {
        ArrayList<WindowNonReasonGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select 0 as WReason_Id,'Select' as WReason union SELECT WReason_Id,WReason FROM Non_Window_Reason", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowNonReasonGetterSetter sb = new WindowNonReasonGetterSetter();
                    sb.setWREASON_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("WReason_Id")));
                    sb.setWREASON(dbcursor.getString(dbcursor.getColumnIndexOrThrow("WReason")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!", e.toString());
            return list;
        }
        Log.d("Fetching non working data---------------------->Stop<-----------", "-------------------");
        return list;
    }


    public ArrayList<NonCategoryReason> getCategoryNonReasonData() {
        ArrayList<NonCategoryReason> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select 0 as CReason_Id,'Select' as CReason union SELECT CReason_Id,CReason FROM Non_Category_Reason", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonCategoryReason sb = new NonCategoryReason();
                    sb.setCReasonId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("CReason_Id")));
                    sb.setCReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CReason")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Non working!!!!!!!!!!!", e.toString());
            return list;
        }
        Log.d("Fetching non working data---------------------->Stop<-----------", "-------------------");
        return list;
    }


    public ArrayList<WindowMaster> getWindowListData(JourneyPlan jcp) {
        Log.d("Fetchecklidata->Start<-", "-");
        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
/*            dbcursor = db.rawQuery(" select wmap.Brand_id as Brand_id, wm.Window_id as Window_id, wm.window as window from window_master wm " +
                    "inner join Mapping_Visibility_Initiatives wmap on wm.window_id=wmap.window_id " +
                    "where state_id = " + jcp.getStateId() + " and Distributor_id = " + jcp.getDistributorId() + " and Store_Type_Id = " + jcp.getStoreTypeId() + "", null);*/

            dbcursor = db.rawQuery("select wmap.Brand_id as Brand_id,bm.Brand as Brand, wm.Window_id as Window_id, wm.window as window, IFNULL(d.Existornot,'false') as EXISTORNOT, IFNULL(d.window_Image,'') as WINDOW_IMAGE,IFNULL( d.Reason_Id,'0') as REASON_ID from window_master wm inner join Mapping_Visibility_Initiatives wmap on wm.window_id = wmap.window_id" +
                    " inner join Brand_Master bm on wmap.Brand_Id = bm.Brand_Id left join" +
                    " (select * from WINDOWS_DATA Where Store_Id = " + jcp.getStoreId() + " and Date ='" + jcp.getVisitDate() + "') as d on wm.Window_Id = d.WINDOW_CD" +
                    "  where state_id = " + jcp.getStateId() + " and Distributor_id = " + jcp.getDistributorId() + " and Store_Type_Id = " + jcp.getStoreTypeId() + " and Classification_Id = " + jcp.getClassificationId() + "", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    WindowMaster ch = new WindowMaster();
                    ch.setBrand_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand_id")));
                    ch.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand")));
                    ch.setWindowId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Window_id")));
                    ch.setWindow(dbcursor.getString(dbcursor.getColumnIndexOrThrow("window")));
                    ch.setExist(Boolean.parseBoolean(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EXISTORNOT"))));
                    ch.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("WINDOW_IMAGE")));
                    ch.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_ID"))));
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get windows list!", e.toString());
            return list;
        }
        Log.d("Fetching check->Stop<", "-");
        return list;
    }

    public ArrayList<CategoryMaster> getCategoryDressingData(JourneyPlan jcp) {
        Log.d("Fetchecklidata->Start<-", "-");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery(" select cm.Category_Id as Category_Id, cm.Category as Category, IFNULL(d.EXISTORNOT,'false') as EXISTORNOT, IFNULL(d.CATEGORY_IMAGE,'') as CATEGORY_IMAGE,IFNULL( d.REASON_ID,'0') as REASON_ID from Category_Master cm  " +
                    " inner join Mapping_Category_Dressing cmap on cm.Category_Id=cmap.Category_Id " +
                    " left join " +
                    " (select * from CATEGORY_DRESSING_DATA Where STORE_ID = " + jcp.getStoreId() + " and DATE ='" + jcp.getVisitDate() + "') as d on cm.Category_Id = d.CATEGORY_CD " +
                    " where state_id = " + jcp.getStateId() + " and Distributor_id = " + jcp.getDistributorId() + " and Store_Type_Id = " + jcp.getStoreTypeId() + " order by Category_Sequence", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster ch = new CategoryMaster();
                    ch.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    ch.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));
                    ch.setExist(Boolean.parseBoolean(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EXISTORNOT"))));
                    ch.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY_IMAGE")));
                    ch.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("REASON_ID"))));
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get windows list!", e.toString());
            return list;
        }
        Log.d("Fetching check->Stop<", "-");
        return list;
    }

    public ArrayList<BrandMaster> getBrandFromCategory(Integer categoryCd) {
        Log.d("Fetchecklidata->Start<-", "-");
        ArrayList<BrandMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select * from Brand_Master where Category_Id = '" + categoryCd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    BrandMaster ch = new BrandMaster();
                    ch.setBrandId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Brand_Id")));
                    ch.setBrand(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Brand")));
                    //ch.setAnswer("");
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get windows list!", e.toString());
            return list;
        }
        Log.d("Fetching check->Stop<", "-");
        return list;
    }



    public ArrayList<CategoryMaster> getCategoryDBSRData(JourneyPlan jcp) {
        Log.d("Fetchecklidata->Start<-", "-");
        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            /*dbcursor = db.rawQuery(" select cm.Category_Id as Category_Id, cm.Category as Category, IFNULL(d.EXISTORNOT,'false') as EXISTORNOT, IFNULL(d.CATEGORY_IMAGE,'') as CATEGORY_IMAGE,IFNULL( d.REASON_ID,'0') as REASON_ID from Category_Master cm  " +
                    " inner join Mapping_Category_Dressing cmap on cm.Category_Id=cmap.Category_Id " +
                    " left join " +
                    " (select * from CATEGORY_DRESSING_DATA Where STORE_ID = " + jcp.getStoreId() + " and DATE ='" + jcp.getVisitDate() + "') as d on cm.Category_Id = d.CATEGORY_CD " +
                    " where state_id = " + jcp.getStateId() + " and Distributor_id = " + jcp.getDistributorId() + " and Store_Type_Id = " + jcp.getStoreTypeId() + " order by Category_Sequence", null);*/


            dbcursor = db.rawQuery("Select cm.Category_Id,cm.Category,IFNULL(cs.EXISTORNOT,'false') as EXISTORNOT,IFNULL(cs.CATEGORY_IMAGE,'') as CATEGORY_IMAGE from Category_Master cm " +
                    " left join (Select * from CATEGORY_DBSR_DATA) as cs on cm.Category_Id = cs.CATEGORY_CD" +
                    " and cs.STORE_ID = " + jcp.getStoreId() + " and cs.DATE = '" + jcp.getVisitDate() + "' order by cm.Category_Sequence", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CategoryMaster ch = new CategoryMaster();
                    ch.setCategoryId(dbcursor.getInt(dbcursor.getColumnIndexOrThrow("Category_Id")));
                    ch.setCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Category")));
                    ch.setExist(Boolean.parseBoolean(dbcursor.getString(dbcursor.getColumnIndexOrThrow("EXISTORNOT"))));
                    ch.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CATEGORY_IMAGE")));
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get windows list!", e.toString());
            return list;
        }
        Log.d("Fetching check->Stop<", "-");
        return list;
    }


    public ArrayList<ChecklistGetterSetter> getChecklistData(String window_cd, JourneyPlan journeyPlan) {
        Log.d("Fetchecklidata->Start<-", "-");
        ArrayList<ChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
           /* dbcursor = db.rawQuery("SELECT DISTINCT WC.Checklist_Id as CHECKLIST_CD, WC.Checklist as CHECKLIST" +
                    " FROM MAPPING_WINDOW_CHECKLIST MWC INNER JOIN WINDOW_CHECKLIST WC ON MWC.Checklist_Id = WC.Checklist_Id" +
                    " WHERE MWC.window_id ='" + window_cd + "'", null);*/

            dbcursor = db.rawQuery("SELECT DISTINCT WC.Checklist_Id as CHECKLIST_CD, WC.Checklist as CHECKLIST,d.ANSWER_CD as ANSWER_CD  FROM  MAPPING_WINDOW_CHECKLIST MWC " +
                    " INNER JOIN WINDOW_CHECKLIST WC ON MWC.Checklist_Id = WC.Checklist_Id " +
                    "  left join " +
                    "  (select * from CheckList_DATA Where STORE_ID = " + journeyPlan.getStoreId() + " and DATE ='" + journeyPlan.getVisitDate() + "') as d " +
                    "  on WC.Checklist_Id = d.CHECKLIST_CD and MWC.Window_id = d.WINDOW_CD " +
                    " WHERE MWC.window_id ='" + window_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ChecklistGetterSetter ch = new ChecklistGetterSetter();
                    ch.setChecklist_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_CD")));
                    ch.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST")));
                    ch.setANSWER_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_CD")));
                    //ch.setAnswer("");
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get windows list!", e.toString());
            return list;
        }
        Log.d("Fetching check->Stop<", "-");
        return list;
    }


    public ArrayList<ChecklistGetterSetter> getCategoryDressingChecklistData(String categoryId, JourneyPlan journeyPlan) {
        Log.d("Fetchecklidata->Start<-", "-");
        ArrayList<ChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            /*dbcursor = db.rawQuery("SELECT DISTINCT WC.Checklist_Id as CHECKLIST_CD, WC.Checklist as CHECKLIST" +
                    " FROM Mapping_Category_Checklist MWC INNER JOIN WINDOW_CHECKLIST WC ON MWC.Checklist_Id = WC.Checklist_Id" +
                    " WHERE MWC.Category_Id ='" + categoryId + "'", null);*/

            dbcursor = db.rawQuery("SELECT DISTINCT WC.Checklist_Id as CHECKLIST_CD, WC.Checklist as CHECKLIST,d.ANSWER_CD as ANSWER_CD" +
                    " FROM Mapping_Category_Checklist MWC INNER JOIN WINDOW_CHECKLIST WC ON MWC.Checklist_Id = WC.Checklist_Id" +
                    "  left join" +
                    "  (select * from CATEGORY_DRESSING_CHECKLIST_DATA Where STORE_ID = " + journeyPlan.getStoreId() + " and DATE ='" + journeyPlan.getVisitDate() + "') as d " +
                    "  on WC.Checklist_Id = d.CHECKLIST_CD and MWC.Category_Id = d.CATEGORY_CD " +
                    " WHERE MWC.Category_Id ='" + categoryId + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ChecklistGetterSetter ch = new ChecklistGetterSetter();
                    ch.setChecklist_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST_CD")));
                    ch.setChecklist(dbcursor.getString(dbcursor.getColumnIndexOrThrow("CHECKLIST")));
                    ch.setANSWER_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_CD")));
                    //ch.setAnswer_type(dbcursor.getString(dbcursor.getColumnIndexOrThrow("ANSWER_TYPE")));
                    ch.setAnswer("");
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get windows list!", e.toString());
            return list;
        }
        Log.d("Fetching check->Stop<", "-");
        return list;
    }


    public ArrayList<AnswerChecklistGetterSetter> getChecklistAnswerData(String checklist_cd) {
        Log.d("Fetanswerdata->Start<-", "-");
        ArrayList<AnswerChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT DISTINCT Checklist_Id, Answer_Id, Answer FROM WINDOW_CHECKLIST_ANSWER  WHERE Checklist_Id =" + checklist_cd + "", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    AnswerChecklistGetterSetter ch = new AnswerChecklistGetterSetter();
                    ch.setChecklist_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Checklist_Id")));
                    ch.setAnswer_cd(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Answer_Id")));
                    ch.setAnswer(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Answer")));
                    list.add(ch);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exc get ans list!", e.toString());
            return list;
        }
        Log.d("Fet checklist->Stop<", "-");
        return list;
    }


    public long InsertWindowsData(String username, JourneyPlan jcp, ArrayList<WindowMaster> listDataHeader, HashMap<WindowMaster, ArrayList<ChecklistGetterSetter>> listDataChild) {
        long l = 0, common_id = 0;
        db.delete(CommonString.TABLE_WINDOWS_DATA, "STORE_ID ='" + jcp.getStoreId() + "'", null);
        db.delete(CommonString.TABLE_INSERT_CHECKLIST_DATA, "STORE_ID ='" + jcp.getStoreId() + "'", null);
        ContentValues values;
        ContentValues value2 = new ContentValues();
        try {

            for (int i = 0; i < listDataHeader.size(); i++) {
                values = new ContentValues();
                values.put("DATE", jcp.getVisitDate());
                values.put(CommonString.KEY_USER_ID, username);
                values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
                values.put("WINDOW_CD", listDataHeader.get(i).getWindowId());
                values.put("BRAND_ID", listDataHeader.get(i).getBrand_Id());
                values.put("EXISTORNOT", String.valueOf(listDataHeader.get(i).isExist()));
                values.put("WINDOW_IMAGE", listDataHeader.get(i).getImage());
                values.put("WINDOW_IMAGE2", listDataHeader.get(i).getImage2());
                values.put("REASON_ID", listDataHeader.get(i).getReasonId());

                common_id = db.insert(CommonString.TABLE_WINDOWS_DATA, null, values);

                if (listDataHeader.get(i).isExist()) {
                    ArrayList<ChecklistGetterSetter> checklist = listDataChild.get(listDataHeader.get(i));
                    for (int j = 0; j < checklist.size(); j++) {
                        values = new ContentValues();
                        values.put("DATE", jcp.getVisitDate());
                        values.put(CommonString.KEY_USER_ID, username);
                        values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
                        values.put("WINDOW_CD", listDataHeader.get(i).getWindowId());
                        values.put(CommonString.KEY_COMMON_ID, String.valueOf(common_id));
                        values.put("CHECKLIST_CD", checklist.get(j).getChecklist_cd());
                        values.put("ANSWER_CD", checklist.get(j).getANSWER_CD());

                        l = db.insert(CommonString.TABLE_INSERT_CHECKLIST_DATA, null, values);
                    }
                }
            }

            if (common_id > 0) {
                return common_id;
            } else {
                return 0;
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Store Data ", ex.toString());
            return 0;
        }
    }

    public long InsertWindowsCheckListData(String username, JourneyPlan jcp, WindowMaster windowMaster, ArrayList<ChecklistGetterSetter> listDataChild) {
        long l = 0, common_id = 0;
        db.delete(CommonString.TABLE_WINDOWS_DATA, "STORE_ID ='" + jcp.getStoreId() + "' AND WINDOW_CD = " + windowMaster.getWindowId() + "", null);
        db.delete(CommonString.TABLE_INSERT_CHECKLIST_DATA, "STORE_ID ='" + jcp.getStoreId() + "' AND WINDOW_CD = " + windowMaster.getWindowId() + "", null);
        ContentValues values;
        ContentValues value2 = new ContentValues();
        try {


            values = new ContentValues();
            values.put("DATE", jcp.getVisitDate());
            values.put(CommonString.KEY_USER_ID, username);
            values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
            values.put("WINDOW_CD", windowMaster.getWindowId());
            values.put("BRAND_ID", windowMaster.getBrand_Id());
            values.put("EXISTORNOT", String.valueOf(windowMaster.isExist()));
            values.put("WINDOW_IMAGE", windowMaster.getImage());
            values.put("WINDOW_IMAGE2", windowMaster.getImage2());
            values.put("REASON_ID", windowMaster.getReasonId());

            common_id = db.insert(CommonString.TABLE_WINDOWS_DATA, null, values);

            if (windowMaster.isExist()) {
                ArrayList<ChecklistGetterSetter> checklist = listDataChild;
                for (int j = 0; j < checklist.size(); j++) {
                    values = new ContentValues();
                    values.put("DATE", jcp.getVisitDate());
                    values.put(CommonString.KEY_USER_ID, username);
                    values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
                    values.put("WINDOW_CD", windowMaster.getWindowId());
                    values.put(CommonString.KEY_COMMON_ID, String.valueOf(common_id));
                    values.put("CHECKLIST_CD", checklist.get(j).getChecklist_cd());
                    values.put("ANSWER_CD", checklist.get(j).getANSWER_CD());

                    l = db.insert(CommonString.TABLE_INSERT_CHECKLIST_DATA, null, values);
                }
            }

            if (common_id > 0) {
                return common_id;
            } else {
                return 0;
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Store Data ", ex.toString());
            return 0;
        }
    }

    public long InsertCategoryDressingData(String username, JourneyPlan jcp, ArrayList<CategoryMaster> listDataHeader, HashMap<CategoryMaster, ArrayList<ChecklistGetterSetter>> listDataChild) {
        long l = 0, common_id = 0;
        db.delete(CommonString.TABLE_CATEGORY_DRESSING_DATA, "STORE_ID ='" + jcp.getStoreId() + "'", null);
        db.delete(CommonString.TABLE_INSERT_CATEGORY_DRESSING_CHECKLIST_DATA, "STORE_ID ='" + jcp.getStoreId() + "'", null);
        ContentValues values;
        ContentValues value2 = new ContentValues();
        try {

            for (int i = 0; i < listDataHeader.size(); i++) {
                values = new ContentValues();
                values.put("DATE", jcp.getVisitDate());
                values.put(CommonString.KEY_USER_ID, username);
                values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
                values.put("CATEGORY_CD", listDataHeader.get(i).getCategoryId());
                values.put("EXISTORNOT", String.valueOf(listDataHeader.get(i).isExist()));
                values.put("CATEGORY_IMAGE", listDataHeader.get(i).getImage());
                values.put("REASON_ID", listDataHeader.get(i).getReasonId());

                common_id = db.insert(CommonString.TABLE_CATEGORY_DRESSING_DATA, null, values);

                if (listDataHeader.get(i).isExist()) {
                    ArrayList<ChecklistGetterSetter> checklist = listDataChild.get(listDataHeader.get(i));
                    for (int j = 0; j < checklist.size(); j++) {
                        values = new ContentValues();
                        values.put("DATE", jcp.getVisitDate());
                        values.put(CommonString.KEY_USER_ID, username);
                        values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
                        values.put("CATEGORY_CD", listDataHeader.get(i).getCategoryId());
                        values.put(CommonString.KEY_COMMON_ID, String.valueOf(common_id));
                        values.put("CHECKLIST_CD", checklist.get(j).getChecklist_cd());
                        values.put("ANSWER_CD", checklist.get(j).getANSWER_CD());

                        l = db.insert(CommonString.TABLE_INSERT_CATEGORY_DRESSING_CHECKLIST_DATA, null, values);
                    }
                }

            }

            if (common_id > 0) {
                return common_id;
            } else {
                return 0;
            }

        } catch (Exception ex) {
            Log.d("Database Exception while Insert Store Data ", ex.toString());
            return 0;
        }

    }


    public long InsertCategoryDBSRData(String username, JourneyPlan jcp, ArrayList<CategoryMaster> listDataHeader) {
        long l = 0, common_id = 0;
        db.delete(CommonString.TABLE_CATEGORY_DBSR_DATA, "STORE_ID ='" + jcp.getStoreId() + "'", null);
        ContentValues values;
        try {

            for (int i = 0; i < listDataHeader.size(); i++) {
                values = new ContentValues();
                values.put("DATE", jcp.getVisitDate());
                values.put(CommonString.KEY_USER_ID, username);
                values.put(CommonString.KEY_STORE_ID, jcp.getStoreId());
                values.put("CATEGORY_CD", listDataHeader.get(i).getCategoryId());
                values.put("EXISTORNOT", String.valueOf(listDataHeader.get(i).isExist()));
                values.put("CATEGORY_IMAGE", listDataHeader.get(i).getImage());

                common_id = db.insert(CommonString.TABLE_CATEGORY_DBSR_DATA, null, values);
            }

            if (common_id > 0) {
                return common_id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Store Data ", ex.toString());
            return 0;
        }

    }

    public ArrayList<CategoryMaster> getCategoryDBSRSavedData(String storeid, String visit_date) {

        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * From " + CommonString.TABLE_CATEGORY_DBSR_DATA + " where " + CommonString.KEY_STORE_ID + " = " + storeid + " and " + CommonString.KEY_DDATE + " = '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    CategoryMaster categoryMaster = new CategoryMaster();
                    categoryMaster.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IID)));
                    categoryMaster.setCategoryId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CATEGORY_CD))));
                    categoryMaster.setExist(Boolean.parseBoolean(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EXISTORNOT))));
                    categoryMaster.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CATEGORY_IMAGE)));

                    list.add(categoryMaster);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }


    public ArrayList<WindowMaster> getWindowData(String storeid, String visit_date) {

        ArrayList<WindowMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * From " + CommonString.TABLE_WINDOWS_DATA + " where " + CommonString.KEY_STORE_ID + " = " + storeid + " and " + CommonString.KEY_DDATE + " = '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    WindowMaster windowMaster = new WindowMaster();
                    windowMaster.setKey_Id(String.valueOf(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IID))));
                    windowMaster.setBrand_Id(String.valueOf(dbcursor.getInt(dbcursor.getColumnIndexOrThrow(CommonString.KEY_BRAND_ID))));
                    windowMaster.setWindowId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_WINDOW_CD))));
                    windowMaster.setExist(Boolean.parseBoolean(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EXISTORNOT))));
                    windowMaster.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_WINDOW_IMAGE)));
                    windowMaster.setImage2(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_WINDOW_IMAGE2)));
                    windowMaster.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))));

                    list.add(windowMaster);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<ChecklistGetterSetter> getWindowCheckListData(String storeid, String visit_date, String commonId) {
        ArrayList<ChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * From " + CommonString.TABLE_INSERT_CHECKLIST_DATA + " where " + CommonString.KEY_STORE_ID + " = " + storeid + " and " + CommonString.KEY_DDATE + " = '" + visit_date + "' and " + CommonString.KEY_COMMON_ID + " = " + commonId + "", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ChecklistGetterSetter checklistGetSet = new ChecklistGetterSetter();

                    checklistGetSet.setCOMMON_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COMMON_ID)));
                    checklistGetSet.setWINDOW_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_WINDOW_CD)));
                    checklistGetSet.setCHECKLIST_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_CD)));
                    checklistGetSet.setANSWER_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_CD)));

                    list.add(checklistGetSet);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<ChecklistGetterSetter> getCategoryDressingCheckListData(String storeid, String visit_date, String commonId) {
        ArrayList<ChecklistGetterSetter> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * From " + CommonString.TABLE_INSERT_CATEGORY_DRESSING_CHECKLIST_DATA + " where " + CommonString.KEY_STORE_ID + " = " + storeid + " and " + CommonString.KEY_DDATE + " = '" + visit_date + "' and " + CommonString.KEY_COMMON_ID + " = " + commonId + "", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    ChecklistGetterSetter checklistGetSet = new ChecklistGetterSetter();

                    checklistGetSet.setCOMMON_ID(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COMMON_ID)));
                    checklistGetSet.setWINDOW_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CATEGORY_CD)));
                    checklistGetSet.setCHECKLIST_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKLIST_CD)));
                    checklistGetSet.setANSWER_CD(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ANSWER_CD)));

                    list.add(checklistGetSet);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<CategoryMaster> getCategoryDressingData(String storeid, String visit_date) {

        ArrayList<CategoryMaster> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("select * From " + CommonString.TABLE_CATEGORY_DRESSING_DATA + " where " + CommonString.KEY_STORE_ID + " = " + storeid + " and " + CommonString.KEY_DDATE + " = '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    CategoryMaster categoryMaster = new CategoryMaster();
                    categoryMaster.setKey_Id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IID)));
                    categoryMaster.setCategoryId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CATEGORY_CD))));
                    categoryMaster.setExist(Boolean.parseBoolean(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EXISTORNOT))));
                    categoryMaster.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CATEGORY_IMAGE)));
                    categoryMaster.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID))));

                    list.add(categoryMaster);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return list;
        }
        return list;
    }

    public long updateCheckoutStatus(String id, String status, String table) {
        ContentValues values = new ContentValues();
        try {
            values.put("Upload_Status", status);
            return db.update(table, values, "Store_Id " + " = '" + id + "'", null);
        } catch (Exception ex) {
            Log.e("Exception", " Journey_Plan" + ex.toString());
            return 0;
        }
    }

    public void updateStoreStatus(String storeid, String visitdate, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put("Upload_Status", status);
            db.update("Journey_Plan", values, "Store_Id ='" + storeid + "' AND Visit_Date ='" + visitdate + "'", null);
        } catch (Exception e) {

        }
    }

    public long InsertCoverageData(CoverageBean data) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, "STORE_ID" + "='" + data.getStoreId() + "' AND VISIT_DATE='" + data.getVisitDate() + "'", null);
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put(CommonString.KEY_STORE_ID, data.getStoreId());
            values.put(CommonString.KEY_USER_ID, data.getUserId());
            values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
            values.put(CommonString.KEY_LATITUDE, data.getLatitude());
            values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
            values.put(CommonString.KEY_IMAGE, data.getImage());
            values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
            values.put(CommonString.KEY_REASON_ID, data.getReasonid());
            values.put(CommonString.KEY_REASON, data.getReason());
            values.put(CommonString.KEY_CHECKOUT_IMAGE, data.getCkeckout_image());
            l = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);
        } catch (Exception ex) {
            Log.d("Database Exception while Insert Closes Data ", ex.toString());
        }
        return l;
    }

    public long updateStoreStatusOnLeave(String storeid, String visitdate, String status) {
        long id = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);
            id = db.update(CommonString.TABLE_Journey_Plan, values, CommonString.KEY_STORE_ID + "='" + storeid + "' AND "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate
                    + "'", null);
            return id;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean insertJCPData(JCPGetterSetter data) {
        db.delete("Journey_Plan", null, null);
        List<JourneyPlan> jcpList = data.getJourneyPlan();
        ContentValues values = new ContentValues();
        try {
            if (jcpList.size() == 0) {
                return false;
            }
            for (int i = 0; i < jcpList.size(); i++) {
                values.put("Store_Id", jcpList.get(i).getStoreId());
                values.put("Visit_Date", jcpList.get(i).getVisitDate());
                values.put("Distributor", jcpList.get(i).getDistributorN());
                values.put("Store_Name", jcpList.get(i).getStoreName());
                values.put("Address1", jcpList.get(i).getAddress1());
                values.put("Address2", jcpList.get(i).getAddress2());
                values.put("Landmark", jcpList.get(i).getLandmark());
                values.put("Pincode", jcpList.get(i).getPincode());
                values.put("Contact_Person", jcpList.get(i).getContactPerson());
                values.put("Contact_No", jcpList.get(i).getContactNo());
                values.put("City", jcpList.get(i).getCity());
                values.put("Store_Type", jcpList.get(i).getStoreType());
                values.put("Store_Category", jcpList.get(i).getStoreCategory());
                values.put("State_Id", jcpList.get(i).getStateId());
                values.put("Store_Type_Id", jcpList.get(i).getStoreTypeId());
                values.put("Store_Category_Id", jcpList.get(i).getStoreCategoryId());
                values.put("Reason_Id", jcpList.get(i).getReasonId());
                values.put("Upload_Status", jcpList.get(i).getUploadStatus());
                values.put("Geo_Tag", jcpList.get(i).getGeoTag());
                values.put("Distributor_Id", jcpList.get(i).getDistributorId());
                values.put("Classification_Id", jcpList.get(i).getClassificationId());
                values.put("Latitude", jcpList.get(i).getLatitude());
                values.put("Longitude", jcpList.get(i).getLongitude());
                values.put("GeoFencing", jcpList.get(i).getGeoFencing());

                long id = db.insert("Journey_Plan", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception in Jcp", ex.toString());
            return false;
        }
    }

    public boolean insertJCP_DBSRData(JCPGetterSetter data) {
        db.delete("Journey_Plan_DBSR", null, null);
        List<JourneyPlan> jcpList = data.getJourneyPlan_dbsr();
        ContentValues values = new ContentValues();
        try {
            if (jcpList.size() == 0) {
                return false;
            }
            for (int i = 0; i < jcpList.size(); i++) {
                values.put("Store_Id", jcpList.get(i).getStoreId());
                values.put("Visit_Date", jcpList.get(i).getVisitDate());
                values.put("Distributor", jcpList.get(i).getDistributorN());
                values.put("Store_Name", jcpList.get(i).getStoreName());
                values.put("Address1", jcpList.get(i).getAddress1());
                values.put("Address2", jcpList.get(i).getAddress2());
                values.put("Landmark", jcpList.get(i).getLandmark());
                values.put("Pincode", jcpList.get(i).getPincode());
                values.put("Contact_Person", jcpList.get(i).getContactPerson());
                values.put("Contact_No", jcpList.get(i).getContactNo());
                values.put("City", jcpList.get(i).getCity());
                values.put("Store_Type", jcpList.get(i).getStoreType());
                values.put("Store_Category", jcpList.get(i).getStoreCategory());
                values.put("State_Id", jcpList.get(i).getStateId());
                values.put("Store_Type_Id", jcpList.get(i).getStoreTypeId());
                values.put("Store_Category_Id", jcpList.get(i).getStoreCategoryId());
                values.put("Reason_Id", jcpList.get(i).getReasonId());
                values.put("Upload_Status", jcpList.get(i).getUploadStatus());
                values.put("Geo_Tag", jcpList.get(i).getGeoTag());
                values.put("Distributor_Id", jcpList.get(i).getDistributorId());
                values.put("Weekly_Upload", jcpList.get(i).getWeeklyUpload());
                long id = db.insert("Journey_Plan_DBSR", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception in Journey_Plan_DBSR", ex.toString());
            return false;
        }
    }


    public boolean insertJCP_DBSRSavedData(JourneyPlan data) {
        db.delete(CommonString.TABLE_Journey_Plan_DBSR_Saved, "Store_Id = " + data.getStoreId() + "", null);
        ContentValues values = new ContentValues();
        try {
            if (data == null) {
                return false;
            }
            values.put("Store_Id", data.getStoreId());
            values.put("Visit_Date", data.getVisitDate());
            values.put("Distributor", data.getDistributorN());
            values.put("Store_Name", data.getStoreName());
            values.put("Address1", data.getAddress1());
            values.put("Address2", data.getAddress2());
            values.put("Landmark", data.getLandmark());
            values.put("Pincode", data.getPincode());
            values.put("Contact_Person", data.getContactPerson());
            values.put("Contact_No", data.getContactNo());
            values.put("City", data.getCity());
            values.put("Store_Type", data.getStoreType());
            values.put("Store_Category", data.getStoreCategory());
            values.put("State_Id", data.getStateId());
            values.put("Store_Type_Id", data.getStoreTypeId());
            values.put("Store_Category_Id", data.getStoreCategoryId());
            values.put("Reason_Id", data.getReasonId());
            values.put("Upload_Status", data.getUploadStatus());
            values.put("Geo_Tag", data.getGeoTag());
            values.put("Distributor_Id", data.getDistributorId());
            long id = db.insert(CommonString.TABLE_Journey_Plan_DBSR_Saved, null, values);
            if (id == -1) {
                throw new Exception();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Exception in Journey_Plan_DBSR", ex.toString());
            return false;
        }
    }

    public boolean insertBrandMasterData(BrandMasterGetterSetter BrandMastergetset) {
        db.delete("Brand_Master", null, null);
        ContentValues values = new ContentValues();
        List<BrandMaster> data = BrandMastergetset.getBrandMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Brand", data.get(i).getBrand());
                values.put("Brand_Id", data.get(i).getBrandId());
                values.put("Brand_Sequence", data.get(i).getBrandSequence());
                values.put("Sub_Category_Id", data.get(i).getSubcategoryId());
                values.put("Company_Id", data.get(i).getCompany_Id());
                values.put("Category_Id", data.get(i).getCategoryId());

                long id = db.insert("Brand_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertNonWorkingData(NonWorkingReasonGetterSetter nonWorkingdata) {
        db.delete("Non_Working_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonWorkingReason> data = nonWorkingdata.getNonWorkingReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Reason_Id", data.get(i).getReasonId());
                values.put("Reason", data.get(i).getReason());
                values.put("Entry_Allow", data.get(i).getEntryAllow());
                values.put("Image_Allow", data.get(i).getImageAllow());
                values.put("GPS_Mandatory", data.get(i).getGPSMandatory());

                long id = db.insert("Non_Working_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertNonCategoryReasonData(NonCategoryReasonGetterSetter nonCategorydata) {
        db.delete("Non_Category_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonCategoryReason> data = nonCategorydata.getNonCategoryReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("CReason_Id", data.get(i).getCReasonId());
                values.put("CReason", data.get(i).getCReason());
                long id = db.insert("Non_Category_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertCategoryMasterData(CategoryMasterGetterSetter CategoryMaster) {
        db.delete("Category_Master", null, null);
        ContentValues values = new ContentValues();
        List<CategoryMaster> data = CategoryMaster.getCategoryMaster();
        try {
            if (data.size() == 0) {
                return false;
            }
            for (int i = 0; i < data.size(); i++) {
                values.put("Category", data.get(i).getCategory());
                values.put("Category_Id", data.get(i).getCategoryId());
                values.put("Category_Sequence", data.get(i).getCategorySequence());
                long id = db.insert("Category_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertMappingCategoryChecklistdata(MappingCategoryChecklistGetterSetter infotype) {
        db.delete("Mapping_Category_Checklist", null, null);
        ContentValues values = new ContentValues();
        List<MappingCategoryChecklist> data = infotype.getMappingCategoryChecklist();
        try {
            if (data.size() == 0) {
                return false;
            }
            for (int i = 0; i < data.size(); i++) {
                values.put("Category_Id", data.get(i).getCategoryId());
                values.put("Checklist_Id", data.get(i).getChecklistId());

                long id = db.insert("Mapping_Category_Checklist", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertWindowMasterkData(WindowMasterGetterSetter BrandMaster) {
        db.delete("Window_Master", null, null);
        ContentValues values = new ContentValues();
        List<WindowMaster> data = BrandMaster.getWindowMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Window_Id", data.get(i).getWindowId());
                values.put("Window", data.get(i).getWindow());

                long id = db.insert("Window_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertWindowChecklistData(WindowChecklistGetterSetter BrandMaster) {
        db.delete("Window_Checklist", null, null);
        ContentValues values = new ContentValues();
        List<WindowChecklist> data = BrandMaster.getWindowChecklist();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Checklist_Id", data.get(i).getChecklistId());
                values.put("Checklist", data.get(i).getChecklist());

                long id = db.insert("Window_Checklist", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertmappingInitiativedata(MappingInitiativeGetterSetter infotype) {
        db.delete("Mapping_Visibility_Initiatives", null, null);
        ContentValues values = new ContentValues();
        List<MappingVisibilityInitiative> data = infotype.getMappingVisibilityInitiatives();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("State_Id", data.get(i).getStateId());
                values.put("Distributor_Id", data.get(i).getDistributorId());
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());
                values.put("Brand_id", data.get(i).getBrandId());
                values.put("Window_Id", data.get(i).getWindowId());
                values.put("Classification_Id", data.get(i).getClassificationId());

                long id = db.insert("Mapping_Visibility_Initiatives", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertwindowChecklistAnsdata(WindowCheckAnswerGetterSetter infotype) {
        db.delete("Window_Checklist_Answer", null, null);
        ContentValues values = new ContentValues();
        List<WindowChecklistAnswer> data = infotype.getWindowChecklistAnswer();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Answer_Id", data.get(i).getAnswerId());
                values.put("Answer", data.get(i).getAnswer());
                values.put("Checklist_Id", data.get(i).getChecklistId());
                long id = db.insert("Window_Checklist_Answer", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertmappingwindowChecklistdata(MappingWindChecklistGetterSetter infotype) {
        db.delete("Mapping_Window_Checklist", null, null);
        ContentValues values = new ContentValues();
        List<MappingWindowChecklist> data = infotype.getMappingWindowChecklist();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Window_Id", data.get(i).getWindowId());
                values.put("Checklist_Id", data.get(i).getChecklistId());
                long id = db.insert("Mapping_Window_Checklist", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertMappingCategoryDressingData(MappingCategoryDressingGetterSetter infotype) {
        db.delete("Mapping_Category_Dressing", null, null);
        ContentValues values = new ContentValues();
        List<MappingCategoryDressing> data = infotype.getMappingCategoryDressing();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("State_Id", data.get(i).getStateId());
                values.put("Distributor_Id", data.get(i).getDistributorId());
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());
                values.put("Category_Id", data.get(i).getCategoryId());
                long id = db.insert("Mapping_Category_Dressing", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertNonWindowReasonData(NonWindowReasonGetterSetter infotype) {
        db.delete("Non_Window_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonWindowReason> data = infotype.getNonWindowReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("WReason_Id", data.get(i).getWReasonId());
                values.put("WReason", data.get(i).getWReason());
                long id = db.insert("Non_Window_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean isCoverageDataFilled(String visit_date) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM COVERAGE_DATA " + "where " + CommonString.KEY_VISIT_DATE + "<>'" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }

            }

        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());
            return filled;
        }

        return filled;
    }

    public void deletePreviousUploadedData(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from COVERAGE_DATA where VISIT_DATE < '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_COVERAGE_DATA, null, null);
                    db.delete(CommonString.TABLE_STORE_GEOTAGGING, null, null);
                    db.delete(CommonString.TABLE_WINDOWS_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_CHECKLIST_DATA, null, null);
                    db.delete(CommonString.TABLE_CATEGORY_DRESSING_DATA, null, null);
                    db.delete(CommonString.TABLE_INSERT_CATEGORY_DRESSING_CHECKLIST_DATA, null, null);
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }
    }

    public void deletePreviousJouneyPlanDBSRData(String visit_date) {
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan_DBSR_Saved where Visit_Date <> '" + visit_date + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getCount();
                dbcursor.close();
                if (icount > 0) {
                    db.delete(CommonString.TABLE_Journey_Plan_DBSR_Saved, null, null);
                }
                dbcursor.close();
            }

        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!", e.toString());
        }

    }

    public ArrayList<CoverageBean> getcoverageDataPrevious(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " +
                    CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "<>'" + visitdate + "'", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setCkeckout_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }

    public JourneyPlan getSpecificStoreDataPrevious(String date, String store_id) {
        JourneyPlan sb = new JourneyPlan();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * from Journey_Plan  " +
                    "where Visit_Date <> '" + date + "' AND Store_Id='" + store_id + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return sb;
        }

        return sb;
    }

    public ArrayList<CoverageBean> getSpecificCoverageData(String visitdate, String store_cd) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "' AND " +
                    CommonString.KEY_STORE_ID + "='" + store_cd + "'", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setCkeckout_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));

                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }

    public long updateCoverageCheckoutIMG(String storeid, String visit_date, String checkout_img) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CommonString.KEY_CHECKOUT_IMAGE, checkout_img);
            l = db.update("COVERAGE_DATA", values, " STORE_ID ='" + storeid + "' AND VISIT_DATE ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public long updateJaurneyPlanSpecificStoreStatus(String storeid, String visit_date, String status) {
        long l = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("Upload_Status", status);
            l = db.update("Journey_Plan", values, " Store_Id ='" + storeid + "' AND Visit_Date ='" + visit_date + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public ArrayList<JourneyPlan> getSpecificStoreData(String store_cd) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan  " + "where Store_Id ='" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public ArrayList<JourneyPlan> getSpecificStore_DBSRData(String store_cd) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan_DBSR  " + "where Store_Id ='" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public ArrayList<JourneyPlan> getSpecificStore_DBSRSavedData(String store_cd) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from Journey_Plan_DBSR_Saved  " + "where Store_Id ='" + store_cd + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }


    public ArrayList<NonWorkingReason> getNonWorkingEntryAllowData() {

        ArrayList<NonWorkingReason> list = new ArrayList<NonWorkingReason>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason WHERE Entry_Allow=1", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReason sb = new NonWorkingReason();

                    sb.setReasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")));
                    sb.setEntryAllow("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"))));
                    sb.setImageAllow("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Image_Allow"))));
                    sb.setGPSMandatory("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GPS_Mandatory"))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            return list;
        }
        return list;
    }

    public ArrayList<NonWorkingReason> getNonWorkingData() {
        ArrayList<NonWorkingReason> list = new ArrayList<NonWorkingReason>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReason sb = new NonWorkingReason();

                    sb.setReasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")));
                    sb.setEntryAllow("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"))));
                    sb.setImageAllow("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Image_Allow"))));
                    sb.setGPSMandatory("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GPS_Mandatory"))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }


    public ArrayList<GeotaggingBeans> getinsertGeotaggingData(String storeid, String status) {
        ArrayList<GeotaggingBeans> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_STORE_GEOTAGGING + "" +
                    " where " + CommonString.KEY_STORE_ID + " ='" + storeid + "' and " + CommonString.KEY_STATUS + " = '" + status + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    GeotaggingBeans geoTag = new GeotaggingBeans();
                    geoTag.setStoreid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    geoTag.setLatitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE))));
                    geoTag.setLongitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE))));
                    geoTag.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FRONT_IMAGE")));
                    list.add(geoTag);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception Brands",
                    e.toString());
            return list;
        }
        return list;

    }

    public void deleteSpecificStoreData(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.KEY_STORE_ID + "='" + storeid + "'", null);

    }

    public ArrayList<JourneyPlan> getStoreData(String date) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan  " + "WHERE Visit_Date ='" + date + "' ORDER BY Store_Name", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    sb.setLongitude(Double.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Longitude"))));
                    sb.setLatitude(Double.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Latitude"))));
                    sb.setGeoFencing(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GeoFencing"))));

                    if (sb.getStoreTypeId() == 1) {
                        sb.setColourCode(R.color.peachpuff);
                    } else if (sb.getStoreTypeId() == 2) {
                        sb.setColourCode(R.color.lightskyblue);
                    } else if (sb.getStoreTypeId() == 3) {
                        sb.setColourCode(R.color.gainsboro);
                    }

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }


        return list;
    }

    public ArrayList<JourneyPlan> getStoreData_DBSR(String date) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan_DBSR  " + "WHERE Visit_Date ='" + date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setWeeklyUpload(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Weekly_Upload")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    if (sb.getStoreTypeId() == 1) {
                        sb.setColourCode(R.color.peachpuff);
                    } else if (sb.getStoreTypeId() == 2) {
                        sb.setColourCode(R.color.lightskyblue);
                    } else if (sb.getStoreTypeId() == 3) {
                        sb.setColourCode(R.color.gainsboro);
                    }
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get Journey_Plan_DBSR!", e.toString());
            return list;
        }
        return list;
    }

    public ArrayList<JourneyPlan> getStoreData_DBSR_Saved(String date) {
        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * FROM Journey_Plan_DBSR_Saved  " + "WHERE Visit_Date ='" + date + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setVisitDate((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setAddress1(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address1")));
                    sb.setAddress2((dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address2"))));
                    sb.setLandmark(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Landmark")));
                    sb.setPincode(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Pincode")));
                    sb.setContactPerson(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_Person")));
                    sb.setContactNo(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Contact_No")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStateId(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setDistributorN(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    if (sb.getStoreTypeId() == 1) {
                        sb.setColourCode(R.color.peachpuff);
                    } else if (sb.getStoreTypeId() == 2) {
                        sb.setColourCode(R.color.lightskyblue);
                    } else if (sb.getStoreTypeId() == 3) {
                        sb.setColourCode(R.color.gainsboro);
                    }
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get Journey_Plan_DBSR!", e.toString());
            return list;
        }
        return list;
    }


    public ArrayList<CoverageBean> getCoverageData(String visitdate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_COVERAGE_DATA + " WHERE " + CommonString.KEY_VISIT_DATE + "='" + visitdate + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();
                    sb.setStoreId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_STORE_ID)));
                    sb.setUserId(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_USER_ID)));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    sb.setLatitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE)));
                    sb.setLongitude(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)));
                    sb.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE)));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    sb.setReasonid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));
                    sb.setMID(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_ID))));
                    sb.setCkeckout_image(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CHECKOUT_IMAGE)));
                    sb.setRemark(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Coverage Data!!!!!!!!!!!!!!!!!!!!!",
                    e.toString());

        }

        return list;

    }


    public boolean isWindowDataFilled(int storeId, int window_cd) {
        boolean filled = false;
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM " + CommonString.TABLE_WINDOWS_DATA + " WHERE STORE_ID= '" + storeId + "' AND WINDOW_CD ='" + window_cd + "'", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Log.d("Exception when fetching Records!!!!!!!!!!!!!!!!!!!!!", e.toString());
            return filled;
        }
        return filled;
    }


}
