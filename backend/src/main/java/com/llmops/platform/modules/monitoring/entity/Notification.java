package com.llmops.platform.modules.monitoring.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.auth.entity.Organization;
import com.llmops.platform.modules.auth.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(length = 50, nullable = false)
    private String type = "SYSTEM"; // ALERT, EXPERIMENT_DONE, SYSTEM

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "link_url", length = 500)
    private String linkUrl;

    public Notification() {}

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }
}
