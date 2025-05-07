package io.tolgee.postgresRunners

import io.tolgee.getRandomString
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource


@ActiveProfiles("tests")
@SpringBootTest
class ContainerConfigGen {

  // postgres-autostart:
  // enabled: true
  // container-name: tolgee_postgres_ee
  // port: 55436
  // stop: true
  @DynamicPropertySource
  fun registerContainerAutostartPropertyGen(registry: DynamicPropertyRegistry) {
    registry.add(
      "tolgee.postgres-autostart.container-name", {
        "tlgDyn-${getRandomString(10)}"
      })
  }
}
