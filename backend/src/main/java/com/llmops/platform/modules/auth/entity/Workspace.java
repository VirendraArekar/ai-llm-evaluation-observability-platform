package com.llmops.platform.modules.auth.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "workspaces")
public class Workspace extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Workspace() {}

    public Workspace(Organization organization, String name, String slug, String description) {
        this.organization = organization;
        this.name = name;
        this.slug = slug;
        this.description = description;
    }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
