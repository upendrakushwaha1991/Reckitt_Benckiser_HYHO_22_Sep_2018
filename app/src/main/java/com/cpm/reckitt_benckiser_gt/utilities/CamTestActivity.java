package com.cpm.reckitt_benckiser_gt.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cpm.reckitt_benckiser_gt.R;
import com.cpm.reckitt_benckiser_gt.database.RBGTDatabase;
import com.cpm.reckitt_benckiser_gt.getterSetter.JourneyPlan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CamTestActivity extends AppCompatActivity implements View.OnClickListener {
    String _UserId, visit_date, visit_date_formatted, store_id;
    JourneyPlan jcpGetset;
    private static final String TAG = "CamTestActivity";
    Preview preview;
    Camera camera;
    Context context;
    RelativeLayout cam_layout;
    LinearLayout okcancellayout;
    Button btnok, btncam, btncancel;
    byte[] datacam;
    RBGTDatabase db;
    private SharedPreferences preferences;
    boolean backwall_click_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cam_test);

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, null);
        store_id = String.valueOf(jcpGetset.getStoreId());
        db = new RBGTDatabase(context);
        db.open();

        preview = new Preview(getApplicationContext(), (SurfaceView) findViewById(R.id.surfaceView));
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        cam_layout = (RelativeLayout) findViewById(R.id.cambtn);
        okcancellayout = (LinearLayout) findViewById(R.id.ok_cancel);
        btncam = (Button) findViewById(R.id.btncamclick);
        btnok = (Button) findViewById(R.id.btnok);
        btncancel = (Button) findViewById(R.id.btncancel);

        btncam.setOnClickListener(this);
        btnok.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        preview.setKeepScreenOn(true);

    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open(0);
                //camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException ex) {
                AlertandMessages.showToastMsg(context, getString(R.string.camera_not_found));
            }
        }
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            //camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        //camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            cam_layout.setVisibility(View.GONE);
            okcancellayout.setVisibility(View.VISIBLE);
            datacam = data;
            //resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], String, String> {

        String dummyFileName;

        @Override
        protected String doInBackground(byte[]... data) {
            FileOutputStream outStream = null;
            File outFile = null;
            // Write to SD Card
            try {
                File dir = new File(CommonString.FOLDER_NAME_WITH_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                dummyFileName = fileName;
                outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return outFile.getAbsolutePath();
        }

        @Override
        protected void onPostExecute(String path) {
            // TODO Auto-generated method stub
            super.onPostExecute(path);
            // Load image into(as) bitmap
            //Bitmap b = BitmapFactory.decodeFile(path);
            Bitmap b = convertBitmap(path);
            // Rectangle Objects
            Paint paint = new Paint();
            // Create Temp bitmap
            Bitmap tBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.RGB_565);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            float screenWidth = metrics.widthPixels;
            float screenHeight = (int) (metrics.heightPixels);
            //  Set paint options
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(255, 255, 125, 000));
            // Create a new canvas and add Bitmap into it
            Canvas canvas = new Canvas(tBitmap);
            //Draw the image bitmap into the canvas
            canvas.drawBitmap(b, 0, 0, null);

            int width = tBitmap.getWidth();
            int height = tBitmap.getHeight();

            canvas.drawLine((width / 11) * 3, 0, (width / 11) * 3, height, paint);
            canvas.drawLine((width / 11) * 8, 0, (width / 11) * 8, height, paint);

            System.out.println("small==" + (screenWidth / 100) * 27 + "  large===" + (screenWidth / 100) * 63);

            canvas.drawLine(0, (height / 11) * 3, width, (height / 11) * 3, paint);
            canvas.drawLine(0, (height / 11) * 8, width, (height / 11) * 8, paint);

            BitmapDrawable btmpDr = new BitmapDrawable(getResources(), tBitmap);
            Bitmap bmp = btmpDr.getBitmap();

            try {
                FileOutputStream outStream;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(CommonString.FOLDER_NAME_WITH_PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = store_id + "_" + _UserId.replace(".", "") + "_WINDOW_withGrid-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                // String fileName = String.format(store_id + _UserId.replace(".", "_") + "SECBACKWALLCANVAS" + visit_date.replace("/", "") + "%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                outStream = new FileOutputStream(outFile);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                /* 100 to keep full quality of the image */
                outStream.flush();
                outStream.close();
                //Refreshing SD card
                refreshGallery(outFile);
                //upendra
                Toast.makeText(getApplicationContext(), "file saved", Toast.LENGTH_LONG).show();
                new File(path).delete();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",fileName);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public static Bitmap convertBitmap(String path) {
        Bitmap bitmap = null;
        BitmapFactory.Options ourOptions = new BitmapFactory.Options();
        ourOptions.inDither = false;
        ourOptions.inPurgeable = true;
        ourOptions.inInputShareable = true;
        ourOptions.inTempStorage = new byte[32 * 1024];
        File file = new File(path);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, ourOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.btncamclick:
                if (!backwall_click_flag) {
                    backwall_click_flag = true;
                    camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                }
                break;
            case R.id.btncancel:
                backwall_click_flag = false;
                resetCam();
                cam_layout.setVisibility(View.VISIBLE);
                okcancellayout.setVisibility(View.GONE);
                break;
            case R.id.btnok:
                new SaveImageTask().execute(datacam);
                break;
        }

    }
}

