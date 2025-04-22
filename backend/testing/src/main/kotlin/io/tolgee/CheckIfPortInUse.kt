package io.tolgee

import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import org.slf4j.LoggerFactory
import java.io.IOException

fun isPortInUse(port: Int): Boolean {
  val logger = LoggerFactory.getLogger("port-in-use")
  val socket = Socket()
  val inetAddress = InetAddress.getByName("0.0.0.0")
  val socketAddress = InetSocketAddress(inetAddress, port)
  try {
    socket.connect(socketAddress)
    socket.bind(socketAddress)
  } catch (n: ConnectException) {
    return false
  } catch (n: IOException) {
    return true
  }
  return false
}

public fun getRandomContainerPort(): String {
  var port: Int?
  do {
    port = (56000..58000).random()
  } while (isPortInUse(port))
  return port.toString()
}
