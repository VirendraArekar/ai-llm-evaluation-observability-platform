package com.llmops.platform.modules.provider.client;

import java.util.Locale;

public class MockLlmResponseGenerator {

    public static String synthesize(String model, String userPrompt) {
        String query = userPrompt != null ? userPrompt.toLowerCase(Locale.ROOT) : "";

        if (query.contains("rate limiter") || query.contains("token bucket") || query.contains("concurrency")) {
            return """
            Here is an enterprise-grade, thread-safe Token Bucket Rate Limiter in Java 25 utilizing virtual threads:

            ```java
            package com.example.ratelimiter;

            import java.util.concurrent.locks.ReentrantLock;

            public final class TokenBucketRateLimiter {
                private final long capacity;
                private final double refillRatePerNano;
                private double availableTokens;
                private long lastRefillNanoTime;
                private final ReentrantLock lock = new ReentrantLock();

                public TokenBucketRateLimiter(long capacity, double tokensPerSecond) {
                    this.capacity = capacity;
                    this.refillRatePerNano = tokensPerSecond / 1_000_000_000.0;
                    this.availableTokens = capacity;
                    this.lastRefillNanoTime = System.nanoTime();
                }

                public boolean tryAcquire(int tokens) {
                    lock.lock();
                    try {
                        refill();
                        if (availableTokens >= tokens) {
                            availableTokens -= tokens;
                            return true;
                        }
                        return false;
                    } finally {
                        lock.unlock();
                    }
                }

                private void refill() {
                    long now = System.nanoTime();
                    double elapsed = (now - lastRefillNanoTime);
                    if (elapsed > 0) {
                        double delta = elapsed * refillRatePerNano;
                        this.availableTokens = Math.min(capacity, availableTokens + delta);
                        this.lastRefillNanoTime = now;
                    }
                }
            }
            ```

            ### Concurrency Architecture
            1. **Virtual Thread Friendly**: Uses standard `ReentrantLock` which unmounts virtual threads instead of pinning carrier threads.
            2. **Nanosecond Resolution**: Prevents token calculation drift under bursty distributed traffic.
            """;
        }

        if (query.contains("refund") || query.contains("billing") || query.contains("invoice")) {
            return """
            Hello,

            Thank you for reaching out to our support team. We sincerely apologize for any confusion regarding your recent billing statement.

            **Resolution Details:**
            1. We have verified your transaction and identified the duplicate charge on invoice.
            2. An automatic reversal of the amount has been initiated via your original payment method.
            3. Funds typically reflect in your account within **3 to 5 business days** depending on your financial institution.

            Your reference support ticket is **#REF-84920**. Please let us know if you require any further assistance!
            """;
        }

        if (query.contains("401") || query.contains("unauthorized") || query.contains("api key")) {
            return """
            ### Troubleshooting 401 Unauthorized Error

            The HTTP 401 response indicates missing or invalid client credentials. Follow these verification steps:

            1. **Header Format**: Ensure your request includes the exact header:
               ```http
               Authorization: Bearer <your_api_key>
               ```
               or for ingestion endpoints:
               ```http
               X-API-Key: ak_live_...
               ```
            2. **Key Expiration & Scopes**: Verify in **Settings -> API Keys** that your key has active `traces:write` permissions and has not passed its expiration date.
            3. **Environment**: Ensure you are pointing to the correct environment URL (`https://api.aetherllm.ai/v1`).
            """;
        }

        if (query.contains("sql") || query.contains("postgres") || query.contains("select") || query.contains("table")) {
            return """
            ```sql
            -- Optimized PostgreSQL 16 Telemetry Query
            SELECT 
                p.name AS project_name,
                COUNT(t.id) AS total_requests,
                ROUND(AVG(t.latency_ms), 2) AS avg_latency_ms,
                SUM(t.total_tokens) AS total_tokens,
                ROUND(SUM(t.estimated_cost), 4) AS total_cost_usd
            FROM projects p
            JOIN traces t ON t.project_id = p.id
            WHERE t.start_time >= NOW() - INTERVAL '7 days'
            GROUP BY p.name
            ORDER BY total_requests DESC;
            ```

            **Execution Plan Analysis:**
            - Utilizes index on `traces(project_id, start_time DESC)`.
            - Zero temporary buffer spilling; estimated runtime `< 4ms`.
            """;
        }

        if (query.contains("diabetes") || query.contains("metformin") || query.contains("medical")) {
            return """
            Based on clinical practice guidelines (ADA Standards of Medical Care):

            - **First-Line Pharmacotherapy**: Metformin remains the preferred initial pharmacological agent for the treatment of type 2 diabetes mellitus, provided there are no contraindications such as severe renal impairment (eGFR < 30 mL/min/1.73 m²).
            - **Combination Therapy**: When glycemic targets are not achieved after 3 months, consider dual therapy incorporating SGLT2 inhibitors or GLP-1 receptor agonists based on cardiovascular and renal risk profiles.
            """;
        }

        if (query.contains("why") || query.contains("how") || query.contains("explain") || query.contains("recommend")) {
            return """
            ### Comprehensive Analysis & Recommendation

            Based on observed telemetry and system parameters:

            1. **Core Findings**:
               - Request execution adheres to configured latency thresholds ($< 500\\text{ ms}$).
               - Token distribution shows approximately 65% prompt context allocation and 35% completion generation.
               - Grounded verification confirms zero hallucinated claims against retrieved documentation.

            2. **Actionable Recommendations**:
               - For high-throughput classification, consider migrating from baseline models to lower-cost specialized variants (e.g., `gpt-4o-mini` or `gemini-1.5-flash`) for up to **70% cost reduction**.
               - Cache repeated embedding lookups in Redis to eliminate redundant vector compute.
            """;
        }

        // Default intelligent fallback
        return """
        ### Response from %s

        Your request has been processed successfully with full telemetry logging.

        **Key Execution Highlights:**
        - **Model Architecture**: Validated against safety and semantic grounding filters.
        - **Inference Latency**: Fast time-to-first-token (TTFT) with deterministic decoding parameters.
        - **Observability**: Execution spans have been recorded in the trace waterfall with token and cost attribution.

        ```json
        {
          "status": "COMPLETED",
          "model": "%s",
          "verified": true,
          "faithfulnessScore": 0.98
        }
        ```
        """.formatted(model != null ? model : "AetherLLM Engine", model != null ? model : "gpt-4o-mini");
    }
}
