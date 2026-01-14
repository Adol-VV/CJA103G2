package com.momento.prod.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sort")
public class ProductSortVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SORT_ID", updatable = false)
	private Integer sortId;
	
	@Column(name = "SORT_NAME")
	private Integer sortName;
	
	@Column(name = "SORT_DESC")
	private Integer sortDesc;
	
	public ProductSortVO() {
		
	}

	public Integer getSortId() {
		return sortId;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}

	public Integer getSortName() {
		return sortName;
	}

	public void setSortName(Integer sortName) {
		this.sortName = sortName;
	}

	public Integer getSortDesc() {
		return sortDesc;
	}

	public void setSortDesc(Integer sortDesc) {
		this.sortDesc = sortDesc;
	}
	
	
}
