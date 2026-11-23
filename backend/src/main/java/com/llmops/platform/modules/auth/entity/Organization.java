package com.llmops.platform.modules.auth.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String plan = "ENTERPRISE";

    public Organization() {}

    public Organization(String name, String slug, String plan) {
        this.name = name;
        this.slug = slug;
        this.plan = plan != null ? plan : "ENTERPRISE";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
}
