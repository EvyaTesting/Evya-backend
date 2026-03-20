package com.ewe.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.ewe.controller.DashboardController;
import com.ewe.dao.GeneralDao;
import com.ewe.service.DashboardService;

@Service
@Scope("request")
public class DashboardServiceImpl implements DashboardService{

	@Autowired
	private GeneralDao<?, ?> generalDao;
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
	
//	@Override
//	public List<Map<String, Object>> getStats(int orgId, LocalDate startDate, LocalDate endDate) {
//	    if (orgId < 1) {
//	        throw new IllegalArgumentException("Invalid organization ID");
//	    }
//
//	    String dateFilter = "";
////	    if (startDate != null && endDate != null) {
////	        dateFilter = String.format(" AND ca.activityDate BETWEEN '%s' AND '%s' ", startDate, endDate);
////	    }
//	    
//	    if (startDate != null && endDate != null) {
//	        dateFilter = String.format(" AND ca.startTime >= '%s 00:00:00' AND ca.endTime <= '%s 23:59:59'", startDate, endDate);
//	    }
//
//
//	    String query = String.format(
//	        "SELECT " +
//	        "   COALESCE(ROUND(SUM(ca.kwConsuption), 2), 0)/1000 AS totalKWH, " +
//	        "   COALESCE(ROUND(SUM(ca.energyDelivered), 2), 0)/1000 AS totalEnergy, " +
//	        "   COALESCE(ROUND(SUM(ca.revenue), 2), 0) AS totalRevenue, " +
//	        "   ( " +
//	        "       SELECT COUNT(DISTINCT ur.user_id) FROM usersinroles ur " +
//	        "       WHERE ur.role_id = 2 " +
//	        (orgId == 1 ? "" :
//	        "AND ur.user_id IN ( " +
//	        "   SELECT uo.user_id FROM users_in_owners uo " +
//	        "   JOIN owner_orgs oo ON uo.Owner_org_id = oo.id " +
//	        "   WHERE oo.whitelabelId = %d OR uo.Owner_org_id = %d " +
//	        ") ").formatted(orgId, orgId) +
//	        "   ) AS totalEVUsers, " +
//	        "   COUNT(*) AS totalSessions " +
//	        "FROM chargingActivity ca " +
//	        "WHERE " +
//	        (orgId == 1
//	            ? "1=1 " + dateFilter
//	            : String.format("ca.ownerId IN (SELECT id FROM owner_orgs WHERE whitelabelId = %d OR id = %d) %s", 
//	                          orgId, orgId, dateFilter))
//	    );
//
//	    return generalDao.findAliasData(query);
//	}
	
	@Override
	public List<Map<String, Object>> getStats(int orgId, LocalDate startDate, LocalDate endDate) {
	    if (orgId < 1) {
	        throw new IllegalArgumentException("Invalid organization ID");
	    }
	    String dateFilter = "";
//	    if (startDate != null && endDate != null) {
//	        dateFilter = String.format(" AND ca.startTime BETWEEN '%s' AND '%s' ", startDate, endDate);
//	    }
	    if (startDate != null && endDate != null) {

	        LocalDateTime startDateTime = startDate.atStartOfDay();
	        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        String start = startDateTime.format(formatter);
	        String end = endDateTime.format(formatter);

	        dateFilter = String.format(
	            " AND ca.startTime >= '%s' AND ca.startTime < '%s' ",
	            start,
	            end
	        );
	    }
	    String query = String.format(
	        "SELECT " +
	        "   COALESCE(ROUND(SUM(ca.kwConsuption), 2), 0)/1000 AS totalKWH, " +
	        "   COALESCE(ROUND(SUM(ca.energyDelivered), 2), 0)/1000 AS totalEnergy, " +
	        "   COALESCE(ROUND(SUM(ca.revenue), 2), 0) AS totalRevenue, " +
	        "   ( " +
	        "       SELECT COUNT(DISTINCT u.id) FROM users u " +
	        "       JOIN usersinroles ur ON u.id = ur.user_id " +
	        "       LEFT JOIN users_in_owners uo ON u.id = uo.user_id " +
	        "       LEFT JOIN owner_orgs oo ON uo.Owner_org_id = oo.id " +
	        "       WHERE ur.role_id = 2 " +
	        (orgId == 1 ? "" :
	        "       AND (oo.whitelabelId = %d OR uo.Owner_org_id = %d OR u.orgId = %d) ").formatted(orgId, orgId, orgId) +
	        "   ) AS totalEVUsers, " +
	        "   COUNT(*) AS totalSessions " +
	        "FROM chargingActivity ca " +
	        "WHERE " +
	        (orgId == 1
	            ? "1=1 " + dateFilter
	            : String.format("ca.ownerId IN (SELECT id FROM owner_orgs WHERE whitelabelId = %d OR id = %d) %s", 
	                          orgId, orgId, dateFilter))
	    );
	    return generalDao.findAliasData(query);
	}
	
	@Override
public List<Map<String, Object>> getRevenueGraph(int orgId, LocalDate startDate, LocalDate endDate) {
    if (orgId < 1) {
        throw new IllegalArgumentException("Invalid organization ID");
    }

    String dateFilter = "";
    if (startDate != null && endDate != null) {
        dateFilter = String.format(" AND s.startTime >= '%s 00:00:00' AND s.endTime <= '%s 23:59:59'\r\n"
        		+ " ", startDate, endDate);
    }

    String withClause = orgId == 1 ? "" : String.format(
        "WITH related_orgs AS ( " +
        "    SELECT id AS owner_org_id " +
        "    FROM owner_orgs " +
        "    WHERE whitelabelId = %d OR id = %d " +
        ") ", orgId, orgId);

    String query = String.format(
        "%s " +  
        "SELECT " +
        "    DATEPART(year, s.startTime) AS [year],\r\n"
        + "mo.monthName AS [time],\r\n"
        + "ISNULL(SUM(s.energyDelivered)/1000, 0) AS [energy],\r\n"
        + "ISNULL(SUM(s.revenue)/1000, 0) AS [revenue]\r\n"
        + "FROM chargingActivity s\r\n"
        + "RIGHT JOIN month mo ON mo.id = DATEPART(month, s.startTime)\r\n"
        + "    AND DATEPART(year, s.startTime) = DATEPART(year, GETDATE())\r\n"
        + " " +
        "WHERE " +
        (orgId == 1
            ? "1=1"
            : "s.ownerId IN (SELECT owner_org_id FROM related_orgs)") +
        dateFilter +
        " GROUP BY mo.monthName, mo.id, DATEPART(year, s.startTime) "
        + " " +
        " ORDER BY mo.id",
        withClause
    );

    return generalDao.findAliasData(query);
}

                       //..........sessions.........
	@Override
	public List<Map<String, Object>> getSessionGraph(int orgId, LocalDate startDate, LocalDate endDate) {
	    if (orgId < 1) {
	        throw new IllegalArgumentException("Invalid organization ID");
	    }

	    String dateFilter = "";
	    if (startDate != null && endDate != null) {
	        dateFilter = String.format(" AND ca.startTime >= '%s 00:00:00' AND ca.endTime <= '%s 23:59:59'\r\n"
	        		+ " ", startDate, endDate);
	    }

	    String withClause = orgId == 1 ? "" : String.format(
	        "WITH related_orgs AS ( " +
	        "   SELECT id AS owner_org_id FROM owner_orgs WHERE whitelabelId = %d OR id = %d " +  // Added OR condition
	        ") ", orgId, orgId
	    );

	    String query = String.format(
	        "%s " + 
	        "SELECT " +
	        "   mo.monthName AS time, " +
	        "   COUNT(ca.id) AS value " +
	        "FROM month mo " +
	        "LEFT JOIN chargingActivity ca ON mo.id = DATEPART(MONTH, ca.startTime)\r\n"
	        + "   AND DATEPART(YEAR, ca.startTime) = DATEPART(YEAR, GETDATE())\r\n"
	        + " " +
	        "   %s " + // Date filter
	        "   %s " + // Org filter
	        "GROUP BY mo.id, mo.monthName " +
	        "ORDER BY mo.id",
	        withClause,
	        dateFilter,
	        (orgId == 1 ? "" : "AND ca.ownerId IN (SELECT owner_org_id FROM related_orgs)")
	    );

	    return generalDao.findAliasData(query);
	}
	@Override
	public List<Map<String, Object>> getStationStats(int orgId, LocalDate startDate, LocalDate endDate) {
	    String dateFilter = "";
	    if (startDate != null && endDate != null) {
	        dateFilter = String.format(" AND s.lastHeartBeat BETWEEN '%s' AND '%s' ", startDate, endDate);
	    }

	    String query = String.format(
	        "WITH related_orgs AS ( " +
	        "    SELECT DISTINCT id FROM owner_orgs " +
	        "    WHERE %d = 1 OR whitelabelId = %d OR id = %d " +  // Combined all three cases
	        "), " +
	        "org_sites AS ( " +
	        "    SELECT id FROM site " +
	        "    WHERE ownerOrg IN (SELECT id FROM related_orgs) " +  // Simplified condition
	        "), " +
	        "station_stats AS ( " +
	        "    SELECT s.stationStatus, COUNT(*) AS count " +
	        "    FROM station s " +
	        "    WHERE s.site_id IN (SELECT id FROM org_sites) " +  // Simplified condition
	        "    %s " +  // Date filter
	        "    GROUP BY s.stationStatus " +
	        ") " +
	        "SELECT 'Active' AS status, COALESCE((SELECT count FROM station_stats WHERE stationStatus = 'Active'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Inactive' AS status, COALESCE((SELECT count FROM station_stats WHERE stationStatus = 'Inactive'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Maintenance' AS status, COALESCE((SELECT count FROM station_stats WHERE stationStatus = 'Maintenance'), 0) AS count",
	        orgId, orgId, orgId, dateFilter
	    );

	    return generalDao.findAliasData(query);
	}

	@Override
	public List<Map<String, Object>> getPortStats(int orgId, LocalDate startDate, LocalDate endDate) {
	    String dateFilter = "";
	    if (startDate != null && endDate != null) {
	        dateFilter = String.format(" AND lastContactedTime BETWEEN '%s' AND '%s' ", startDate, endDate);
	    }

	    String query = String.format(
	        "WITH related_orgs AS ( " +
	        "    SELECT DISTINCT id FROM owner_orgs " +
	        "    WHERE %d = 1 OR whitelabelId = %d OR id = %d " +  // Combined all three cases
	        "), " +
	        "org_sites AS ( " +
	        "    SELECT id FROM site " +
	        "    WHERE ownerOrg IN (SELECT id FROM related_orgs) " +  // Simplified condition
	        "), " +
	        "org_stations AS ( " +
	        "    SELECT id FROM station " +
	        "    WHERE site_id IN (SELECT id FROM org_sites) " +  // Simplified condition
	        "), " +
	        "port_stats AS ( " +
	        "    SELECT status, COUNT(*) AS count " +
	        "    FROM statusNotification " +
	        "    WHERE stationId IN (SELECT id FROM org_stations) " +
	        "    %s " + // Date filter applied here
	        "    GROUP BY status " +
	        ") " +
	        "SELECT 'Available' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'Available'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Charging' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'Charging'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Inoperative' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'Inoperative'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Reserved' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'reserved'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Blocked' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'blocked'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Planned' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'Planned'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'Removed' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'Removed'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'SuspendedEVSE' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'SuspendedEVSE'), 0) AS count " +
	        "UNION ALL " +
	        "SELECT 'SuspendedEV' AS status, COALESCE((SELECT count FROM port_stats WHERE status = 'suspendedEV'), 0) AS count",
	        orgId, orgId, orgId, dateFilter
	    );

	    return generalDao.findAliasData(query);
	}
}