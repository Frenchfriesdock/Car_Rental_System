package com.hosiky.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotNull;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class PageParameter<T> {

	@NotNull(message = "当前页不可为空")
	int page;

	@NotNull(message = "当前页大小不可为空")
	int limit;

	T data;

	@NotNull(message = "当前页不可为空")
	public int getPage() {
		return page;
	}

	public void setPage(@NotNull(message = "当前页不可为空") int page) {
		this.page = page;
	}

	@NotNull(message = "当前页大小不可为空")
	public int getLimit() {
		return limit;
	}

	public void setLimit(@NotNull(message = "当前页大小不可为空") int limit) {
		this.limit = limit;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
