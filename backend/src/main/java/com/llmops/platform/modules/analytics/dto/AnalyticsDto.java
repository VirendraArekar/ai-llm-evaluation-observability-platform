package com.llmops.platform.modules.analytics.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsDto {

    public static class OverviewMetricsDto {
        private long totalRequests;
        private double averageLatencyMs;
        private long totalTokens;
        private BigDecimal totalCost;
        private double evaluationScore;
        private double hallucinationRate;
        private double errorRate;
        private int activeModels;

        public OverviewMetricsDto() {}

        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        public double getAverageLatencyMs() { return averageLatencyMs; }
        public void setAverageLatencyMs(double averageLatencyMs) { this.averageLatencyMs = averageLatencyMs; }
        public long getTotalTokens() { return totalTokens; }
        public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        public double getEvaluationScore() { return evaluationScore; }
        public void setEvaluationScore(double evaluationScore) { this.evaluationScore = evaluationScore; }
        public double getHallucinationRate() { return hallucinationRate; }
        public void setHallucinationRate(double hallucinationRate) { this.hallucinationRate = hallucinationRate; }
        public double getErrorRate() { return errorRate; }
        public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
        public int getActiveModels() { return activeModels; }
        public void setActiveModels(int activeModels) { this.activeModels = activeModels; }
    }

    public static class CostAnalyticsDto {
        private BigDecimal totalCost;
        private BigDecimal projectedMonthlyCost;
        private Map<String, BigDecimal> costByModel;
        private List<TimeSeriesPoint> dailyCostTrend;
        private List<TimeSeriesPoint> dailyTokenTrend;

        public CostAnalyticsDto() {}

        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        public BigDecimal getProjectedMonthlyCost() { return projectedMonthlyCost; }
        public void setProjectedMonthlyCost(BigDecimal projectedMonthlyCost) { this.projectedMonthlyCost = projectedMonthlyCost; }
        public Map<String, BigDecimal> getCostByModel() { return costByModel; }
        public void setCostByModel(Map<String, BigDecimal> costByModel) { this.costByModel = costByModel; }
        public List<TimeSeriesPoint> getDailyCostTrend() { return dailyCostTrend; }
        public void setDailyCostTrend(List<TimeSeriesPoint> dailyCostTrend) { this.dailyCostTrend = dailyCostTrend; }
        public List<TimeSeriesPoint> getDailyTokenTrend() { return dailyTokenTrend; }
        public void setDailyTokenTrend(List<TimeSeriesPoint> dailyTokenTrend) { this.dailyTokenTrend = dailyTokenTrend; }
    }

    public static class TimeSeriesPoint {
        private String date;
        private double value;

        public TimeSeriesPoint() {}
        public TimeSeriesPoint(String date, double value) {
            this.date = date;
            this.value = value;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}
