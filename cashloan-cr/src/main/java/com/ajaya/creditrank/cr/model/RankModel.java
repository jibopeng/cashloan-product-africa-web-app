package com.ajaya.creditrank.cr.model;

import com.ajaya.creditrank.cr.domain.Rank;


/** 
 * @author lyang
 * @version 1.0
 * @date 2017-1-16 下午4:35:03
 */
@SuppressWarnings("serial")
public class RankModel extends Rank{

	private String num;

	/**
	 * 获取num
	 * @return num
	 */
	public String getNum() {
		return num;
	}

	/**
	 * 设置num
	 * @param num
	 */
	public void setNum(String num) {
		this.num = num;
	}
}
