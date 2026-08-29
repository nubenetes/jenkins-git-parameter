// ==============================================================================
// Shared Library Step: otelLogEvent.groovy
// Emits OpenTelemetry trace metadata & annotations for Grafana correlation
// ==============================================================================

def call(Map eventData = [:]) {
    def eventName = eventData.name ?: 'pipeline.custom.event'
    
    echo "📊 [OpenTelemetry Event] Emitting Span Annotation: ${eventName}"
    eventData.each { k, v ->
        echo "   - otel.attr.${k} = ${v}"
    }

    // When the Jenkins OpenTelemetry plugin is active, span attributes can be added
    // or sent via standard OTLP trace context
    sh """
        echo "OTel Trace Context: TRACE_ID=\${TRACE_ID:-mock-trace-id} SPAN_ID=\${SPAN_ID:-mock-span-id}"
    """
}
