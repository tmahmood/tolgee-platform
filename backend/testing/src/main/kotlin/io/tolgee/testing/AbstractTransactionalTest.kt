package io.tolgee.testing

import io.tolgee.BatchJobTestListener
import io.tolgee.CleanDbTestListener
import io.tolgee.getRandomString
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.test.context.transaction.TransactionalTestExecutionListener

@TestExecutionListeners(
  value = [
    TransactionalTestExecutionListener::class,
    DependencyInjectionTestExecutionListener::class,
    CleanDbTestListener::class,
    DirtiesContextTestExecutionListener::class,
    BatchJobTestListener::class,
  ],
)
@ActiveProfiles(profiles = ["local", "tests"])
abstract class AbstractTransactionalTest {
  @Autowired
  protected open lateinit var entityManager: EntityManager

  protected fun commitTransaction() {
    TestTransaction.flagForCommit()
    entityManager.flush()
    TestTransaction.end()
    TestTransaction.start()
    entityManager.clear()
  }
}
