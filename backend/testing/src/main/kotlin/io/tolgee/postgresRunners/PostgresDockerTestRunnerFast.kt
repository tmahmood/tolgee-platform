package io.tolgee.postgresRunners

import io.tolgee.PostgresRunner
import io.tolgee.configuration.tolgee.PostgresAutostartProperties
import io.tolgee.isPortInUse
import io.tolgee.misc.dockerRunner.DockerContainerRunner
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile

@Profile("tests")
// @SpringBootTest(
//   properties = [
//     "tolgee.postgres-autostart.stop=true"
//   ],
// )
open class PostgresDockerTestRunnerFast(
    private val postgresAutostartProperties: PostgresAutostartProperties,
) : PostgresRunner {
  private var instances = listOf<DockerContainerRunner>()
  private val logger = LoggerFactory.getLogger(javaClass)
  private var port: String? = null
  private var containerName: String? = null
  private val containers  = listOf(
    Pair("test_container_05", 52405),
    Pair("test_container_06", 52406),
  )
  private var currentInstance: Int = 0

  override fun run() {
    // first check if any port from our list is available?
    for (c in containers) {
      if (!isPortInUse( c.second)) {
        port = c.second.toString()
        containerName = c.first
        break
      }
    }
    // if all containers are initialized
    if(port == null) {
      // take the oldest container that was active
      currentInstance += 1
      if(currentInstance > containers.size) {
        currentInstance = 0
      }
      // set the port and containerName
      port = containers[currentInstance].second.toString()
      containerName = containers[currentInstance].first
      logger.info("SELECTED $port for $containerName")
      // no need to stay here, because we already have a container running
      return
    }

    logger.info("Initializing SELECTED $port for $containerName")
    instances.plus(
      DockerContainerRunner(
          image = "postgres:16.3",
          expose = mapOf(port.toString() to "5432"),
          waitForLog = "database system is ready to accept connections",
          waitForLogTimesForNewContainer = 2,
          waitForLogTimesForExistingContainer = 1,
          rm = true,
          name = containerName,
          stopBeforeStart = true,
          env =
              mapOf(
                  "POSTGRES_PASSWORD" to postgresAutostartProperties.password,
                  "POSTGRES_USER" to postgresAutostartProperties.user,
                  "POSTGRES_DB" to postgresAutostartProperties.databaseName,
              ),
          command =
              "postgres -c max_connections=10000 -c random_page_cost=1.0 " +
                      "-c fsync=off -c synchronous_commit=off -c full_page_writes=off",
          timeout = 300000,
      ).also {
        logger.info("Starting Postgres Docker container")
        it.run()
      })
  }

  override fun stop() {
    instances.forEach { it.stop() }
  }

  override val shouldRunMigrations: Boolean
    // we don't want to run migrations when the container existed, and we are not stopping it,
    // this happens only for tests and there we can delete the database and start again with migrations
    get() = true

  override val datasourceUrl by lazy {
    "jdbc:postgresql://localhost:${port}/${postgresAutostartProperties.databaseName}"
  }
}
