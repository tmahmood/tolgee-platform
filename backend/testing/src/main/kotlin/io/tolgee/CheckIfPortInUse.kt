package io.tolgee

import io.tolgee.misc.dockerRunner.DockerContainerRunner
import java.io.File
import java.util.concurrent.TimeUnit


public fun runCommand(
  cmd: String,
  workingDir: File = File("."),
  timeoutAmount: Long = 120,
  timeoutUnit: TimeUnit = TimeUnit.SECONDS,
): String {
  val process = ProcessBuilder("\\s+".toRegex().split(cmd.trim()))
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
  if (process.exitValue() != 0) {
    val output = process.errorStream.bufferedReader().readText();
    println(output)
    throw DockerContainerRunner.CommandRunFailedException(output)
  }
  return process.inputStream.bufferedReader().use { it.readText() } +
    process.errorStream.bufferedReader().readText()
}


public fun getRandomString(length: Int): String {
  val allowedChars = ('A'..'Z') + ('a'..'z')
  return (1..length)
    .map { allowedChars.random() }
    .joinToString("")
}

