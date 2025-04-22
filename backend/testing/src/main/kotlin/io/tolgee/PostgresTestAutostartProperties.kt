package io.tolgee

import io.tolgee.configuration.tolgee.PostgresAutostartProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Profile("tests")
@Configuration
@Primary
class PostgresTestAutostartProperties {

  public fun getRandomContainer() : PostgresAutostartProperties {
    return PostgresAutostartProperties().apply {
      enabled = true
      mode = PostgresAutostartProperties.PostgresAutostartMode.DOCKER
      user = "postgres"
      password = "postgres"
      databaseName = "postgres"
      maxWaitTime = 300
      port = getRandomContainerPort().toString()
      containerName = getRandomString(10)
      stop = true
    }
  }

}
