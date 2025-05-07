package io.tolgee

import io.tolgee.configuration.tolgee.PostgresAutostartProperties
import io.tolgee.postgresRunners.PostgresDockerTestRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile


@Configuration
@Profile("tests")
class PostgresTestRunnerConfiguration {

  @Bean
  @Primary
  fun postgresTestRunner(): PostgresRunner? {
    val postgresAutostartProperties = PostgresAutostartProperties()
    return PostgresDockerTestRunner(postgresAutostartProperties)
  }

}
