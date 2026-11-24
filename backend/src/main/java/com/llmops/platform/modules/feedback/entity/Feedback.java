package com.llmops.platform.modules.feedback.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Project;
import com.llmops.platform.modules.auth.entity.User;
import com.llmops.platform.modules.tracing.entity.Span;
import com.llmops.platform.modules.tracing.entity.Trace;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trace_id")
    private Trace trace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "span_id")
    private Span span;

    @Column(length = 50, nullable = false)
    private String rating; // THUMBS_UP, THUMBS_DOWN, 1_STAR, 5_STARS

    @Column(name = "user_comment", columnDefinition = "TEXT")
    private String userComment;

    @Column(length = 500)
    private String tags;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Feedback() {}

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public Trace getTrace() { return trace; }
    public void setTrace(Trace trace) { this.trace = trace; }
    public Span getSpan() { return span; }
    public void setSpan(Span span) { this.span = span; }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getUserComment() { return userComment; }
    public void setUserComment(String userComment) { this.userComment = userComment; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
