package com.hy2014.phonesafer.gestureLock;

import com.hy2014.phonesafer.utils.LogUtil;

public class MyCircle {	
	private int ox;          // Բ�ĺ�����
	private int oy;          // Բ��������
	private float r;         // �뾶����
	private Integer num;     // ������ֵ
	private boolean onTouch; // false=δѡ��
	public int getOx() {
		return ox;
	}
	public void setOx(int ox) {
		this.ox = ox;
	}
	public int getOy() {
		return oy;
	}
	public void setOy(int oy) {
		this.oy = oy;
	}
	public float getR() {
		return r;
	}
	public void setR(float r) {
		this.r = r;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public boolean isOnTouch() {
		return onTouch;
	}
	public void setOnTouch(boolean onTouch) {
		this.onTouch = onTouch;
	}

	/**
	 * ���������Ƿ���Բ��
	 * 
	 * @param x 
	 * @param y
	 * @return
	 */
	public boolean isPointIn(int x, int y)
	{
		//LogUtil.e("x="+x+" y="+y);
		// [ox-r,ox+r]
		// [oy-r,oy+r]
		// return (x>= (ox - r)) && (x<=(ox + r) ) &&(y >= (oy - r)) &&( y <=(oy + r) );
		double distance = Math.sqrt((x - ox) * (x - ox) + (y - oy) * (y - oy));
		return distance <= 2 * r;
	}
}
