package com.example.Sensor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.fxb.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText name;
	private EditText period;
	private EditText number;
	private Button start;
	private Button stop;
	private Button save;
	private FileHelper helper;
	private WifiManager mwifimanager;
	private WifiInfo mWifiInfo;
	private StringBuilder sb;
	private SensorManager sensorManager = null;
	private Sensor sensor_GYRO = null;
	private Sensor sensor_ACCELEROMETER = null;
	private Sensor sensor_MAGN = null;
	private Sensor sensor_ORIENTATION = null;
	private Sensor sensor_PRESSURE = null;
	private SensorEvent e_gyro;
	private SensorEvent e_acc;
	private SensorEvent e_magn;
	private SensorEvent e_orien;
	private SensorEvent e_press;
	private MySensorEventListener sensorEventListener;

	/************************* determine whether WIFI is open ****************************************************************/
	public void Wifiadmin(Context context) {
		mwifimanager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mwifimanager.getConnectionInfo();
		if (!mwifimanager.isWifiEnabled()) {
			new AlertDialog.Builder(this).setTitle("hint")
					.setMessage("your wifi isn't open,please open it!")
					.setPositiveButton("OK", listener2)
					.setNegativeButton("Cancel", listener2).show();
		}
	}

	/************************* determine whether WIFI is open***************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		name = (EditText) findViewById(R.id.name);
		period = (EditText) findViewById(R.id.period);
		number = (EditText) findViewById(R.id.number);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		save = (Button) findViewById(R.id.save);
		helper = new FileHelper(getApplicationContext());
		this.Wifiadmin(getBaseContext());
		sb = new StringBuilder();
		sensorEventListener = new MySensorEventListener();

		start.setOnClickListener(start_listener);
		stop.setOnClickListener(stop_listener);
		save.setOnClickListener(save_listener);

		mwifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor_GYRO = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);// GYRO
		sensor_ACCELEROMETER = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// ACCELEROMETER
		sensor_MAGN = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);// MAGN
		sensor_ORIENTATION = sensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);// ORIENTATION
		sensor_PRESSURE=sensorManager
				.getDefaultSensor(Sensor.TYPE_PRESSURE);//PRESSURE

	}

	@Override
	protected void onStop() {
		super.onStop();
		sensorManager.unregisterListener(sensorEventListener);  
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler to realize timer
			// Timer operations are performed every two seconds
			try {
				handler.postDelayed(this,
						Integer.parseInt(period.getText().toString()));
				sb.append(number.getText().toString()).append("\r\n").append("time: ").append(getNowTime()).append("\r\n");
				sb.append("gyro: ").append(e_gyro.values[0]).append(" ").append(e_gyro.values[1]).append(" ").append(e_gyro.values[2]).append("\r\n");
				sb.append("magn: ").append(e_magn.values[0]).append(" ").append(e_magn.values[1]).append(" ").append(e_magn.values[2]).append("\r\n");
				sb.append("orien: ").append(e_orien.values[0]).append(" ").append(e_orien.values[1]).append(" ").append(e_orien.values[2]).append("\r\n");
				sb.append("acc: ").append(e_acc.values[0]).append(" ").append(e_acc.values[1]).append(" ").append(e_acc.values[2]).append("\r\n");
                sb.append("press: ").append(e_press.values[0]).append("\r\n");
			} catch (Exception e) {
				e.printStackTrace();// 12345678
			}
		}
	};
	/**
	 * get current time
	 * @return
	 */
	public static String getNowTime(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		Date date = new Date(System.currentTimeMillis());
		return simpleDateFormat.format(date);
	}
	private View.OnClickListener start_listener = new OnClickListener() {
		public void onClick(View v) {
			start.setEnabled(false);
			stop.setEnabled(true);
			mwifimanager.startScan();
			sensorManager.registerListener(sensorEventListener, sensor_GYRO, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(sensorEventListener, sensor_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(sensorEventListener, sensor_ORIENTATION, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(sensorEventListener, sensor_MAGN, SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(sensorEventListener,sensor_PRESSURE,SensorManager.SENSOR_DELAY_GAME);
			handler.postDelayed(runnable,
					Integer.parseInt(period.getText().toString()));
		}
	};
	private View.OnClickListener stop_listener = new OnClickListener() {
		public void onClick(View v) {
			sensorManager.unregisterListener(sensorEventListener);
			start.setEnabled(true);
			stop.setEnabled(false);
			handler.removeCallbacks(runnable);
		}
	};
	private final class MySensorEventListener implements SensorEventListener    
    {    
        //get the sensors' real-time measurement values
        @Override    
        public void onSensorChanged(SensorEvent event)     
        {       
            //get the orientation sensor's measurement values
            if(event.sensor.getType()==Sensor.TYPE_ORIENTATION)    
            {    
            	e_orien = event;
            }    
            //get the accelerometer sensor's measurement values
            else if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)    
            {    
            	e_acc = event;
            }
			//get the gyroscope sensor's measurement values
            else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
            {    
            	e_gyro = event;
            }
			//get the magnetic sensor's measurement values
            else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
            {    
            	e_magn = event;
            }
            //get the pressure sensor's measurement values
			else if(event.sensor.getType()==Sensor.TYPE_PRESSURE) {
				e_press = event;
			}
        }    
        //rewrite the change
        @Override    
        public void onAccuracyChanged(Sensor sensor, int accuracy)     
        {    
        }    
    } 
	private View.OnClickListener save_listener = new OnClickListener() {
		public void onClick(View v) {
			String NAME = name.getText().toString() + ".txt";
			if (helper.hasSD()) {
				try {
					helper.createSDFile(NAME).getAbsolutePath();
					helper.writeSDFile(sb.toString(), NAME);
					sb = new StringBuilder();
					Toast.makeText(MainActivity.this, "the saved", Toast.LENGTH_LONG)
							.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				Toast.makeText(MainActivity.this, "doesn't find SD card", Toast.LENGTH_LONG)
						.show();
		}
	};
	DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ok"open wifi
			{
				mwifimanager.setWifiEnabled(true);
				dialog.cancel();
				break;
			}
			case AlertDialog.BUTTON_NEGATIVE:// "Cancel"
				dialog.cancel();
				break;
			default:
				break;
			}
		}
	};
	
}
