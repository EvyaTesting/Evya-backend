package com.ewe.dao.impl;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.query.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ewe.controller.advice.ServerException;
import com.ewe.dao.GeneralDao;
import com.ewe.exception.UserNotFoundException;
import com.ewe.pojo.BaseEntity;
import com.ewe.pojo.ChargingActivity;
import com.ewe.pojo.Referral;
import com.ewe.pojo.Role;
import com.ewe.pojo.Site;
import com.ewe.pojo.User;
import com.ewe.serviceImpl.TicketServiceImpl;

@Repository
public class GeneralDaoImpl<T extends BaseEntity, I> implements GeneralDao<T, I> {
	
  @Autowired
  protected SessionFactory sessionFactory;
  @PersistenceContext
  private EntityManager entityManager;

	 private  static final  Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);

  Session getCurrentSession() {
    return this.sessionFactory.getCurrentSession();
  }
  
  
  @Transactional(readOnly = true)
  public <T> List<T> findAll(T newsEntry) {
    Criteria crit = getCurrentSession().createCriteria(newsEntry.getClass());
    return crit.list();
  }
  
  @Transactional(readOnly = true)
  public <T> T findOneById(T newsEntry, long id) throws UserNotFoundException {
    if (newsEntry == null)
      throw new UserNotFoundException(newsEntry.getClass() + " id [" + id + "] not found"); 
    newsEntry = (T)getCurrentSession().get(newsEntry.getClass(), Long.valueOf(id));
    return newsEntry;
  }
  
  @Transactional(propagation = Propagation.REQUIRED)
  public <T> T save(T newsEntry) throws UserNotFoundException {
    getCurrentSession().save(newsEntry);
    return newsEntry;
  }
  
  @Transactional(propagation = Propagation.REQUIRED)
  public <T> T update(T newsEntry) throws UserNotFoundException {
    getCurrentSession().update(newsEntry);
    return newsEntry;
  }
  
  @Transactional(propagation = Propagation.REQUIRED)
  public <T> T savOrupdate(T newsEntry) throws UserNotFoundException {
    getCurrentSession().saveOrUpdate(newsEntry);
    return newsEntry;
  }
  
  @Transactional(propagation = Propagation.REQUIRED)
  public <T> void delete(T newsEntry) throws UserNotFoundException {
    getCurrentSession().delete(newsEntry);
  }
  
  @Transactional(propagation = Propagation.REQUIRED)
  public <T> T merge(T newsEntry) {
    getCurrentSession().merge(newsEntry);
    return newsEntry;
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> findAllSQLQuery(T newsEntry, String query) throws UserNotFoundException {
    List<T> list = getCurrentSession().createSQLQuery(query).addEntity(newsEntry.getClass()).list();
    return list;
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> findAllHQLQuery(T newsEntry, String query) throws UserNotFoundException {
    List<T> list = getCurrentSession().createQuery(query).list();
    return list;
  }
  @Override
  @Transactional(readOnly = true)
  public <T> List<T> findAllSQLQuery(T entity, String query, Map<String, Object> params) throws UserNotFoundException {
      SQLQuery sqlQuery = getCurrentSession()
          .createSQLQuery(query)
          .addEntity(entity.getClass());

      if (params != null && !params.isEmpty()) {
          for (Map.Entry<String, Object> entry : params.entrySet()) {
              sqlQuery.setParameter(entry.getKey(), entry.getValue());
          }
      }

      return sqlQuery.list();
  }

  @Transactional(readOnly = true)
  public List<?> findAllByIdSQLQuery(String query) throws UserNotFoundException {
    return getCurrentSession().createSQLQuery(query).list();
  }
  
  @Transactional(readOnly = true)
  public <T> T findOneSQLQuery(T newsEntry, String query) throws UserNotFoundException {
    newsEntry = (T)getCurrentSession().createSQLQuery(query).addEntity(newsEntry.getClass()).uniqueResult();
    return newsEntry;
  }
  
  @Transactional(readOnly = true)
  public <T> T findOneHQLQuery(T newsEntry, String query) throws UserNotFoundException {
    newsEntry = (T)getCurrentSession().createQuery(query).uniqueResult();
    return newsEntry;
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> findAllCriteriaQuery(T newsEntry, Criteria criteria) throws UserNotFoundException {
    criteria.setProjection(null);
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    List<T> data = criteria.list();
    return data;
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> findAllCriteriaQueryManyToMany(T newsEntry, TypedQuery<T> criteria) throws UserNotFoundException {
    List<T> data = criteria.getResultList();
    if (data.size() == 0)
      throw new UserNotFoundException(criteria + " data not found"); 
    return data;
  }
  
  @Transactional(readOnly = true)
  public Long countCriteriaQuery(Criteria criteria) throws UserNotFoundException {
    criteria.setProjection(Projections.rowCount());
    criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    return (Long)criteria.uniqueResult();
  }
  
  @Transactional(readOnly = true)
  public Long countSQL(String sql) throws UserNotFoundException {
    return Long.valueOf(getCurrentSession().createSQLQuery(sql).list().size());
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> findAllByIdHQLQuery(T newsEntry, String query, String parameter, List<Long> ids) {
    Query<?> q = getCurrentSession().createQuery(query);
    q.setParameterList(parameter, ids);
    return (List<T>) q.list();
  }
  
  @Transactional(readOnly = true)
  public List<?> findAllSingalObject(String query) {
    return getCurrentSession().createSQLQuery(query).list();
  }
  
  @Transactional(readOnly = true)
  public <T> List<Map<String, Object>> findAliasData(String query) {
    return getCurrentSession().createSQLQuery(query).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
  }
  
  @Transactional(readOnly = true)
  public <T> int queryExecute(String query) {
    return getCurrentSession().createSQLQuery(query).executeUpdate();
  }
  
  public <T> List<T> findAllNamedQuery(String queryName, String fieldName, List<?> ls) {
    return getCurrentSession().getNamedQuery(queryName).setParameterList(fieldName, ls).list();
  }
  
  @Transactional(readOnly = true)
  public String getSingleRecord(String hql) {
    String result = (String)getCurrentSession().createSQLQuery(hql).setMaxResults(1).uniqueResult();
    if (result == null)
      return ""; 
    return result;
  }
  
  @Transactional(readOnly = true)
  public <T> List<Map<String, Object>> findAliasData(String query, String parameter, String parameterValue) {
    return getCurrentSession().createQuery(query).setParameter(parameter, parameterValue)
      .setResultTransformer((ResultTransformer)Transformers.ALIAS_TO_ENTITY_MAP).list();
  }
  
  @Transactional(readOnly = true)
  public List<Map<String, Object>> getMapData(String hql) {
    List<Map<String, Object>> mapObj = getCurrentSession().createSQLQuery(hql).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();
    return mapObj;
  }
  
  @Transactional
  public String deleteSqlQuiries(String hql) {
    this.sessionFactory.getCurrentSession().createSQLQuery(hql).executeUpdate();
    return "success";
  }
  
  public long findIdBySqlQuery(String hql) {
    BigDecimal id = (BigDecimal)getCurrentSession().createSQLQuery(hql).uniqueResult();
    return id.longValue();
  }
  
  public Float findIdByOneSqlQuery(String hql) {
    String id = (String)getCurrentSession().createSQLQuery(hql).uniqueResult();
    return Float.valueOf((id != null) ? Float.valueOf(id).floatValue() : 0.0F);
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> findAllWithMaxRecord(T newsEntry, String query, int limit) {
    return getCurrentSession().createQuery(query).setMaxResults(limit).list();
  }
  
  @Transactional(readOnly = true)
  public <T> List<T> getUserByRole(T newsEntry, String roleName) throws UserNotFoundException {
    CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
    CriteriaQuery<User> cq = cb.createQuery(User.class);
    Root<User> from = cq.from(User.class);
    Join<User, Role> join = from.join("roles", JoinType.LEFT);
    cq.where((Expression)cb.equal((Expression)join.get("rolename"), roleName));
    Query query = getCurrentSession().createQuery(cq);
    List<T> data = findAllCriteriaQueryManyToMany(newsEntry, (TypedQuery<T>)query);
    return data;
  }
  
  @Transactional(readOnly = true)
  public int updateSqlQuery(String query) {
    return getCurrentSession().createSQLQuery(query).executeUpdate();
  }
  
  @Transactional
  public void excuteUpdateUserData(String query) {
    getCurrentSession().createSQLQuery(query).executeUpdate();
  }
  
  @Transactional(readOnly = true)
  public String listOfStringData(String queryString) {
    return getCurrentSession().createSQLQuery(queryString).list().toString().replace("[", "'").replace("]", "'")
      .replace(", ", "','");
  }
  
  
  @Transactional(readOnly = true)
  public User getUser(long userId) throws UserNotFoundException {
    User userObject = (User)getCurrentSession().get(User.class, Long.valueOf(userId));
    if (userObject == null)
      throw new UserNotFoundException("User id [" + userId + "] not found"); 
    return userObject;
  }
  
  public BigDecimal getSingleRecordofOrgId(String hql) {
    BigDecimal result = (BigDecimal)getCurrentSession().createSQLQuery(hql).setMaxResults(1).uniqueResult();
    return result;
  }
  
  public String findIdBySqlQueryString(String hql) {
    Timestamp id = (Timestamp)getCurrentSession().createSQLQuery(hql).uniqueResult();
    if (id == null)
      return "0"; 
    return id.toString();
  }
  
  public Map<String, Object> getMap_Data(String query1) {
    Map<String, Object> mapObj = (Map<String, Object>)getCurrentSession().createSQLQuery(query1);
    return mapObj;
  }
	
	@Override
	public long getCountByUserId(Long paramLong) throws ServerException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	public User findByEmail(String email) throws UserNotFoundException {
	    try {
	        String hql = "FROM User WHERE email = :email";
	        Query<User> query = getCurrentSession().createQuery(hql, User.class);
	        query.setParameter("email", email);
	        return query.uniqueResult();
	    } catch (NoResultException e) {
	        throw new UserNotFoundException("User with email " + email + " not found");
	    }
	}
	
	
	@Override
	public User findUserByChargerId(String chargerId) {
	    try {
	        // Query the database using the charger ID
	        User user = entityManager.createQuery(
	                "SELECT u FROM User u WHERE u.chargerId = :chargerId", User.class)
	                .setParameter("chargerId", chargerId)
	                .getSingleResult();
	        
	        LOGGER.info("User found for charger ID {}: {}", chargerId, user.getEmail());
	        return user;
	    } catch (NoResultException e) {
	        LOGGER.warn("No user found with charger ID: {}", chargerId);
	        return null;
	    } catch (Exception e) {
	        LOGGER.error("Error while fetching user by charger ID: {}", chargerId, e);
	        return null;
	    }
	}
	@Override
	public Query createNativeQuery(String sql) {
	    return (Query) entityManager.createNativeQuery(sql);
	}
	@Override
	public List<Site> findAllOrg(Site site, String query) {
	    return entityManager.createNativeQuery(query, Site.class).getResultList();
	}
	
	
	@Override
	public List<ChargingActivity> findByQuery(String jpql, Map<String, Object> params, Class<ChargingActivity> clazz) {
	    TypedQuery<ChargingActivity> query = entityManager.createQuery(jpql, clazz);
	
	    if (params != null && !params.isEmpty()) {
	        for (Map.Entry<String, Object> entry : params.entrySet()) {
	            query.setParameter(entry.getKey(), entry.getValue());
	        }
	    }
	
	    return query.getResultList();
	}
	
	@Override
	    public <T> List<T> findAllByIds(Class<T> clazz, List<Long> ids) {
	        if (ids == null || ids.isEmpty()) {
	            return Collections.emptyList();
	        }
	
	        String entityName = clazz.getSimpleName(); // e.g., "Site"
	        String idFieldName = "id"; // assuming "id" is the primary key field
	
	        String jpql = "SELECT e FROM " + entityName + " e WHERE e." + idFieldName + " IN :ids";
	        return entityManager.createQuery(jpql, clazz)
	                            .setParameter("ids", ids)
	                            .getResultList();
	    }
	@Override
	public Map<String, Object> getSingleMapData(String query) {
	    List<Map<String, Object>> results = getMapData(query);
	    return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	public <T> List<T> findAllHQLQueryPaginated(T entity, String hql, int page, int size, Map<String, Object> parameters) {
	    TypedQuery<T> query = entityManager.createQuery(hql, (Class<T>) entity.getClass());
	    for (Map.Entry<String, Object> entry : parameters.entrySet()) {
	        query.setParameter(entry.getKey(), entry.getValue());
	    }
	    query.setFirstResult(page * size);
	    query.setMaxResults(size);
	    return query.getResultList();
	}
	
	@Override
	public Long countHQLQuery(String hql, Map<String, Object> parameters) {
	    TypedQuery<Long> query = entityManager.createQuery(hql, Long.class);
	    for (Map.Entry<String, Object> entry : parameters.entrySet()) {
	        query.setParameter(entry.getKey(), entry.getValue());
	    }
	    return query.getSingleResult();
	}
	
	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public <T> List<T> findAllHQLQry(T entity, String hql, Object... params) throws UserNotFoundException {
	    Query<T> query = (Query<T>) getCurrentSession().createQuery(hql, (Class<T>) entity.getClass());
	
	    if (params != null) {
	        for (int i = 0; i < params.length; i++) {
	            query.setParameter(i + 1, params[i]);
	        }
	    }
	
	    return query.list();
	}
	
	@Override
	public <T> List<T> findByHQL(String hql, Map<String, Object> params) {
	    Session session = entityManager.unwrap(Session.class);
	    Query<T> query = session.createQuery(hql);
	
	    if (params != null) {
	        params.forEach(query::setParameter);
	    }
	
	    return query.getResultList();
	}

}
