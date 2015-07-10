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
 * ����������Ӧģʽ�İ�ȫ�Ƕ�,�Ƕ�ָ���ٶȴ�����x,y��ˮƽ��н� * 
 * @author Dawin
 * 
 */
public class SettingXYAngleActivity extends BaseActivity
{
	
	final static boolean IS_X=true;
	//�ı䶶����Χ
	public SeekBar degreeValueBar;	
	//public TextView mSeekArcProgress;
     //�����İ�ȫ�Ƕ�
	private TextView tvDegree;
	//��ǰ�Ƕ�
	private TextView tvXDegree;
	private TextView tvYDegree;
	//���õ�x,y�Ƕ�
	private TextView tvXYDegree;
	private SensorManager sensorManager;
	private Sensor accelrometerSensor;
	/**
	 * �̶���X
	 */
	private GaugeChart01View gaugeChart01ViewX;
	
	/**
	 * �̶���Y
	 */
	private GaugeChart01View gaugeChart01ViewY;	
	//�����߽�
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
		// ������Χ    ��ȡ���õķ�ֵ
		speedValue = sp.getInt("speedValue", 20/* Ĭ��ֵ����������С */);
		shakeAngle=sp.getInt("degree", 20/* Ĭ��ֵ����������С */);
		tvDegree.setText(shakeAngle+"��");
		settedX=sp.getInt("degreeX", 0);
		settedY=sp.getInt("degreeY", 0);
		
		tvXYDegree.setText("��ǰ���õĽǶ�x="+settedX+"��  y="+settedY+"��");
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
	 * ���±��̰�ȫ�;�����
	 * 
	 * @param x
	 *            x��ˮƽ��н�
	 * @param y
	 *            y��ˮƽ��н�
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
					LogUtil.e("��ֱ");
					isHorizontal=false;
					break;
				case R.id.horizontal_rd_Btn:
					LogUtil.e("ˮƽ");
					isHorizontal=true;
					break;
				}
			}
		});
	}
*/
	
	/**
	 * ���̷����Ƕ�[�Ƕ�(0-mStartAngle)����ɫ]
	 * @param degree0 ���õ�x�н�
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
				// �� �̺�
				// safeScodeLowX safeScodeHighX-safeScodeLowX 180-safeScodeHighX
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeLow, Color.rgb(247, 156, 27)));
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeHigh - safeScodeLow, Color.rgb(73, 172, 72)));
				mPartitionSet.add(new Pair<Float, Integer>((float) 180 - safeScodeHigh, Color.rgb(247, 156, 27)));
			} else
			{
				// �� �� ��
				// safeScodeHighX safeScodeLowX-safeScodeHighX 180-safeScodeLowX
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeHigh, Color.rgb(73, 172, 72)));// ��
				mPartitionSet.add(new Pair<Float, Integer>((float) safeScodeLow - safeScodeHigh, Color.rgb(247, 156, 27)));// ��
				mPartitionSet.add(new Pair<Float, Integer>((float) 180 - safeScodeLow, Color.rgb(73, 172, 72)));// ��
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
		// ��ѡ��ť
		//vhRadioGroup = (RadioGroup) findViewById(R.id.vh_radio_group);
		//verticalRadioButton = (RadioButton) findViewById(R.id.vertical_rd_btn);
		//horizontalRadioButton = (RadioButton) findViewById(R.id.horizontal_rd_Btn);

	}
	/**���ð�ȫ�Ƕȶ�����Χ*/ 
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
						tvDegree.setText(progress + "��");
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
				// ������100%-0
				mSeekArcProgress.setText(String.valueOf(progress) + "%");
			}
		});
	}
*/
	public void sensorManager()
	{
		// ��ȡ������������
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		registerAccelrometer();
	}

	/**
	 * ע��
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
	//�ֻ�����������ƽ��н�
	private int degreeX ;
	//�ֻ�����������ƽ��н�
	private int degreeY;
	private float[] gravity = new float[3];
	// �ֻ�ʵ�ʵļ��ٶ�
	private int[] linear_acceleration = new int[3];
	private final float ALPHA = 0.8F;

	//֮ǰ�ĽǶ�
	private int degreeXOld=-1;
	private int degreeYOld=-1;

	public void avoidGavity(SensorEvent event)
	{
		/*--------------------------�����ֻ�ʵ�ʼ��ٶȣ�����ʹ����sdk�ṩ�Ĺ�ʽ�����������ٶ�--------------------------*/
		gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
		gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
		gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

		linear_acceleration[0] = (int) (event.values[0] - gravity[0]);// x
		linear_acceleration[1] = (int) (event.values[1] - gravity[1]);// y
		linear_acceleration[2] = (int) (event.values[2] - gravity[2]);// z

		// ���ٶ�=0���ܵõ���ȷ�ĽǶ�ֵ
		if (linear_acceleration[2] != 0 || linear_acceleration[1] != 0 || linear_acceleration[0] != 0)
			{
				return;
			}

		/*--------------------------�����ֻ�x,y��н�--------------------------*/
		int z = (int) event.values[2];
		LogUtil.w("z =" + z);

		degreeX = toDegreeX(event.values[0]);
		degreeY = toDegreeY(event.values[1]);

		LogUtil.d("ʵ�� x degree=" + degreeX);
		LogUtil.d("ʵ�� y degree=" + degreeY);
		// ת����ĽǶ�
		degreeX = xAngleTo180(degreeX, z);
		degreeY = yAngleTo180(degreeY, z);
		// ȥ�ظ�x,y����
		if (degreeXOld != degreeX)
			{
				degreeXOld = degreeX;
				tvXDegree.setText("X��ˮƽ��н�  " + degreeX + "��");
				gaugeChart01ViewX.setAngle(degreeX);
			}
		if (degreeYOld != degreeY)
			{
				degreeXOld = degreeY;
				tvYDegree.setText("Y��ˮƽ��н�  " + degreeY + "��");
				gaugeChart01ViewY.setAngle(degreeY);
			}
	}

	/**
	 * x�Ƕ� [0,90] 
	 * 
	 * @param xx
	 * @return
	 * �ֻ��Ҳ��+ 
	 * �ֻ�����- 
	 * ��Ļ����������
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
	 * y�Ƕ� [0,90]
	 * 
	 * @param yy
	 * @return 
	 * �ֻ�������+ 
	 * �ֻ��ײ���- 
	 * ��Ļ����������
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
	 * X�Ƕ�ת����0-180
	 * 
	 * @param angle Ҫת����x�н�
	 * @param zz ��Ļ����>0���ϣ�<0���£�
	 * @return 0-180��
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
	 * Y�Ƕ�ת����0-180
	 * 
	 * @param angle Ҫת����y�н�
	 * @param zz ��Ļ����>0���ϣ�<0���£�
	 * @return 0-180��
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
 
	// �˳�ʱ����������
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
	 * ���������氲ȫ�Ƕ�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
			{
				LogUtil.i("[SettingSpeedValue]�������õ��ֻ��Ƕ�");
				ToastUtil.makeText(context, "x="+degreeX+" y="+degreeY);
				editor.putInt("degreeX", degreeX);
				editor.putInt("degreeY", degreeY);
				editor.commit();
				tvXYDegree.setText("��ǰ���õĽǶ�x="+degreeX+"��  y="+degreeY+"��");
				drawPartition(degreeX,degreeY);
				return true;
			}
		return super.onKeyDown(keyCode, event);
	}
}
