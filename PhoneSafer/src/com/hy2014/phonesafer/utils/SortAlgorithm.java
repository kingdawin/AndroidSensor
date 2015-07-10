/*
 *@author Dawin,2015-1-14
 *
 *
 *
 */
package com.hy2014.phonesafer.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.provider.ContactsContract.CommonDataKinds.Nickname;

/**
 * �����㷨
 * 
 * @author Dawin
 * 
 */
public class SortAlgorithm
{

	/**
	 * �������� ����˼�룺�����д��Ƚ���ֵ����������ͳһΪͬ������λ���ȣ���λ�϶̵���ǰ�油�㡣 Ȼ�󣬴����λ��ʼ�����ν���һ������
	 * ���������λ����һֱ�����λ��������Ժ�,���оͱ��һ���������С�
	 * 
	 * @param array
	 */
	public void radixSort(int[] array)
	{

		// ����ȷ�����������;
		int max = array[0];
		for (int i = 1; i < array.length; i++)
			{
				if (array[i] > max)
					{
						max = array[i];
					}
			}

		int time = 0;
		// �ж�λ��;
		while (max > 0)
			{
				max /= 10;
				time++;
			}
		// ����10������;
		List<ArrayList> queue = new ArrayList<ArrayList>();
		for (int i = 0; i < 10; i++)
			{
				ArrayList<Integer> queue1 = new ArrayList<Integer>();
				queue.add(queue1);
			}
		// ����time�η�����ռ�;
		for (int i = 0; i < time; i++)
			{
				// ��������Ԫ��;
				for (int j = 0; j < array.length; j++)
					{
						// �õ����ֵĵ�time+1λ��;
						int x = array[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
						ArrayList<Integer> queue2 = queue.get(x);
						queue2.add(array[j]);
						queue.set(x, queue2);
					}
			
				int count = 0;// Ԫ�ؼ�����;
				// �ռ�����Ԫ��;
				for (int k = 0; k < 10; k++)
					{
						while (queue.get(k).size() > 0)
							{
								ArrayList<Integer> queue3 = queue.get(k);
								array[count] = queue3.get(0);
								queue3.remove(0);
								count++;
							}
					}
			}			
	}

	/*
	 * �鲢����
	 * ��1���������򣺹鲢��Merge�������ǽ����������������ϣ������ϲ���һ���µ���������Ѵ��������з�Ϊ���ɸ������У�ÿ��������������� ��
	 * Ȼ���ٰ����������кϲ�Ϊ�����������С�
	 */
	public void mergingSort(int[] data, int left, int right)
	{

		if (left < right)
			{
				// �ҳ��м�����
				int center = (left + right) / 2;
				// �����������еݹ�
				mergingSort(data, left, center);
				// ���ұ�������еݹ�
				mergingSort(data, center + 1, right);
				// �ϲ�
				merge(data, left, center, right);

			}
	}

	public void merge(int[] data, int left, int center, int right)
	{
	
		int[] tmpArr = new int[data.length];
		int mid = center + 1;
		// third��¼�м����������
		int third = left;
		int tmp = left;
		while (left <= center && mid <= right)
			{

				// ������������ȡ����С�ķ����м�����
				if (data[left] <= data[mid])
					{
						tmpArr[third++] = data[left++];
					} else
					{
						tmpArr[third++] = data[mid++];
					}
			}
		// ʣ�ಿ�����η����м�����
		while (mid <= right)
			{
				tmpArr[third++] = data[mid++];
			}
		while (left <= center)
			{
				tmpArr[third++] = data[left++];
			}
		// ���м������е����ݸ��ƻ�ԭ����
		while (tmp <= right)
			{
				data[tmp] = tmpArr[tmp++];
			}
		System.out.println(Arrays.toString(data));
	}

	/*
	 * ��������
	 * ��1������˼�룺ѡ��һ����׼Ԫ��,ͨ��ѡ���һ��Ԫ�ػ������һ��Ԫ��,ͨ��һ��ɨ�裬���������зֳ�������,һ���ֱȻ�׼Ԫ��С,һ���ִ��ڵ��ڻ�׼Ԫ��
	 * ,��ʱ��׼Ԫ�������ź�������ȷλ��,Ȼ������ͬ���ķ����ݹ�����򻮷ֵ������֡�
	 */

	public void _quickSort(int[] list, int low, int high)
	{
		if (low < high)
			{
				int middle = getMiddle(list, low, high); // ��list�������һ��Ϊ��
				_quickSort(list, low, middle - 1); // �Ե��ֱ���еݹ�����
				_quickSort(list, middle + 1, high); // �Ը��ֱ���еݹ�����
			}
	}
	public int getMiddle(int[] list, int low, int high)
	{
		int tmp = list[low]; // ����ĵ�һ����Ϊ����
		while (low < high)
			{
				while (low < high && list[high] >= tmp)
					{

						high--;
					}
				list[low] = list[high]; // ������С�ļ�¼�Ƶ��Ͷ�
				while (low < high && list[low] <= tmp)
					{
						low++;
					}
				list[high] = list[low]; // �������ļ�¼�Ƶ��߶�
			}
		list[low] = tmp; // �����¼��β
		
		return low; // ���������λ��
		
	}

	public void quick(int[] a2)
	{
		if (a2.length > 0)
			{ // �鿴�����Ƿ�Ϊ��
				_quickSort(a2, 0, a2.length - 1);
			}
	}

}
