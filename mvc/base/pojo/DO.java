package com.jo.analysis.web.mvc.base.pojo;

import com.jo.analysis.web.mvc.base.annotation.Column;
import com.jo.analysis.web.mvc.entity.POJO;

/**
 * Auth: Jo.Ho
 * Email: 827661159@qq.com
 * Date: 2018/4/20
 *
 */
public class DO extends POJO {

	@Column("id")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
