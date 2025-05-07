package io.tolgee.postgresRunners

import io.tolgee.PostgresRunner
import io.tolgee.configuration.tolgee.PostgresAutostartProperties
import io.tolgee.getRandomString
import io.tolgee.misc.dockerRunner.DockerContainerRunner
import io.tolgee.runCommand
import io.tolgee.runCommandNoWait
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile

@Profile("tests")
@SpringBootTest(
  properties = [
    "tolgee.postgres-autostart.stop=true"
  ],
)
open class PostgresDockerTestRunner(
  private val postgresAutostartProperties: PostgresAutostartProperties,
) : PostgresRunner {
  private var instances:List<DockerContainerRunner> = listOf();
  private var instance:DockerContainerRunner? = null
  private val logger = LoggerFactory.getLogger(javaClass)
  private var port: String? = null
  private var containerName: String? = null

  override fun run() {
    // first create the base image
    val baseContainerName = postgresAutostartProperties.containerName
    val containersRunning = runCommand("docker ps");
    DockerContainerRunner(
      image = "postgres:16.3",
      expose = mapOf(postgresAutostartProperties.port to "5432"),
      waitForLog = "database system is ready to accept connections",
      waitForLogTimesForNewContainer = 2,
      waitForLogTimesForExistingContainer = 1,
      rm = false,
      name = baseContainerName,
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
      if (containersRunning.contains(baseContainerName)) {
        logger.info("base container is running, creating image")
        if(!runCommand("docker images").contains(baseContainerName)) {
          runCommand("docker commit $baseContainerName $baseContainerName:latest")
        }
      } else {
        it.run()
        println(runCommand("docker commit $baseContainerName $baseContainerName:latest"))
      }
    };
    val tlgContainers = containersRunning.split("\n")
      .filter { it.contains("tlg-") }

    val containerNames = tlgContainers
      .map { "tlg-".plus(it.split("tlg-").last()).trim() };

    val oldContainers = tlgContainers
      .filter { it.contains("minutes") }
      .reversed()
      .map { "tlg-".plus(it.split("tlg-").last()).trim() }

    // if(containerNames.size > 20) {
    //   logger.info("closing old containers")
    //   oldContainers.forEach { runCommandNoWait("docker stop $it") }
    //   logger.info("closed old containers")
    // }

    val portsToAvoid = containersRunning.split("\n")
      .filter{ it.contains("0.0.0.0:") }
      .map { it -> it.split("0.0.0.0:").last() }
      .map { it -> it.split("-").first()}

    var foundContainer = false;
    while (!foundContainer) {
      containerName = "tlg-${getRandomString(10)}";
      port = (55410..56420).random().toString()
      if(portsToAvoid.contains(port) || containerNames.contains(containerName)) {
        continue
      }
      DockerContainerRunner(
        image = baseContainerName,
        expose = mapOf(port.toString() to "5432"),
        waitForLog = "database system is ready to accept connections",
        waitForLogTimesForNewContainer = 2,
        waitForLogTimesForExistingContainer = 1,
        rm = true,
        name = containerName,
        stopBeforeStart = false,
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
        try {
          it.run()
          instance = it
          foundContainer = true;
        } catch(dockerContainerRunner: DockerContainerRunner.CommandRunFailedException) {
          logger.warn("Failed to run container")
        }
      }
    }
  }


  override fun stop() {
    instance!!.stop()
  }

  override val shouldRunMigrations: Boolean
    // we don't want to run migrations when the container existed, and we are not stopping it,
    // this happens only for tests and there we can delete the database and start again with migrations
    get() = true

  override val datasourceUrl by lazy {
    "jdbc:postgresql://localhost:${port}/${postgresAutostartProperties.databaseName}"
  }
}
