//package com.ewe.service;
//
//package com.ewe.beckn.service;
//
//import com.ewe.beckn.dto.request.*;
//import com.ewe.beckn.dto.response.*;
//import com.ewe.model.ChargingStation;
//import com.ewe.repository.StationRepository;
//import lombok.RequiredArgsConstructor;
//import org.locationtech.jts.geom.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class BecknSearchService {
//
//    private final StationRepository stationRepository;
//    private final StationToBecknAdapter adapter;
//    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//
//    @Value("${beckn.default.search.radius.km:10}")
//    private double defaultSearchRadiusKm;
//
//    @Value("${beckn.default.page.size:10}")
//    private int defaultPageSize;
//
//    public BecknSearchResponse handleSearchRequest(BecknSearchRequest request) {
//        // Extract search parameters
//        SearchParameters params = extractSearchParameters(request);
//        
//        // Perform search
//        Page<ChargingStation> stations = stationRepository.searchStations(
//            params.getCenter(),
//            params.getRadius(),
//            params.getConnectorTypes(),
//            params.getMinPower(),
//            params.getMaxPrice(),
//            params.isAvailableNow(),
//            PageRequest.of(0, defaultPageSize)
//        );
//        
//        // Convert to Beckn response
//        return adapter.convertToBecknResponse(
//            stations.getContent(),
//            request.getContext(),
//            params
//        );
//    }
//
//    private SearchParameters extractSearchParameters(BecknSearchRequest request) {
//        SearchParameters params = new SearchParameters();
//        
//        // Location parameters
//        if (request.getMessage().getIntent().getLocation() != null) {
//            params.setCenter(parseLocation(
//                request.getMessage().getIntent().getLocation().getGps()));
//            params.setRadius(parseRadius(
//                request.getMessage().getIntent().getLocation().getRadius()));
//        }
//        
//        // Connector types
//        if (request.getMessage().getCriteria() != null) {
//            params.setConnectorTypes(request.getMessage().getCriteria().getConnectorTypes());
//        } else if (request.getMessage().getIntent().getItem() != null) {
//            params.setConnectorTypes(List.of(
//                request.getMessage().getIntent().getItem().getDescriptor().getName()));
//        }
//        
//        // Other criteria
//        if (request.getMessage().getCriteria() != null) {
//            params.setMinPower(request.getMessage().getCriteria().getMinPowerKw());
//            params.setMaxPrice(request.getMessage().getCriteria().getMaxPricePerKwh());
//            params.setAvailableNow(request.getMessage().getCriteria().getAvailableNow());
//        }
//        
//        return params;
//    }
//
//    private Point parseLocation(String gps) {
//        if (gps == null || gps.isEmpty()) return null;
//        
//        try {
//            String[] coords = gps.split(",");
//            double lat = Double.parseDouble(coords[0]);
//            double lon = Double.parseDouble(coords[1]);
//            return geometryFactory.createPoint(new Coordinate(lon, lat));
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private Double parseRadius(Double radius) {
//        return radius != null ? radius * 1000 : defaultSearchRadiusKm * 1000;
//    }
//    
//    @Data
//    private static class SearchParameters {
//        private Point center;
//        private Double radius;
//        private List<String> connectorTypes;
//        private Double minPower;
//        private Double maxPrice;
//        private Boolean availableNow;
//    }
//}