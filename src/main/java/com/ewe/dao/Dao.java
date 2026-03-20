package com.ewe.dao;

import java.util.List;

import com.ewe.pojo.BaseEntity;

public interface Dao<T extends BaseEntity, I>
{
	List<T> findAll();
	T find(I id);
	T save(T newsEntry);
	T update(T newsEntry);
	void delete(T newsEntry);
	T saveOrUpdate(T newsEntry);
	T merge(T newsEntry);
}