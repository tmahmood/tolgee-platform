package io.tolgee.development.testDataBuilder.data

import io.tolgee.development.testDataBuilder.builders.KeyBuilder
import io.tolgee.model.enums.Scope
import io.tolgee.model.enums.TranslationState
import io.tolgee.model.key.Key

class BatchJobsTestData : BaseTestData() {
  val anotherUser = root.addUserAccount { username = "anotherUser" }.self
  val germanLanguage = projectBuilder.addGerman().self
  val czechLanguage = projectBuilder.addCzech().self

  init {
    this.projectBuilder.addPermission {
      user = anotherUser
      scopes = arrayOf(Scope.KEYS_VIEW)
    }
  }

  fun addTranslationOperationData(keyCount: Int = 100): List<Key> {
    addAKey()
    return (1..keyCount).map {
      this.projectBuilder
        .addKey {
          name = "key$it"
        }.build {
          addTranslation {
            language = englishLanguage
            text = "en"
          }
        }.self
    }
  }

  private fun addAKey(): KeyBuilder =
    this.projectBuilder
      .addKey {
        name = "a-key"
      }.build {
        addTranslation {
          language = englishLanguage
          text = "en"
        }
        addTranslation {
          language = czechLanguage
          text = "cs"
        }
        addTranslation {
          language = germanLanguage
          text = "de"
        }
      }

  fun addKeyWithTranslationsReviewed(): Key {
    val aKey = addAKey()
    aKey.translations.forEach {
      it.self.state = TranslationState.REVIEWED
    }
    return aKey.self
  }

  fun addStateChangeData(keyCount: Int = 100): List<Key> =
    (1..keyCount).map {
      this.projectBuilder
        .addKey {
          name = "key$it"
        }.build {
          addTranslation {
            language = englishLanguage
            text = "en$it"
          }
          addTranslation {
            language = germanLanguage
            text = "de$it"
          }
          addTranslation {
            language = czechLanguage
            text = "cs$it"
          }
        }.self
    }

  fun addTagKeysData(keyCount: Int = 100): List<Key> {
    this.projectBuilder
      .addKey {
        name = "a-key"
      }.build {
        addTag("a-tag")
      }.self
    return (1..keyCount).map {
      this.projectBuilder
        .addKey {
          name = "key$it"
        }.build {
          addTag("tag1")
          addTag("tag2")
          addTag("tag3")
        }.self
    }
  }

  fun addNamespaceData(): List<Key> {
    this.projectBuilder.addKey("namespace", "key")
    return (1..500).map {
      this.projectBuilder.addKey("namespace1", "key$it").self
    } +
      (1..500).map {
        this.projectBuilder.addKey(keyName = "key-without-namespace-$it").self
      }
  }
}
