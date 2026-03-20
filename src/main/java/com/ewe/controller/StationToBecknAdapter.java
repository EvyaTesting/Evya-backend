//package com.ewe.controller;
//
//
//
//import com.ewe.beckn.dto.request.*;
//import com.ewe.beckn.dto.response.*;
//import com.ewe.form.BecknSearchResponse;
//import com.ewe.model.*;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//public class StationToBecknAdapter {
//
//    public BecknSearchResponse convertToBecknResponse(
//            List<ChargingStation> stations, 
//            Context context,
//            BecknSearchService.SearchParameters params) {
//        
//        BecknSearchResponse response = new BecknSearchResponse();
//        response.setContext(context);
//        
//        Catalog catalog = new Catalog();
//        catalog.setDescriptor(createDescriptor("EV Charging Network"));
//        catalog.setProviders(stations.stream()
//            .map(station -> convertToProvider(station, params))
//            .collect(Collectors.toList()));
//        
//        response.setMessage(new ResponseMessage(catalog));
//        return response;
//    }
//
//    private Provider convertToProvider(ChargingStation station, 
//                                    BecknSearchService.SearchParameters params) {
//        Provider provider = new Provider();
//        provider.setId(station.getOcppId());
//        provider.setDescriptor(createDescriptor(station.getStationName()));
//        provider.setLocations(createLocations(station));
//        provider.setItems(createItems(station, params));
//        provider.setFulfillments(createFulfillments(station));
//        provider.setTags(createTags(station));
//        provider.setTime(createOperatingTime(station.getSite()));
//        
//        return provider;
//    }
//
//    private List<Location> createLocations(ChargingStation station) {
//        Location location = new Location();
//        location.setGps(station.getLatitude() + "," + station.getLongitude());
//        location.setCity(station.getSite().getCity());
//        location.setCountry(station.getSite().getCountry());
//        return List.of(location);
//    }
//
//    private List<Item> createItems(ChargingStation station, 
//                                 BecknSearchService.SearchParameters params) {
//        return station.getPorts().stream()
//            .filter(port -> matchesSearchCriteria(port, params))
//            .map(this::createItem)
//            .collect(Collectors.toList());
//    }
//
//    private boolean matchesSearchCriteria(Port port, 
//                                       BecknSearchService.SearchParameters params) {
//        // Filter by connector type if specified
//        if (params.getConnectorTypes() != null && !params.getConnectorTypes().isEmpty()) {
//            if (!params.getConnectorTypes().stream()
//                .anyMatch(type -> type.equalsIgnoreCase(port.getConnectorType()))) {
//                return false;
//            }
//        }
//        
//        // Filter by min power if specified
//        if (params.getMinPower() != null && 
//            (port.getMaxPowerKw() == null || port.getMaxPowerKw() < params.getMinPower())) {
//            return false;
//        }
//        
//        // Filter by max price if specified
//        if (params.getMaxPrice() != null && 
//            (port.getBillingAmount() == null || port.getBillingAmount() > params.getMaxPrice())) {
//            return false;
//        }
//        
//        // Filter by availability if requested
//        if (params.isAvailableNow() != null && params.isAvailableNow()) {
//            if (port.getStatusNotifications().isEmpty() || 
//                !port.getStatusNotifications().get(0).getStatus().equalsIgnoreCase("Available")) {
//                return false;
//            }
//        }
//        
//        return true;
//    }
//
//    private Item createItem(Port port) {
//        Item item = new Item();
//        item.setId("connector-" + port.getConnectorId());
//        
//        Descriptor descriptor = new Descriptor();
//        descriptor.setName(port.getConnectorType() + " Connector");
//        descriptor.setCode(port.getConnectorType());
//        item.setDescriptor(descriptor);
//        
//        // Add pricing information
//        item.setPrice(createPrice(port));
//        
//        // Add technical specifications
//        item.setTags(createItemTags(port));
//        
//        return item;
//    }
//
//    private List<Fulfillment> createFulfillments(ChargingStation station) {
//        return station.getPorts().stream()
//            .map(port -> {
//                Fulfillment fulfillment = new Fulfillment();
//                fulfillment.setType("EV_CHARGING");
//                fulfillment.setId("fulfill-" + station.getId() + "-" + port.getId());
//                
//                State state = new State();
//                Descriptor status = new Descriptor();
//                status.setName(getCurrentStatus(port));
//                state.setDescriptor(status);
//                fulfillment.setState(state);
//                
//                // Add contact information
//                fulfillment.setContacts(createContacts(station.getSite()));
//                
//                return fulfillment;
//            })
//            .collect(Collectors.toList());
//    }
//
//    private String getCurrentStatus(Port port) {
//        if (port.getStatusNotifications().isEmpty()) {
//            return "UNKNOWN";
//        }
//        return port.getStatusNotifications().get(0).getStatus();
//    }
//
//    private List<TagGroup> createTags(ChargingStation station) {
//        List<TagGroup> tags = new ArrayList<>();
//        
//        // Add station capabilities
//        TagGroup capabilities = new TagGroup();
//        capabilities.setName("capabilities");
//        capabilities.setList(List.of(
//            createTag("v2g_support", station.getV2gSupport().toString()),
//            createTag("plug_and_charge", "false") // Update as needed
//        ));
//        tags.add(capabilities);
//        
//        return tags;
//    }
//
//    private Time createOperatingTime(Site site) {
//        Time time = new Time();
//        time.setLabel("Operating Hours");
//        time.setDays("All days");
//        time.setSchedule("24/7"); // Update with actual schedule from site if available
//        return time;
//    }
//
//    // Helper methods
//    private Descriptor createDescriptor(String name) {
//        Descriptor descriptor = new Descriptor();
//        descriptor.setName(name);
//        return descriptor;
//    }
//    
//    private Tag createTag(String name, String value) {
//        Tag tag = new Tag();
//        tag.setName(name);
//        tag.setValue(value);
//        return tag;
//    }
//    
//    // ... other helper methods ...
//}
