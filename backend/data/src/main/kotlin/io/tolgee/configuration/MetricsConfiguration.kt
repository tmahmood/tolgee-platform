package io.tolgee.configuration

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfiguration {
  @Bean
  fun meterRegistry(): MeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
}
