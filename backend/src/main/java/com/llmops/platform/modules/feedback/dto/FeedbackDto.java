package com.llmops.platform.modules.feedback.dto;

import java.time.Instant;
import java.util.UUID;

public class FeedbackDto {
    private UUID id;
    private UUID projectId;
    private String traceId;
    private String spanId;
    private String rating; // THUMBS_UP, THUMBS_DOWN, 1_STAR, 5_STARS
    private String userComment;
    private String tags;
    private String createdByName;
    private Instant createdAt;

    public FeedbackDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getSpanId() { return spanId; }
    public void setSpanId(String spanId) { this.spanId = spanId; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getUserComment() { return userComment; }
    public void setUserComment(String userComment) { this.userComment = userComment; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class CreateFeedbackRequest {
        private String traceId;
        private String spanId;
        private String rating;
        private String userComment;
        private String tags;

        public CreateFeedbackRequest() {}

        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public String getSpanId() { return spanId; }
        public void setSpanId(String spanId) { this.spanId = spanId; }
        public String getRating() { return rating; }
        public void setRating(String rating) { this.rating = rating; }
        public String getUserComment() { return userComment; }
        public void setUserComment(String userComment) { this.userComment = userComment; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
    }

    public static class FeedbackStatsDto {
        private long totalFeedback;
        private long thumbsUpCount;
        private long thumbsDownCount;
        private double positiveRatio;

        public FeedbackStatsDto() {}

        public long getTotalFeedback() { return totalFeedback; }
        public void setTotalFeedback(long totalFeedback) { this.totalFeedback = totalFeedback; }
        public long getThumbsUpCount() { return thumbsUpCount; }
        public void setThumbsUpCount(long thumbsUpCount) { this.thumbsUpCount = thumbsUpCount; }
        public long getThumbsDownCount() { return thumbsDownCount; }
        public void setThumbsDownCount(long thumbsDownCount) { this.thumbsDownCount = thumbsDownCount; }
        public double getPositiveRatio() { return positiveRatio; }
        public void setPositiveRatio(double positiveRatio) { this.positiveRatio = positiveRatio; }
    }
}
