"""
AetherLLM Python SDK
Lightweight, zero-dependency ingestion client for Python LLM applications.
Supports OpenAI, LangChain, LlamaIndex, LiteLLM, Anthropic, or raw API calls.
"""

import time
import uuid
import json
import urllib.request
from typing import Optional, Dict, Any, List

class AetherTracer:
    def __init__(self, api_key: str, endpoint: str = "http://localhost:8080/api/v1/traces/ingest", project_id: Optional[str] = None):
        self.api_key = api_key
        self.endpoint = endpoint
        self.project_id = project_id

    def trace(self, name: str, session_id: Optional[str] = None, user_id: Optional[str] = None, tags: Optional[str] = None):
        return TraceContext(self, name, session_id, user_id, tags)

    def send_trace(self, payload: Dict[str, Any]):
        try:
            req_data = json.dumps(payload).encode('utf-8')
            req = urllib.request.Request(
                self.endpoint,
                data=req_data,
                headers={
                    'Content-Type': 'application/json',
                    'X-API-Key': self.api_key,
                },
                method='POST'
            )
            with urllib.request.urlopen(req, timeout=5) as response:
                return json.loads(response.read().decode('utf-8'))
        except Exception as e:
            print(f"[AetherLLM Tracer Warning] Failed to send trace: {e}")
            return None


class TraceContext:
    def __init__(self, tracer: AetherTracer, name: str, session_id: Optional[str] = None, user_id: Optional[str] = None, tags: Optional[str] = None):
        self.tracer = tracer
        self.id = str(uuid.uuid4())
        self.name = name
        self.session_id = session_id
        self.user_id = user_id
        self.tags = tags
        self.spans: List[Dict[str, Any]] = []
        self.start_time: Optional[float] = None
        self.end_time: Optional[float] = None
        self.status = "SUCCESS"

    def __enter__(self):
        self.start_time = time.time()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.end_time = time.time()
        if exc_type is not None:
            self.status = "ERROR"

        payload = {
            "id": self.id,
            "projectId": self.tracer.project_id,
            "name": self.name,
            "sessionId": self.session_id,
            "userId": self.user_id,
            "tags": self.tags,
            "status": self.status,
            "startTime": time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime(self.start_time)),
            "endTime": time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime(self.end_time)),
            "spans": self.spans
        }
        self.tracer.send_trace(payload)

    def span(self, name: str, span_type: str = "LLM", parent_span_id: Optional[str] = None):
        return SpanContext(self, name, span_type, parent_span_id)


class SpanContext:
    def __init__(self, trace_ctx: TraceContext, name: str, span_type: str, parent_span_id: Optional[str]):
        self.trace_ctx = trace_ctx
        self.id = str(uuid.uuid4())
        self.name = name
        self.span_type = span_type
        self.parent_span_id = parent_span_id
        self.start_time: Optional[float] = None
        self.end_time: Optional[float] = None
        self.input_json: Optional[str] = None
        self.output_json: Optional[str] = None
        self.model_name: Optional[str] = None
        self.provider_name: Optional[str] = None
        self.input_tokens = 0
        self.output_tokens = 0
        self.status = "SUCCESS"
        self.error_message: Optional[str] = None

    def __enter__(self):
        self.start_time = time.time()
        return self

    def set_input(self, data: Any):
        self.input_json = json.dumps(data) if not isinstance(data, str) else data

    def set_output(self, data: Any):
        self.output_json = json.dumps(data) if not isinstance(data, str) else data

    def set_model(self, model: str, provider: str = "OpenAI"):
        self.model_name = model
        self.provider_name = provider

    def set_tokens(self, input_tokens: int, output_tokens: int):
        self.input_tokens = input_tokens
        self.output_tokens = output_tokens

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.end_time = time.time()
        if exc_type is not None:
            self.status = "ERROR"
            self.error_message = str(exc_val)
            self.trace_ctx.status = "ERROR"

        span_data = {
            "id": self.id,
            "parentSpanId": self.parent_span_id,
            "name": self.name,
            "spanType": self.span_type,
            "status": self.status,
            "startTime": time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime(self.start_time)),
            "endTime": time.strftime('%Y-%m-%dT%H:%M:%SZ', time.gmtime(self.end_time)),
            "inputJson": self.input_json,
            "outputJson": self.output_json,
            "modelName": self.model_name,
            "providerName": self.provider_name,
            "inputTokens": self.input_tokens,
            "outputTokens": self.output_tokens,
            "errorMessage": self.error_message
        }
        self.trace_ctx.spans.append(span_data)
