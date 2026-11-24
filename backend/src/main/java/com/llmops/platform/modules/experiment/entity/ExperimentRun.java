package com.llmops.platform.modules.experiment.entity;

import com.llmops.platform.common.entity.BaseEntity;
import com.llmops.platform.modules.dataset.entity.DatasetItem;
import com.llmops.platform.modules.prompt.entity.PromptVersion;
import com.llmops.platform.modules.provider.entity.Model;
import com.llmops.platform.modules.tracing.entity.Trace;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "experiment_runs")
public class ExperimentRun extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_item_id", nullable = false)
    private DatasetItem datasetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id")
    private PromptVersion promptVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trace_id")
    private Trace trace;

    @Column(length = 50, nullable = false)
    private String status = "PENDING"; // PENDING, RUNNING, COMPLETED, FAILED

    @Column(name = "input_text", columnDefinition = "TEXT", nullable = false)
    private String inputText;

    @Column(name = "generated_output", columnDefinition = "TEXT")
    private String generatedOutput;

    @Column(name = "latency_ms")
    private long latencyMs = 0;

    private int tokens = 0;

    @Column(precision = 12, scale = 6)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public ExperimentRun() {}

    public Experiment getExperiment() { return experiment; }
    public void setExperiment(Experiment experiment) { this.experiment = experiment; }
    public DatasetItem getDatasetItem() { return datasetItem; }
    public void setDatasetItem(DatasetItem datasetItem) { this.datasetItem = datasetItem; }
    public PromptVersion getPromptVersion() { return promptVersion; }
    public void setPromptVersion(PromptVersion promptVersion) { this.promptVersion = promptVersion; }
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    public Trace getTrace() { return trace; }
    public void setTrace(Trace trace) { this.trace = trace; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInputText() { return inputText; }
    public void setInputText(String inputText) { this.inputText = inputText; }
    public String getGeneratedOutput() { return generatedOutput; }
    public void setGeneratedOutput(String generatedOutput) { this.generatedOutput = generatedOutput; }
    public long getLatencyMs() { return latencyMs; }
    public void setLatencyMs(long latencyMs) { this.latencyMs = latencyMs; }
    public int getTokens() { return tokens; }
    public void setTokens(int tokens) { this.tokens = tokens; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
