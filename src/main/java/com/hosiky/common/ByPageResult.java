package com.hosiky.common;

import com.github.pagehelper.PageInfo;

import java.util.LinkedList;
import java.util.List;


public class ByPageResult<T> {

	private List<T> rows;
	private long total;

	public ByPageResult() {

	}

	public static <T> ByPageResult<T> empty() {
		return new ByPageResult<T>(new LinkedList<T>(), 0);
	}

	public static <T> ByPageResult<T> build(PageInfo<T> pageInfo) {
		return new ByPageResult<T>(pageInfo.getList(), pageInfo.getTotal());
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public ByPageResult(List<T> rows, long total) {
		this.setRows(rows);
		this.setTotal(total);
	}

}
