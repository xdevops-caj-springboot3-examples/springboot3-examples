# Distributed Tracing with OpenTelemetry and Jaeger in Spring Boot 3


## References

- https://refactorfirst.com/distributed-tracing-with-opentelemetry-jaeger-in-spring-boot
- https://www.jaegertracing.io/docs/1.47/apis/#opentelemetry-protocol-stable


## Application

### Import dependencies

Import actuator dependencies:
```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```

Import opentelemetry dependencies:
```xml
		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-tracing-bridge-otel</artifactId>
		</dependency>
		<dependency>
			<groupId>io.opentelemetry</groupId>
			<artifactId>opentelemetry-exporter-otlp</artifactId>
		</dependency>
```

Notes:
- `micrometer-tracing-bridge-otel` — provides micrometer bridge/facade to Opentelemetry tracing. It also transitively brings all Opentelemetry dependency SDK toolsets for Span tracing, propagation & instrumentation.
- `opentelemetry-exporter-otlp` — provides Span exporting/reporting toolset to any external OpenTelemetry protocol (OTLP) compliant collector.

### Application configuration

```yaml
# Jaeger collector url to accept OpenTelemetry Protocol (OTLP) 
tracing:
  url: http://localhost:4318/v1/traces

management:
  tracing:
    sampling:
      probability: 1.0
```


## Start Jaeger with container

```bash
docker run -d --name jaeger \
  -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
  -e COLLECTOR_OTLP_ENABLED=true \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 4317:4317 \
  -p 4318:4318 \
  -p 14250:14250 \
  -p 14268:14268 \
  -p 14269:14269 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.47

```

Common ports:
- 16686	HTTP	query	serve frontend
- 4317	HTTP	collector	accept OpenTelemetry Protocol (OTLP) over gRPC, if enabled
- 4318	HTTP	collector	accept OpenTelemetry Protocol (OTLP) over HTTP, if enabled


References:
- https://www.jaegertracing.io/docs/1.47/getting-started/


## Test

```bash
curl http://localhost:8080/greet/william
curl http://localhost:8080/greet/tom
curl http://localhost:8080/greet/john
```

Access http://localhost:16686 to access the Jaeger UI.

Choose service as `jaeger-tracing` and click "Find Traces".

## More info

- https://github.com/magsther/awesome-opentelemetry