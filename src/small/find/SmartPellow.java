/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package small.find;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;

import small.find.R;

import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SmartPellow extends Activity {
  /** The main dataset that includes all the series that go into a chart. */
  private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
  /** The main renderer that includes all the renderers customizing a chart. */
  private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
  /** The most recently added series. */
  private XYSeries mTemperature;
  private XYSeries mSound;
  private XYSeries mPressure;
  /** The most recently created renderer, customizing the current series. */
  private XYSeriesRenderer mCurrentRenderer;
  private XYSeriesRenderer mCurrentRenderer2;
  private XYSeriesRenderer mCurrentRenderer3;
  /** Button for creating a new series of data. */
  private Button mNewSeries;
  /** Button for adding entered data to the current series. */
  /** The chart view that displays the data. */
  private GraphicalView mChartView;
  //声音次数
  int numdata=0;
  //时间
  int time=0;
  private Button notice;
  private Button naozhong;
  boolean showed=false;
  private BluetoothAdapter btAdapter;
	private BluetoothDevice btDevice;
	private BluetoothSocket btSocket;
	static OutputStream outputStream;
	static InputStream inputStream;
	private BroadcastReceiver mReceiver;
	
	private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//			("00001101-0000-1000-8000-98D331B3A53B");
	//要配对的地址
	String address="98:D3:31:B3:A5:3B";
	private BroadcastReceiver btConnectReceiver;
	private BroadcastReceiver btDisconnectReceiver;
	private IntentFilter connectIntentFilter;
	private IntentFilter disconnectIntentFilter;
	private boolean isConnect = false;
	//只配对一个
	List<BluetoothDevice>devices;
	private static final String TAG = "ProcessInfo";
  
	final String HAVEHULU="您打呼噜哦";
	final String HAVEHULUHEAVY="您打呼噜比较严重哦";
	final String FANGUN="您睡觉容易翻身哦，睡眠深度不足";
	final String DIWEN="温度较低，请注意头部保暖哦";
	//呼噜&&翻身
	final String SLEEPSTATE1="较差";
	//呼噜||翻身
	final String SLEEPSTATE2="一般";
	//none
	final String SLEEPSTATE3="良好";
  
	String showNotice;
	boolean hulu=false;
	boolean huluheavy=false;
	boolean fanshen=false;
	double temperaver=0;
	double yalicha=0;
	boolean first=false;
	double preyali=0;
	
	
  int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN };
  String[] titles = new String[] { "温度", "压力", "声音" };
  PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,  
	        PointStyle.TRIANGLE };
  
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // save the current data, for instance when changing screen orientation
    outState.putSerializable("dataset", mDataset);
    outState.putSerializable("renderer", mRenderer);
    outState.putSerializable("current_series", mTemperature);
    outState.putSerializable("current_series2", mSound);
    outState.putSerializable("current_series3", mPressure);
    outState.putSerializable("current_renderer", mCurrentRenderer);
    outState.putSerializable("current_renderer2", mCurrentRenderer2);
    outState.putSerializable("current_renderer3", mCurrentRenderer3);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    // restore the current data, for instance when changing the screen
    // orientation
    mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
    mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
    mTemperature = (XYSeries) savedState.getSerializable("current_series");
    mSound = (XYSeries) savedState.getSerializable("current_series2");
    mPressure = (XYSeries) savedState.getSerializable("current_series3");
    mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
    mCurrentRenderer2 = (XYSeriesRenderer) savedState.getSerializable("current_renderer2");
    mCurrentRenderer3 = (XYSeriesRenderer) savedState.getSerializable("current_renderer3");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.xy_chart);

    
    // set some properties on the main renderer
    mRenderer.setApplyBackgroundColor(true);
    mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
    mRenderer.setAxisTitleTextSize(16);
    mRenderer.setChartTitleTextSize(20);
    mRenderer.setLabelsTextSize(15);
    mRenderer.setLegendTextSize(15);
    mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
    mRenderer.setZoomButtonsVisible(true);
    mRenderer.setPointSize(5);

    mRenderer.setXAxisMax(30);
    mRenderer.setXAxisMin(0);
    mRenderer.setYAxisMin(0); 
    mRenderer.setYAxisMax(35);
    mRenderer.setPanLimits(new double[]{0,30,0,35});
    
    // the button that handles the new series of data creation
    mNewSeries = (Button) findViewById(R.id.new_series);
    mNewSeries.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
//        String seriesTitle = "Series " + (mDataset.getSeriesCount() + 1);
//        // create a new series of data
//        XYSeries series = new XYSeries(seriesTitle);
//        mDataset.addSeries(series);
//        mCurrentSeries = series;
    	    //添加三组数据
    	  XYSeries temperature=new XYSeries("温度");
  	    mDataset.addSeries(temperature);
    // create a new renderer for the new series
    XYSeriesRenderer renderer = new XYSeriesRenderer();
    mRenderer.addSeriesRenderer(renderer);
    // set some renderer properties
    renderer.setPointStyle(PointStyle.CIRCLE);
    renderer.setFillPoints(true);
    renderer.setDisplayChartValues(true);
    renderer.setDisplayChartValuesDistance(5);
    mTemperature = temperature;
    mCurrentRenderer = renderer;
//    setSeriesWidgetsEnabled(true);
  //  mChartView.repaint();
    
    XYSeries sound=new XYSeries("声音");
    mDataset.addSeries(sound);
    XYSeriesRenderer renderer2 = new XYSeriesRenderer();
    mRenderer.addSeriesRenderer(renderer2);
    // set some renderer properties
    renderer2.setPointStyle(PointStyle.DIAMOND);
    renderer2.setFillPoints(true);
    renderer2.setDisplayChartValues(true);
    renderer2.setDisplayChartValuesDistance(5);
    mSound=sound;
    mCurrentRenderer2 = renderer2;
//    setSeriesWidgetsEnabled(true);
  //  mChartView.repaint();
    
    
    XYSeries pressure = new XYSeries("压力");
    pressure = new XYSeries("压力");
    mDataset.addSeries(pressure);
    XYSeriesRenderer renderer3 = new XYSeriesRenderer();
    mRenderer.addSeriesRenderer(renderer3);
    // set some renderer properties
    renderer3.setPointStyle(PointStyle.TRIANGLE);
    renderer3.setFillPoints(true);
    renderer3.setDisplayChartValues(true);
    renderer3.setDisplayChartValuesDistance(5);
    mPressure=pressure;
    mCurrentRenderer3 = renderer3;
//    setSeriesWidgetsEnabled(true);
    
    
    pressure.add(0, 0);
//    pressure.add(20, 30);
    temperature.add(0, 0);
//    temperature.add(5, 20);
    sound.add(0, 0);
//    sound.add(5, 30);
    showed=true;
    mChartView.repaint();
      }
    });
    notice=(Button)findViewById(R.id.notice);
    notice.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			judge();
			new AlertDialog.Builder(SmartPellow.this).setTitle("关于").setMessage(showNotice).setPositiveButton("知道啦", null).show();
			
		}
	});
    naozhong=(Button)findViewById(R.id.naozhongbtn);
    naozhong.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			LayoutInflater inflater = getLayoutInflater();
			   View layout = inflater.inflate(R.layout.naozhong,
			     (ViewGroup) findViewById(R.id.dialog));

			   new AlertDialog.Builder(SmartPellow.this).setTitle("闹钟").setView(layout)
			     .setPositiveButton("确定", null)
			     .setNegativeButton("取消", null).show();
		}
	});
    
    //蓝牙连接部分
    
    btAdapter=BluetoothAdapter.getDefaultAdapter();
	
  		devices=new ArrayList<BluetoothDevice>(btAdapter.getBondedDevices());
  		mReceiver=new BroadcastReceiver() {
  			
  			@Override
  			public void onReceive(Context context, Intent intent) {
  				String action=intent.getAction();
  				if(BluetoothDevice.ACTION_FOUND.equals(action)){
  					BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
  					Toast.makeText(SmartPellow.this, device.getName(), Toast.LENGTH_SHORT).show();
  					Log.i(TAG, device.getName()+device.getAddress());
  					if(device.getName().equals("HC-06"))
  						connect();
//  						new Connect().start();
  				}
  			}
  		};
  		IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
  		registerReceiver(mReceiver, intentFilter);
  		
  		btConnectReceiver=new BroadcastReceiver() {
  			
  			@Override
  			public void onReceive(Context context, Intent intent) {
  				Log.e(TAG, "--connected");
  				isConnect=true;
  				
  				
  				
  			}
  		};
  		connectIntentFilter=new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
  		registerReceiver(btConnectReceiver, connectIntentFilter);
  		
  		btDisconnectReceiver = new BroadcastReceiver() {
  			@Override
  			public void onReceive(Context arg0, Intent arg1) {
  				// TODO Auto-generated method stub
  				Log.e(TAG, "-- disconnected");
  				isConnect = false;
  				new TryToConnet().start();
  			}
  		};
  		
  		disconnectIntentFilter = new IntentFilter(
  				BluetoothDevice.ACTION_ACL_DISCONNECTED);
  		registerReceiver(btDisconnectReceiver, disconnectIntentFilter);
  		
  		 btAdapter.cancelDiscovery();
			btDevice=btAdapter.getRemoteDevice(address);
			if(devices.size()>0){
				Toast.makeText(SmartPellow.this, "正在连接",Toast.LENGTH_SHORT).show();
//				new Connect().start();
				connect();
//				
			}else{
				Toast.makeText(SmartPellow.this, "正在搜索", Toast.LENGTH_SHORT).show();
				btAdapter.startDiscovery();
				
			}
		    

    
    
    
    
    
    
    
    
  }
  void judge(){
	  showNotice="";
	  if(temperaver<40)showNotice+=DIWEN+";";
	  if(huluheavy)showNotice+=HAVEHULUHEAVY+";";
	  else if(hulu)showNotice+=HAVEHULU+";";
	  if(yalicha>2)showNotice+=FANGUN+";";
	  if(hulu&&fanshen)showNotice+=SLEEPSTATE1;
	  else{
		  if(hulu||fanshen)showNotice+=SLEEPSTATE2;
		  else showNotice+=SLEEPSTATE3;
	  }
  }
  void adddata(double t,double v,double s){
	  if(!first){
		  first=true;
	  }else{
		  yalicha=Math.abs(v-preyali);
	  }
	  preyali=v;
	  mSound.add(time, s);
	  if(s>20)huluheavy=true;
	  if(s>2)hulu=true;
	  mPressure.add(time, v);
	  
	  
	  mTemperature.add(time, t);
	  temperaver=Math.max(temperaver, t);
	  time++;
	  mChartView.repaint();
  }
  void show(){
	  XYSeries temperature=new XYSeries("温度");
	    mDataset.addSeries(temperature);
  // create a new renderer for the new series
  XYSeriesRenderer renderer = new XYSeriesRenderer();
  mRenderer.addSeriesRenderer(renderer);
  // set some renderer properties
  renderer.setPointStyle(PointStyle.CIRCLE);
  renderer.setFillPoints(true);
  renderer.setDisplayChartValues(true);
  renderer.setDisplayChartValuesDistance(5);
  mTemperature = temperature;
  mCurrentRenderer = renderer;
//  setSeriesWidgetsEnabled(true);
//  mChartView.repaint();
  
  XYSeries sound=new XYSeries("声音");
  mDataset.addSeries(sound);
  XYSeriesRenderer renderer2 = new XYSeriesRenderer();
  mRenderer.addSeriesRenderer(renderer2);
  // set some renderer properties
  renderer2.setPointStyle(PointStyle.DIAMOND);
  renderer2.setFillPoints(true);
  renderer2.setDisplayChartValues(true);
  renderer2.setDisplayChartValuesDistance(5);
  mCurrentRenderer2 = renderer2;
//  setSeriesWidgetsEnabled(true);
//  mChartView.repaint();
  
  
  XYSeries pressure = new XYSeries("压力");
  pressure = new XYSeries("压力");
  mDataset.addSeries(pressure);
  XYSeriesRenderer renderer3 = new XYSeriesRenderer();
  mRenderer.addSeriesRenderer(renderer3);
  // set some renderer properties
  renderer3.setPointStyle(PointStyle.TRIANGLE);
  renderer3.setFillPoints(true);
  renderer3.setDisplayChartValues(true);
  renderer3.setDisplayChartValuesDistance(5);
  mCurrentRenderer3 = renderer3;
//  setSeriesWidgetsEnabled(true);
  
  
  pressure.add(0, 10);
  pressure.add(20, 30);
  temperature.add(0, 20);
  temperature.add(5, 20);
  sound.add(2, 20);
  sound.add(5, 30);
  mChartView.repaint();
  
  
  }
  void connect(){
	  btAdapter.cancelDiscovery();
		btDevice=btAdapter.getRemoteDevice(address);
		try {
			btSocket=btDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			btSocket.connect();
			outputStream =btSocket.getOutputStream();
			inputStream=btSocket.getInputStream();
//			show();
			Toast.makeText(SmartPellow.this, "连接成功", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(SmartPellow.this, "连接失败", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		new Thread(new Connect()).start();
  }
  
  public void showData(String aa){
	  int sindex=aa.indexOf(':');
	  String v=aa.substring(sindex+1, sindex+6);
	  sindex+=7;
	  String num=aa.substring(sindex,sindex+5);
	  sindex+=5;
	  String t=aa.substring(sindex,sindex+3);
	  if(showed){
		  adddata(Double.parseDouble(t), Double.parseDouble(v), Integer.parseInt(num)/10);
		  numdata=Integer.parseInt(num);
	  }
  }
  
  private class Connect implements Runnable{
	  
	public void run() {
		byte []buffer=new byte[25];
		String ans="";
		byte [][]str=new byte[25][1];
		while(true){
			try {
				for(int i=0;i<25;i++){
					inputStream.read(str[i]);
					buffer[i]=str[i][0];
				}
				ans+=new String(buffer);
				ans=replaceEnter(ans);
				if(ans.length()>=100){
					System.out.println(ans+"****");
					showData(ans);
					ans="";
				}
//				System.out.println(new String(buffer));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  
//		
	    
	    
	}
	String replaceEnter(String str){
		 String dest = "";  
	        if (str != null) {  
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
	            Matcher m = p.matcher(str);  
	            dest = m.replaceAll("");  
	        }  
	        return dest; 
	}
  }
  private class TryToConnet extends Thread {
		public void run() {
			/* 此处必须重新创建一个socket，否则重新连接后无法传输数据，个人猜想是Rfcomm通道已经改变 */
			try {
				btSocket = btDevice.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "-- failed to create btSocket");
			}
			while (true) {
				try {
					btSocket.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (isConnect) {
					Log.e(TAG, "-- Connect again,ending the TryToConnet thread");
					break;
				}
			}
			try {
				outputStream = btSocket.getOutputStream();
				inputStream=btSocket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
  
  
  
 
  
  
  
  
  
  @Override
  protected void onResume() {
    super.onResume();
    if (mChartView == null) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
      mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
      // enable the chart click events
      mRenderer.setClickEnabled(true);
      mRenderer.setSelectableBuffer(10);
      mChartView.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          // handle the click event on the chart
          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
          if (seriesSelection == null) {
            Toast.makeText(SmartPellow.this, "No chart element", Toast.LENGTH_SHORT).show();
          } else {
            // display information of the clicked point
            Toast.makeText(
                SmartPellow.this,
                "Chart element in series index " + seriesSelection.getSeriesIndex()
                    + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                    + " closest point value X=" + seriesSelection.getXValue() + ", Y="
                    + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
          }
        }
      });
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
//      boolean enabled = mDataset.getSeriesCount() > 0;
//      setSeriesWidgetsEnabled(enabled);
    } else {
      mChartView.repaint();
    }
  }

  /**
   * Enable or disable the add data to series widgets
   * 
   * @param enabled the enabled state
   */
//  private void setSeriesWidgetsEnabled(boolean enabled) {
//    mX.setEnabled(enabled);
//    mY.setEnabled(enabled);
//    mAdd.setEnabled(enabled);
//  }
}