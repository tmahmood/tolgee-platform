package io.tolgee.util

import io.tolgee.commandLineRunners.StartupImportCommandLineRunner
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*


fun checkIfPortIsInUse(port: Int): Boolean {
  val socket: Socket = Socket();
  val inetAddress = InetAddress.getByName ("localhost");
  val socketAddress = InetSocketAddress(inetAddress, port);
  socket.bind(socketAddress);
  socket.connect(socketAddress);
  System.out.println("The socket is connected: " + socket.isConnected());
  System.out.println("The socket is bounded: " + socket.isBound());
  return socket.isConnected
}


fun setContainerNameAndPort(startupImportCommandLineRunner: StartupImportCommandLineRunner) {
  startupImportCommandLineRunner.tolgeeProperties.postgresAutostart.containerName = UUID.randomUUID().toString();
  var port: Int = startupImportCommandLineRunner.tolgeeProperties.postgresAutostart.port.toInt();
  for (p in port..port + 1) {
    if (!checkIfPortIsInUse(p)) {
      port = p;
      break
    }
  }
  startupImportCommandLineRunner.tolgeeProperties.postgresAutostart.port = port.toString();
  startupImportCommandLineRunner.tolgeeProperties.postgresAutostart.stop = true
}
