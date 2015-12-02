package com.payleven.hmac;

public class NotificationHmacBuilder extends HmacBuilder {

    private String requestMethod;
    private String requestUrl;
    private String applicationId;
    private String messageId;

    private NotificationHmacBuilder(String notificationBody, long timestamp) {
        super(notificationBody, timestamp);
    }


    public static NotificationHmacBuilder newBuilder(String notificationBody, String timestamp) {
        return new NotificationHmacBuilder(notificationBody, Long.valueOf(timestamp));
    }

    public NotificationHmacBuilder appendRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public NotificationHmacBuilder appendRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public NotificationHmacBuilder appendApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public NotificationHmacBuilder appendMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    @Override
    protected void buildNormalizedString(StringBuilder normalizedString) {


        normalizedString.append(requestMethod).append(NEW_LINE)
                .append(requestUrl).append(NEW_LINE)
                .append(applicationId).append(NEW_LINE)
                .append(messageId).append(NEW_LINE)
                .append(getComputedHttpBodyHash()).append(NEW_LINE)
                .append(getTimestamp());
    }

}