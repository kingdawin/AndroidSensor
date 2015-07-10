/*
 *@author Dawin,2015-1-10
 *
 *
 *
 */
package com.hy2014.phonesafer.activity;
import java.util.ArrayList;
import java.util.List;

import com.hy2014.phonesafer.R;
import com.hy2014.phonesafer.utils.LogUtil;
import com.hy2014.phonesafer.utils.ToastUtil;
import com.hy2014.phonesafer.view.GaugeChart01View;

import android.content.Context;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 设置重力感应模式的安全角度,角度指加速度传感器x,y与水平面夹角 * 
 * @author Dawin
 * 
 */
public class SettingXYAngleActivity extends BaseActivity
{
	
	final static boolean IS_X=true;
	//改变抖动范围
	public SeekBar degreeValueBar;	
	//public TextView mSeekArcProgress;
     //抖动的安全角度
	private TextView tvDegree;
	//当前角度
	private TextView tvXDegree;
	private TextView tvYDegree;
	//设置的x,y角度
	private TextView tvXYDegree;
	private SensorManager sensorManager;
	private Sensor accelrometerSensor;
	/**
	 * 刻度盘X
	 */
	private GaugeChart01View gaugeChart01ViewX;
	
	/**
	 * 刻度盘Y
	 */
	private GaugeChart01View gaugeChart01ViewY;	
	//分区边界
	private List<Pair> mPartitionSet;		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_speedvalue_degree);

		init();
		sensorManager();
		sp = getSharedPreferences(FILE_NAME, 1);
		editor = sp.edit();
		// 抖动范围    获取设置的阀值
		speedValue = sp.getInt("speedValue", 20/* 默认值，灵敏度最小 */);
		shakeAngle=sp.getInt("degree", 20/* 默认值，灵敏度最小 */);
		tvDegree.setText(shakeAngle+"°");
		settedX=sp.getInt("degreeX", 0);
		settedY=sp.getInt("degreeY", 0);
		
		tvXYDegree.setText("当前设置的角度x="+settedX+"°  y="+settedY+"°");
		drawPartition(settedX,settedY);
		
		/*if (isHorizontal)
			{
				horizontalRadioButton.setChecked(true);
			} else
			{
				verticalRadioButton.setChecked(true);
			}*/
		setDegreeBar();
		//setOnCheckedChangeListener();
	}
    
	/**
	 * 更新表盘安全和警报区
	 * 
	 * @param x
	 *            x与水平面夹角
	 * @param y
	 *            y与水平面夹角
	 */
	public void drawPartition(int x, int y)
	{
		gaugeChart01ViewX.drawPartition(chartDataSet(x,IS_X));
		gaugeChart01ViewY.drawPartition(chartDataSet(y,!IS_X));	
	}
	/*public void setOnCheckedChangeListener()
	{
		vhRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch (group.getCheckedRadioButtonId())
				{
				case R.id.vertical_rd_btn:
					LogUtil.e("垂直");
					isHorizontal=false;
					break;
				case R.id.horizontal_rd_Btn:
					LogUtil.e("水平");
					isHorizontal=true;
					break;
				}
			}
		});
	}
*/
	
	/**
	 * 表盘分区角度[角度(0-mStartAngle)，颜色]
	 * @param degree0 设置的x夹角
	 * @return
	 */
	private List<Pair> chartDataSet(int degree0,boolean isX)
	{
		mPartitionSet = new ArrayList<Pair>();	
		
		int safeScodeHigh;
		int safeScodeLow;
		
		safeScodeHigh = degree0 + shakeAngle > 180 ? (degree0 + shakeAngle - 180) : (degree0 + shakeAngle);
		safeScodeLow = degree0 - shakeAngle < 0 ? 180 + degree0 - shakeAngle : degree0 - shakeAngle;
		
		if(isX){
			safeScodeHighX=safeScodeHigh;
			safeScodeLowX=safeScodeLow;
		}else {
			safeScodeHighY=safeScodeHigh;
			safeScodeLowY=safeScodeLow;
		}
		// <0 180+?
		// >180 ?-180
		if (safeScodeHigh > safeScodeLow)
			{
				// 红 绿红
				// safeScodeLowX safeScodeHighX-safeScodeLowX 180-safeScodeHighX
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeLow, Color.rgb(247, 156, 27)));
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeHigh - safeScodeLow, Color.rgb(73, 172, 72)));
				mPartitionSet.add(new Pair<Float, Integer>((float) 180 - safeScodeHigh, Color.rgb(247, 156, 27)));
			} else
			{
				// 绿 红 绿
				// safeScodeHighX safeScodeLowX-safeScodeHighX 180-safeScodeLowX
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeHigh, Color.rgb(73, 172, 72)));// 绿
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeLow - safeScodeHigh, Color.rgb(247, 156, 27)));// 橙
				mPartitionSet.add(new Pair<Float, Integer>((float) 180 - safeScodeLow, Color.rgb(73, 172, 72)));// 红
			}
		return mPartitionSet;
	}	
	public void init()
	{
		gaugeChart01ViewX=(GaugeChart01View)findViewById(R.id.chart_view_x);
		gaugeChart01ViewY=(GaugeChart01View)findViewById(R.id.chart_view_y);
		degreeValueBar = (SeekBar) findViewById(R.id.seekbar_degree);
		tvDegree = (TextView) findViewById(R.id.tv_degree);
		tvXDegree=(TextView) findViewById(R.id.tv_x_degree);
		tvYDegree=(TextView) findViewById(R.id.tv_y_degree);
		tvXYDegree=(TextView) findViewById(R.id.tv_degree_xy);
		// 单选按钮
		//vhRadioGroup = (RadioGroup) findViewById(R.id.vh_radio_group);
		//verticalRadioButton = (RadioButton) findViewById(R.id.vertical_rd_btn);
		//horizontalRadioButton = (RadioButton) findViewById(R.id.horizontal_rd_Btn);

	}
	/**设置安全角度抖动范围*/ 
	public void setDegreeBar()
	{
		degreeValueBar.setProgress(shakeAngle);
		degreeValueBar.setMax(90);// [0,90]
		degreeValueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				if (fromUser)
					{
						shakeAngle = progress;
						tvDegree.setText(progress + "°");
					}
			}
		});
	}
/*
	public void addSeekArc()
	{
		mSeekArc = (SeekArc) findViewById(R.id.seekArc);
		mSeekArcProgress = (TextView) findViewById(R.id.seekArcProgress);
		mSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekArc seekArc)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekArc seekArc)
			{
			}

			@Override
			public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser)
			{
				speedValue = progress;
				// 灵敏度100%-0
				mSeekArcProgress.setText(String.valueOf(progress) + "%");
			}
		});
	}
*/
	public void sensorManager()
	{
		// 获取传感器管理器
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		registerAccelrometer();
	}

	/**
	 * 注册
	 */
	public void registerAccelrometer()
	{
		if (accelrometerSensor == null)
			return;
		sensorManager.registerListener(acceleromererListener, accelrometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private SensorEventListener acceleromererListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged(SensorEvent event)
		{
			avoidGavity(event);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			// wait for a minute
		}
	};
	//手机横坐标轴与平面夹角
	private int degreeX ;
	//手机竖坐标轴与平面夹角
	private int degreeY;
	private float[] gravity = new float[3];
	// 手机实际的加速度
	private int[] linear_acceleration = new int[3];
	private final float ALPHA = 0.8F;

	//之前的角度
	private int degreeXOld=-1;
	private int degreeYOld=-1;

	public void avoidGavity(SensorEvent event)
	{
		/*--------------------------计算手机实际加速度，这里使用了sdk提供的公式消除重力加速度--------------------------*/
		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

		linear_acceleration[0] = (int) (event.values[0] - gravity[0]);// x
		linear_acceleration[1] = (int) (event.values[1] - gravity[1]);// y
		linear_acceleration[2] = (int) (event.values[2] - gravity[2]);// z

		// 加速度=0才能得到正确的角度值
		if (linear_acceleration[2] != 0 || linear_acceleration[1] != 0 || linear_acceleration[0] != 0)
			{
				return;
			}

		/*--------------------------计算手机x,y轴夹角--------------------------*/
		int z = (int) event.values[2];
		LogUtil.w("z =" + z);

		degreeX = toDegreeX(event.values[0]);
		degreeY = toDegreeY(event.values[1]);

		LogUtil.d("实际 x degree=" + degreeX);
		LogUtil.d("实际 y degree=" + degreeY);
		// 转换后的角度
		degreeX = xAngleTo180(degreeX, z);
		degreeY = yAngleTo180(degreeY, z);
		// 去重复x,y数据
		if (degreeXOld != degreeX)
			{
				degreeXOld = degreeX;
				tvXDegree.setText("X与水平面夹角  " + degreeX + "°");
				gaugeChart01ViewX.setAngle(degreeX);
			}
		if (degreeYOld != degreeY)
			{
				degreeXOld = degreeY;
				tvYDegree.setText("Y与水平面夹角  " + degreeY + "°");
				gaugeChart01ViewY.setAngle(degreeY);
			}
	}

	/**
	 * x角度 [0,90] 
	 * 
	 * @param xx
	 * @return
	 * 手机右侧高+ 
	 * 手机左侧高- 
	 * 屏幕朝下无数据
	 */
	private /*static*/ int toDegreeX(float xx)
	{
		if (xx > 10)
			{
				xx = 10;
			} else if (xx < -10)
			{
				xx = -10;
			}
		double r = Math.asin(xx / 10);//Na
		int degree = (int) (r * 180 / Math.PI);
		return degree;
	}
	
	/**
	 * y角度 [0,90]
	 * 
	 * @param yy
	 * @return 
	 * 手机顶部高+ 
	 * 手机底部高- 
	 * 屏幕朝下无数据
	 */
	private /*static*/ int toDegreeY(float yy)
	{		
		if (yy > 10)
			{
				yy = 10;
			} else if (yy < -10)
			{
				yy = -10;
			}

		double r = Math.asin(yy / 10);
		int degree = (int) (r * 180 / Math.PI);
		return degree;
	}
	
	/**
	 * X角度转换成0-180
	 * 
	 * @param angle 要转换的x夹角
	 * @param zz 屏幕朝向（>0朝上，<0朝下）
	 * @return 0-180度
	 */
	public static int xAngleTo180(int angle, int zz)
	{
		
		if (angle == 0)
			{
				return 0;
			}
		if (angle < 0)
			{
				return zz > 0 ? -angle : 180 + angle;
			} else
			{
				return zz > 0 ? 180 - angle : angle;
			}
	}

	/**
	 * Y角度转换成0-180
	 * 
	 * @param angle 要转换的y夹角
	 * @param zz 屏幕朝向（>0朝上，<0朝下）
	 * @return 0-180度
	 */
	public static int yAngleTo180(int angle, int zz)
	{
		if (angle == 0)
			{
				return 0;
			}
		if (angle < 0)
			{
				return zz > 0 ? 180 + angle : -angle;
			} else
			{
				return zz > 0 ? angle : 180 - angle;
			}
	}
 
	// 退出时，保存设置
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		LogUtil.d("[SettingSpeedValue]  speedValue=" + speedValue);
		editor.putInt("speedValue", speedValue);
		editor.putInt("degree", shakeAngle);
		editor.commit();
		sensorManager.unregisterListener(acceleromererListener);
	}

	/** 
	 * 音量键保存安全角度
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			{
				LogUtil.i("[SettingSpeedValue]保存设置的手机角度");
				ToastUtil.makeText(context, "x="+degreeX+" y="+degreeY);
				editor.putInt("degreeX", degreeX);
				editor.putInt("degreeY", degreeY);
				editor.commit();
				tvXYDegree.setText("当前设置的角度x="+degreeX+"°  y="+degreeY+"°");
				drawPartition(degreeX,degreeY);
				return true;
			}
		return super.onKeyDown(keyCode, event);
	}
}
