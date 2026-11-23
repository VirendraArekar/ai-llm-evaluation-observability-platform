package com.llmops.platform.modules.provider.entity;

import com.llmops.platform.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "provider_credentials")
public class ProviderCredential extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false, unique = true)
    private LlmProvider provider;

    @Column(name = "encrypted_api_key", nullable = false, columnDefinition = "TEXT")
    private String encryptedApiKey;

    @Column(name = "key_masked", nullable = false, length = 50)
    private String keyMasked;

    @Column(name = "iv_salt", length = 100)
    private String ivSalt;

    public ProviderCredential() {}

    public LlmProvider getProvider() { return provider; }
    public void setProvider(LlmProvider provider) { this.provider = provider; }
    public String getEncryptedApiKey() { return encryptedApiKey; }
    public void setEncryptedApiKey(String encryptedApiKey) { this.encryptedApiKey = encryptedApiKey; }
    public String getKeyMasked() { return keyMasked; }
    public void setKeyMasked(String keyMasked) { this.keyMasked = keyMasked; }
    public String getIvSalt() { return ivSalt; }
    public void setIvSalt(String ivSalt) { this.ivSalt = ivSalt; }
}
