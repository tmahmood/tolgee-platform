package io.tolgee

import io.tolgee.configuration.tolgee.PostgresAutostartProperties
import io.tolgee.postgresRunners.PostgresDockerTestRunnerFast
import io.tolgee.postgresRunners.PostgresDockerTestRunnerSlow
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile


@Configuration
@Profile("testsSlow")
class PostgresTestRunnerConfiguration {

  @Bean
  @Primary
  fun postgresTestRunner(): PostgresRunner? {
    val postgresAutostartProperties = PostgresAutostartProperties()
    return PostgresDockerTestRunnerSlow(postgresAutostartProperties)
  }

}

@Configuration
@Profile("tests")
class PostgresTestRunnerConfigurationSlow {

  @Bean
  @Primary
  fun postgresTestRunner(): PostgresRunner? {
    val postgresAutostartProperties = PostgresAutostartProperties()
    return PostgresDockerTestRunnerFast(postgresAutostartProperties)
  }

}
