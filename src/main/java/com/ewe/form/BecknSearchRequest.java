//package com.ewe.form;
//
//import java.util.List;
//
//public class BecknSearchRequest {
//    private Context context;
//    private Message message;
//
//    public Context getContext() {
//        return context;
//    }
//
//    public void setContext(Context context) {
//        this.context = context;
//    }
//
//    public Message getMessage() {
//        return message;
//    }
//
//    public void setMessage(Message message) {
//        this.message = message;
//    }
//
//    public static class Context {
//        private String domain;
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
//
//        public static class Location {
//            private String id;
//            private String gps;
//            private String city;
//            private String country;
//            private String areaCode;
//            private Double radius;
//
//            // Getters and Setters
//            public String getId() { return id; }
//            public void setId(String id) { this.id = id; }
//            public String getGps() { return gps; }
//            public void setGps(String gps) { this.gps = gps; }
//            public String getCity() { return city; }
//            public void setCity(String city) { this.city = city; }
//            public String getCountry() { return country; }
//            public void setCountry(String country) { this.country = country; }
//            public String getAreaCode() { return areaCode; }
//            public void setAreaCode(String areaCode) { this.areaCode = areaCode; }
//            public Double getRadius() { return radius; }
//            public void setRadius(Double radius) { this.radius = radius; }
//        }
//    }
//
//    public static class Message {
//        private Intent intent;
//        private SearchCriteria criteria;
//
//        // Getters and Setters
//        public Intent getIntent() { return intent; }
//        public void setIntent(Intent intent) { this.intent = intent; }
//        public SearchCriteria getCriteria() { return criteria; }
//        public void setCriteria(SearchCriteria criteria) { this.criteria = criteria; }
//
//        public static class Intent {
//            private Item item;
//            private Fulfillment fulfillment;
//            private Location location;
//
//            // Getters and Setters
//            public Item getItem() { return item; }
//            public void setItem(Item item) { this.item = item; }
//            public Fulfillment getFulfillment() { return fulfillment; }
//            public void setFulfillment(Fulfillment fulfillment) { this.fulfillment = fulfillment; }
//            public Location getLocation() { return location; }
//            public void setLocation(Location location) { this.location = location; }
//
//            public static class Item {
//                private Descriptor descriptor;
//                private List<TagGroup> tags;
//
//                // Getters and Setters
//                public Descriptor getDescriptor() { return descriptor; }
//                public void setDescriptor(Descriptor descriptor) { this.descriptor = descriptor; }
//                public List<TagGroup> getTags() { return tags; }
//                public void setTags(List<TagGroup> tags) { this.tags = tags; }
//
//                public static class Descriptor {
//                    private String name;
//                    private String code;
//                    private List<String> images;
//
//                    // Getters and Setters
//                    public String getName() { return name; }
//                    public void setName(String name) { this.name = name; }
//                    public String getCode() { return code; }
//                    public void setCode(String code) { this.code = code; }
//                    public List<String> getImages() { return images; }
//                    public void setImages(List<String> images) { this.images = images; }
//                }
//
//                public static class TagGroup {
//                    private String name;
//                    private List<Tag> list;
//
//                    // Getters and Setters
//                    public String getName() { return name; }
//                    public void setName(String name) { this.name = name; }
//                    public List<Tag> getList() { return list; }
//                    public void setList(List<Tag> list) { this.list = list; }
//
//                    public static class Tag {
//                        private String name;
//                        private String value;
//
//                        // Getters and Setters
//                        public String getName() { return name; }
//                        public void setName(String name) { this.name = name; }
//                        public String getValue() { return value; }
//                        public void setValue(String value) { this.value = value; }
//                    }
//                }
//            }
//
//            public static class Fulfillment {
//                private String type;
//                private State state;
//                private List<Contact> contacts;
//
//                // Getters and Setters
//                public String getType() { return type; }
//                public void setType(String type) { this.type = type; }
//                public State getState() { return state; }
//                public void setState(State state) { this.state = state; }
//                public List<Contact> getContacts() { return contacts; }
//                public void setContacts(List<Contact> contacts) { this.contacts = contacts; }
//
//                public static class State {
//                    private Descriptor descriptor;
//
//                    // Getters and Setters
//                    public Descriptor getDescriptor() { return descriptor; }
//                    public void setDescriptor(Descriptor descriptor) { this.descriptor = descriptor; }
//                }
//
//                public static class Contact {
//                    private String phone;
//                    private String email;
//
//                    // Getters and Setters
//                    public String getPhone() { return phone; }
//                    public void setPhone(String phone) { this.phone = phone; }
//                    public String getEmail() { return email; }
//                    public void setEmail(String email) { this.email = email; }
//                }
//            }
//        }
//
//        public static class SearchCriteria {
//            private List<String> connectorTypes;
//            private Double minPowerKw;
//            private Double maxPricePerKwh;
//            private Boolean availableNow;
//
//            // Getters and Setters
//            public List<String> getConnectorTypes() { return connectorTypes; }
//            public void setConnectorTypes(List<String> connectorTypes) { this.connectorTypes = connectorTypes; }
//            public Double getMinPowerKw() { return minPowerKw; }
//            public void setMinPowerKw(Double minPowerKw) { this.minPowerKw = minPowerKw; }
//            public Double getMaxPricePerKwh() { return maxPricePerKwh; }
//            public void setMaxPricePerKwh(Double maxPricePerKwh) { this.maxPricePerKwh = maxPricePerKwh; }
//            public Boolean getAvailableNow() { return availableNow; }
//            public void setAvailableNow(Boolean availableNow) { this.availableNow = availableNow; }
//        }
//    }
//}