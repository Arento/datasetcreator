package com.example.android.camera2basic;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements View.OnClickListener{

    Range<Integer> isoRange;
    Range<Integer> evRange;
    Range<Long> etRange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.sendsettings).setOnClickListener(this);
        Intent intent = getIntent();
        String cameraId = intent.getStringExtra(Constants.CAMERA_ID);
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
        CameraCharacteristics mCharacteristics = null;
        try {
            mCharacteristics = manager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if(mCharacteristics != null){
            etRange = mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
            isoRange = mCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
            evRange =  mCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
            if(etRange == null)
                etRange = Range.create((long)0,(long)0);
            if(isoRange == null)
                isoRange = Range.create(0,0);
            if(evRange == null)
                evRange = Range.create(0,0);
            String info = "Camera parameters: \n";
            info = info + "ISO range: " + isoRange.toString() + "\n";
            info = info + "Exposure compensation value range: " + evRange.toString() +"\n";
            info = info + "Exposure time range: " + etRange.toString() + "\n";
            TextView tw = (TextView) findViewById(R.id.camerainfo);
            tw.setText(info.toCharArray(),0,info.length());
        }
    }
    private Intent getParameters(){
        return null;
    }
    private boolean checkStringsInteger(String lower, String upper, String step, Range<Integer> range) {
        boolean success = true;
        success = success && !lower.isEmpty() && range.contains(Integer.parseInt(lower));
        success = success && !upper.isEmpty() && range.contains(Integer.parseInt(upper));
        success = success && !step.isEmpty() && Integer.parseInt(step) != 0;
        success = success && Integer.parseInt(lower) <= Integer.parseInt(upper);
        return success;
    }
    private boolean checkStringsLong(String lower, String upper, String step, Range<Long> range){
        boolean success = true;
        success = success && !lower.isEmpty() && range.contains(Long.parseLong(lower));
        success = success && !upper.isEmpty() && range.contains(Long.parseLong(upper));
        success = success && !step.isEmpty() && Long.parseLong(step) != 0;
        success = success && Integer.parseInt(lower) <= Long.parseLong(upper);
        return success;
    }
    public void onClick(View view) {
        if(view.getId() == R.id.sendsettings) {
            String isoLower = ((EditText) findViewById(R.id.isolower)).getText().toString();
            String isoStep = ((EditText) findViewById(R.id.isostep)).getText().toString();
            String isoUpper = ((EditText) findViewById(R.id.isoupper)).getText().toString();
            String evLower = ((EditText) findViewById(R.id.eclower)).getText().toString();
            String evStep = ((EditText) findViewById(R.id.ecstep)).getText().toString();
            String evUpper = ((EditText) findViewById(R.id.ecupper)).getText().toString();
            String etLower = ((EditText) findViewById(R.id.etlower)).getText().toString();
            String etStep = ((EditText) findViewById(R.id.etstep)).getText().toString();
            String etUpper = ((EditText) findViewById(R.id.etupper)).getText().toString();
            String datasetName = ((EditText) findViewById(R.id.datasetname)).getText().toString();
            boolean awb = ((CheckBox) findViewById(R.id.awbcheck)).isChecked();
            Intent data = new Intent();
            if(checkStringsInteger(isoLower,isoUpper,isoStep,isoRange) &&
               checkStringsLong(etLower,etUpper,etStep,etRange) &&
               checkStringsInteger(evLower,evUpper,evStep,evRange)) {
                data.putExtra(Constants.LOWER_ISO,Integer.parseInt(isoLower));
                data.putExtra(Constants.UPPER_ISO,Integer.parseInt(isoUpper));
                data.putExtra(Constants.STEP_ISO,Integer.parseInt(isoStep));
                data.putExtra(Constants.LOWER_EV,(int)Integer.parseInt(evLower));
                int debug = Integer.parseInt(evLower);
                data.putExtra(Constants.UPPER_EV,Integer.parseInt(evUpper));
                data.putExtra(Constants.STEP_EV,Integer.parseInt(evStep));
                data.putExtra(Constants.EXPOSURE_TIME_LOWER,Long.parseLong(etLower));
                data.putExtra(Constants.EXPOSURE_TIME_UPPER,Long.parseLong(etUpper));
                data.putExtra(Constants.EXPOSURE_TIME_STEP,Long.parseLong(etStep));
                data.putExtra(Constants.DATASET_NAME,datasetName);
                data.putExtra(Constants.NO_AWB,awb);
                setResult(Activity.RESULT_OK,data);
                finish();
            } else {
                final Activity activity = this;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity,"Settings are not correct or not full",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }
    }
}
