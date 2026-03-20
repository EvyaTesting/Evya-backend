package com.ewe.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.dao.Dao;
import com.ewe.pojo.BaseEntity;

@Repository
@SuppressWarnings("unchecked")
public abstract class DaoImpl<T extends BaseEntity, I> implements Dao<T, I> {

	@Autowired
	protected SessionFactory sessionFactory;

	Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> findAll() {
		String hql = "FROM T t ORDER BY t.id";
		return getCurrentSession().createQuery(hql).list();
	}

	@Override
	@Transactional(readOnly = true)
	public T find(I id) {
		String hql = "FROM T t where id=" + id;
		return (T) getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public T save(T newsEntry) {
		getCurrentSession().save(newsEntry);
		return newsEntry;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public T saveOrUpdate(T newsEntry) {
		getCurrentSession().saveOrUpdate(newsEntry);
		return newsEntry;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public T update(T newsEntry) {
		getCurrentSession().update(newsEntry);
		return newsEntry;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(T newsEntry) {
		// String hql = "delete FROM T t where id=" + id;
		getCurrentSession().delete(newsEntry);

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
	public T merge(T newsEntry) {
		// String hql = "delete FROM T t where id=" + id;
		getCurrentSession().merge(newsEntry);
		return newsEntry;

	}

}
