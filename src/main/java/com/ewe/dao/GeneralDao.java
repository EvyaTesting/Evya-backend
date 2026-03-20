package com.ewe.dao;

import com.ewe.controller.advice.ServerException;

import com.ewe.exception.UserNotFoundException;
import com.ewe.pojo.ChargingActivity;
import com.ewe.pojo.Employee;
import com.ewe.pojo.ManufacturerDetails;
import com.ewe.pojo.Referral;
import com.ewe.pojo.Site;
import com.ewe.pojo.Station;
import com.ewe.pojo.User;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.hibernate.Criteria;

public interface GeneralDao<T extends com.ewe.pojo.BaseEntity, I> {
  <T> T update(T paramT) throws UserNotFoundException;
  
  <T> T savOrupdate(T paramT) throws UserNotFoundException;
  
  <T> void delete(T paramT) throws UserNotFoundException;
  
  <T> T save(T paramT) throws UserNotFoundException;
  
  <T> T merge(T paramT);
  
  <T> T findOneById(T paramT, long paramLong) throws UserNotFoundException;
  
  <T> List<T> findAll(T paramT);
  
  <T> T findOneSQLQuery(T paramT, String paramString) throws UserNotFoundException;
  
  <T> T findOneHQLQuery(T paramT, String paramString) throws UserNotFoundException;
  
  <T> List<T> findAllSQLQuery(T paramT, String paramString) throws UserNotFoundException;
  
  <T> List<T> findAllHQLQuery(T paramT, String paramString) throws UserNotFoundException;
  
  Long countSQL(String paramString) throws UserNotFoundException;
  
  <T> List<T> findAllByIdHQLQuery(T paramT, String paramString1, String paramString2, List<Long> paramList);
  
  <T> List<T> findAllCriteriaQuery(T paramT, Criteria paramCriteria) throws UserNotFoundException;
  
  Long countCriteriaQuery(Criteria paramCriteria) throws UserNotFoundException;
  
  <T> List<T> findAllCriteriaQueryManyToMany(T paramT, TypedQuery<T> paramTypedQuery) throws UserNotFoundException;
  
  List<?> findAllByIdSQLQuery(String paramString) throws UserNotFoundException;
  <T> List<T> findAllSQLQuery(T entity, String query, Map<String, Object> params) throws UserNotFoundException;

  <T> int queryExecute(String paramString);
  
  <T> List<Map<String, Object>> findAliasData(String paramString);
  
  List<?> findAllSingalObject(String paramString);
  
  <T> List<T> findAllNamedQuery(String paramString1, String paramString2, List<?> paramList);
  
  String getSingleRecord(String paramString);
  
  List<Map<String, Object>> getMapData(String paramString);
  
  <T> List<Map<String, Object>> findAliasData(String paramString1, String paramString2, String paramString3);
  
  String deleteSqlQuiries(String paramString);
  
  long findIdBySqlQuery(String paramString);
  
  Float findIdByOneSqlQuery(String paramString);
  
  <T> List<T> findAllWithMaxRecord(T paramT, String paramString, int paramInt);
  
  <T> List<T> getUserByRole(T paramT, String paramString) throws UserNotFoundException;
  
  void excuteUpdateUserData(String paramString);
  
  String listOfStringData(String paramString);
  
  long getCountByUserId(Long paramLong) throws ServerException;
  
  User getUser(long paramLong) throws UserNotFoundException;
  
  int updateSqlQuery(String paramString);
  
  BigDecimal getSingleRecordofOrgId(String paramString);
  
  String findIdBySqlQueryString(String paramString);
  
  Map<String, Object> getMap_Data(String paramString);
   

	User findByEmail(String email)throws UserNotFoundException;
	
	User findUserByChargerId(String chargerId);
	
	Query createNativeQuery(String sql);
	
	List<Site> findAllOrg(Site site, String query);
	
	List<ChargingActivity> findByQuery(String string, Map<String, Object> params, Class<ChargingActivity> class1);
	
	<T> List<T> findAllByIds(Class<T> clazz, List<Long> ids);
	
	Map<String, Object> getSingleMapData(String query);
	<T> List<T> findAllHQLQueryPaginated(
		    T entity,
		    String hql,
		    int page,
		    int size,
		    Map<String, Object> parameters
		);
	
		Long countHQLQuery(
		    String hql,
		    Map<String, Object> parameters
		);
		
		<T> List<T> findAllHQLQry(T entity, String hql, Object... params) throws UserNotFoundException;
	
		
		<T> List<T> findByHQL(String hql, Map<String, Object> params);
}