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
 * 排序算法
 * 
 * @author Dawin
 * 
 */
public class SortAlgorithm
{

	/**
	 * 基数排序 基本思想：将所有待比较数值（正整数）统一为同样的数位长度，数位较短的数前面补零。 然后，从最低位开始，依次进行一次排序。
	 * 这样从最低位排序一直到最高位排序完成以后,数列就变成一个有序序列。
	 * 
	 * @param array
	 */
	public void radixSort(int[] array)
	{

		// 首先确定排序的趟数;
		int max = array[0];
		for (int i = 1; i < array.length; i++)
			{
				if (array[i] > max)
					{
						max = array[i];
					}
			}

		int time = 0;
		// 判断位数;
		while (max > 0)
			{
				max /= 10;
				time++;
			}
		// 建立10个队列;
		List<ArrayList> queue = new ArrayList<ArrayList>();
		for (int i = 0; i < 10; i++)
			{
				ArrayList<Integer> queue1 = new ArrayList<Integer>();
				queue.add(queue1);
			}
		// 进行time次分配和收集;
		for (int i = 0; i < time; i++)
			{
				// 分配数组元素;
				for (int j = 0; j < array.length; j++)
					{
						// 得到数字的第time+1位数;
						int x = array[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
						ArrayList<Integer> queue2 = queue.get(x);
						queue2.add(array[j]);
						queue.set(x, queue2);
					}
			
				int count = 0;// 元素计数器;
				// 收集队列元素;
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
	 * 归并排序
	 * （1）基本排序：归并（Merge）排序法是将两个（或两个以上）有序表合并成一个新的有序表，即把待排序序列分为若干个子序列，每个子序列是有序的 。
	 * 然后再把有序子序列合并为整体有序序列。
	 */
	public void mergingSort(int[] data, int left, int right)
	{

		if (left < right)
			{
				// 找出中间索引
				int center = (left + right) / 2;
				// 对左边数组进行递归
				mergingSort(data, left, center);
				// 对右边数组进行递归
				mergingSort(data, center + 1, right);
				// 合并
				merge(data, left, center, right);

			}
	}

	public void merge(int[] data, int left, int center, int right)
	{
	
		int[] tmpArr = new int[data.length];
		int mid = center + 1;
		// third记录中间数组的索引
		int third = left;
		int tmp = left;
		while (left <= center && mid <= right)
			{

				// 从两个数组中取出最小的放入中间数组
				if (data[left] <= data[mid])
					{
						tmpArr[third++] = data[left++];
					} else
					{
						tmpArr[third++] = data[mid++];
					}
			}
		// 剩余部分依次放入中间数组
		while (mid <= right)
			{
				tmpArr[third++] = data[mid++];
			}
		while (left <= center)
			{
				tmpArr[third++] = data[left++];
			}
		// 将中间数组中的内容复制回原数组
		while (tmp <= right)
			{
				data[tmp] = tmpArr[tmp++];
			}
		System.out.println(Arrays.toString(data));
	}

	/*
	 * 快速排序
	 * （1）基本思想：选择一个基准元素,通常选择第一个元素或者最后一个元素,通过一趟扫描，将待排序列分成两部分,一部分比基准元素小,一部分大于等于基准元素
	 * ,此时基准元素在其排好序后的正确位置,然后再用同样的方法递归地排序划分的两部分。
	 */

	public void _quickSort(int[] list, int low, int high)
	{
		if (low < high)
			{
				int middle = getMiddle(list, low, high); // 将list数组进行一分为二
				_quickSort(list, low, middle - 1); // 对低字表进行递归排序
				_quickSort(list, middle + 1, high); // 对高字表进行递归排序
			}
	}
	public int getMiddle(int[] list, int low, int high)
	{
		int tmp = list[low]; // 数组的第一个作为中轴
		while (low < high)
			{
				while (low < high && list[high] >= tmp)
					{

						high--;
					}
				list[low] = list[high]; // 比中轴小的记录移到低端
				while (low < high && list[low] <= tmp)
					{
						low++;
					}
				list[high] = list[low]; // 比中轴大的记录移到高端
			}
		list[low] = tmp; // 中轴记录到尾
		
		return low; // 返回中轴的位置
		
	}

	public void quick(int[] a2)
	{
		if (a2.length > 0)
			{ // 查看数组是否为空
				_quickSort(a2, 0, a2.length - 1);
			}
	}

}
