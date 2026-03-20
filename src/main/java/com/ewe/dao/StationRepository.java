//package com.ewe.dao;
//
//package com.ewe.repository;
//
//import com.ewe.model.ChargingStation;
//import org.locationtech.jts.geom.Point;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import java.util.List;
//
//public interface StationRepository extends JpaRepository<ChargingStation, Long> {
//
//    @Query("SELECT s FROM ChargingStation s WHERE " +
//           "(:connectorTypes IS NULL OR EXISTS (" +
//           "   SELECT p FROM s.ports p WHERE " +
//           "   LOWER(p.connectorType) IN (LOWER(:connectorTypes)) AND " +
//           "   (:availableNow = FALSE OR EXISTS (" +
//           "       SELECT n FROM p.statusNotifications n WHERE n.status = 'Available'" +
//           "   )" +
//           ")) AND " +
//           "(:minPower IS NULL OR EXISTS (" +
//           "   SELECT p FROM s.ports p WHERE p.maxPowerKw >= :minPower" +
//           ")) AND " +
//           "(:maxPrice IS NULL OR EXISTS (" +
//           "   SELECT p FROM s.ports p WHERE p.billingAmount <= :maxPrice" +
//           ")) AND " +
//           "(:center IS NULL OR ST_Distance_Sphere(s.location, :center) <= :radius) AND " +
//           "s.stationStatus = 'Active'")
//    Page<ChargingStation> searchStations(
//        @Param("center") Point center,
//        @Param("radius") Double radius,
//        @Param("connectorTypes") List<String> connectorTypes,
//        @Param("minPower") Double minPower,
//        @Param("maxPrice") Double maxPrice,
//        @Param("availableNow") Boolean availableNow,
//        Pageable pageable);
//}
