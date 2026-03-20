//package com.ewe.form;
//
//
//import java.util.List;
//
//import com.ewe.form.BecknSearchRequest.Context.Location;
//
//public class BecknSearchResponse {
//    private Context context;
//    private ResponseMessage message;
//
//    public Context getContext() {
//        return context;
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//    }
//
//    public ResponseMessage getMessage() {
//        return message;
//    }
//
//    public void setMessage(ResponseMessage message) {
//        this.message = message;
//    }
//
//    public static class Context {
//    	private String domain;
//        private String country;
//        private String city;
//        private String action;
//        private String coreVersion;
//        private String bapId;
//        private String bapUri;
//        private String transactionId;
//        private String messageId;
//        private String timestamp;
//        private String bppId;
//        private String bppUri;
//        private String key;
//        private String ttl;
//        private Location location;
//
//        // Getters and Setters
//        public String getDomain() { return domain; }
//        public void setDomain(String domain) { this.domain = domain; }
//        public String getCountry() { return country; }
//        public void setCountry(String country) { this.country = country; }
//        public String getCity() { return city; }
//        public void setCity(String city) { this.city = city; }
//        public String getAction() { return action; }
//        public void setAction(String action) { this.action = action; }
//        public String getCoreVersion() { return coreVersion; }
//        public void setCoreVersion(String coreVersion) { this.coreVersion = coreVersion; }
//        public String getBapId() { return bapId; }
//        public void setBapId(String bapId) { this.bapId = bapId; }
//        public String getBapUri() { return bapUri; }
//        public void setBapUri(String bapUri) { this.bapUri = bapUri; }
//        public String getTransactionId() { return transactionId; }
//        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
//        public String getMessageId() { return messageId; }
//        public void setMessageId(String messageId) { this.messageId = messageId; }
//        public String getTimestamp() { return timestamp; }
//        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
//        public String getBppId() { return bppId; }
//        public void setBppId(String bppId) { this.bppId = bppId; }
//        public String getBppUri() { return bppUri; }
//        public void setBppUri(String bppUri) { this.bppUri = bppUri; }
//        public String getKey() { return key; }
//        public void setKey(String key) { this.key = key; }
//        public String getTtl() { return ttl; }
//        public void setTtl(String ttl) { this.ttl = ttl; }
//        public Location getLocation() { return location; }
//        public void setLocation(Location location) { this.location = location; }
//    }
//
//    public static class ResponseMessage {
//        private Catalog catalog;
//
//        public Catalog getCatalog() {
//            return catalog;
//        }
//
//        public void setCatalog(Catalog catalog) {
//            this.catalog = catalog;
//        }
//
//        public static class Catalog {
//            private Descriptor descriptor;
//            private List<Provider> providers;
//
//            public Descriptor getDescriptor() {
//                return descriptor;
//            }
//
//            public void setDescriptor(Descriptor descriptor) {
//                this.descriptor = descriptor;
//            }
//
//            public List<Provider> getProviders() {
//                return providers;
//            }
//
//            public void setProviders(List<Provider> providers) {
//                this.providers = providers;
//            }
//
//            public static class Descriptor {
//                private String name;
//                private String code;
//                // Add other descriptor fields as needed
//
//                public String getName() {
//                    return name;
//                }
//
//                public void setName(String name) {
//                    this.name = name;
//                }
//
//                public String getCode() {
//                    return code;
//                }
//
//                public void setCode(String code) {
//                    this.code = code;
//                }
//            }
//
//            public static class Provider {
//                private String id;
//                private Descriptor descriptor;
//                private List<Location> locations;
//                private List<Item> items;
//                private List<Fulfillment> fulfillments;
//                private List<TagGroup> tags;
//                private Time time;
//
//                // Getters and setters for all fields
//                public String getId() { return id; }
//                public void setId(String id) { this.id = id; }
//                public Descriptor getDescriptor() { return descriptor; }
//                public void setDescriptor(Descriptor descriptor) { this.descriptor = descriptor; }
//                public List<Location> getLocations() { return locations; }
//                public void setLocations(List<Location> locations) { this.locations = locations; }
//                public List<Item> getItems() { return items; }
//                public void setItems(List<Item> items) { this.items = items; }
//                public List<Fulfillment> getFulfillments() { return fulfillments; }
//                public void setFulfillments(List<Fulfillment> fulfillments) { this.fulfillments = fulfillments; }
//                public List<TagGroup> getTags() { return tags; }
//                public void setTags(List<TagGroup> tags) { this.tags = tags; }
//                public Time getTime() { return time; }
//                public void setTime(Time time) { this.time = time; }
//
//                public static class Location {
//                    // Location fields and methods
//                    private String gps;
//                    private String city;
//                    private String country;
//
//                    public String getGps() { return gps; }
//                    public void setGps(String gps) { this.gps = gps; }
//                    public String getCity() { return city; }
//                    public void setCity(String city) { this.city = city; }
//                    public String getCountry() { return country; }
//                    public void setCountry(String country) { this.country = country; }
//                }
//
//                public static class Item {
//                    // Item fields and methods
//                    private String id;
//                    private Descriptor descriptor;
//
//                    public String getId() { return id; }
//                    public void setId(String id) { this.id = id; }
//                    public Descriptor getDescriptor() { return descriptor; }
//                    public void setDescriptor(Descriptor descriptor) { this.descriptor = descriptor; }
//                }
//
//                public static class Fulfillment {
//                    // Fulfillment fields and methods
//                    private String id;
//                    private String type;
//                    private State state;
//
//                    public String getId() { return id; }
//                    public void setId(String id) { this.id = id; }
//                    public String getType() { return type; }
//                    public void setType(String type) { this.type = type; }
//                    public State getState() { return state; }
//                    public void setState(State state) { this.state = state; }
//
//                    public static class State {
//                        private Descriptor descriptor;
//
//                        public Descriptor getDescriptor() { return descriptor; }
//                        public void setDescriptor(Descriptor descriptor) { this.descriptor = descriptor; }
//                    }
//                }
//
//                public static class TagGroup {
//                    // TagGroup fields and methods
//                    private String name;
//                    private List<Tag> list;
//
//                    public String getName() { return name; }
//                    public void setName(String name) { this.name = name; }
//                    public List<Tag> getList() { return list; }
//                    public void setList(List<Tag> list) { this.list = list; }
//
//                    public static class Tag {
//                        private String name;
//                        private String value;
//
//                        public String getName() { return name; }
//                        public void setName(String name) { this.name = name; }
//                        public String getValue() { return value; }
//                        public void setValue(String value) { this.value = value; }
//                    }
//                }
//
//                public static class Time {
//                    private String label;
//                    private String duration;
//                    private Range range;
//                    private String days;
//                    private String schedule;
//
//                    public String getLabel() { return label; }
//                    public void setLabel(String label) { this.label = label; }
//                    public String getDuration() { return duration; }
//                    public void setDuration(String duration) { this.duration = duration; }
//                    public Range getRange() { return range; }
//                    public void setRange(Range range) { this.range = range; }
//                    public String getDays() { return days; }
//                    public void setDays(String days) { this.days = days; }
//                    public String getSchedule() { return schedule; }
//                    public void setSchedule(String schedule) { this.schedule = schedule; }
//
//                    public static class Range {
//                        private String start;
//                        private String end;
//
//                        public String getStart() { return start; }
//                        public void setStart(String start) { this.start = start; }
//                        public String getEnd() { return end; }
//                        public void setEnd(String end) { this.end = end; }
//                    }
//                }
//            }
//        }
//    }
//}