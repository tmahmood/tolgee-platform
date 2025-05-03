package io.tolgee.batch

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
  properties = [
    "tolgee.cache.enabled=true",
  ],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ActiveProfiles("tests")
class BatchJobsGeneralWithoutRedisTest : AbstractBatchJobsGeneralTest()
