package sample.chirper.friend.impl

import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents
import sample.chirper.friend.api.FriendService
import com.softwaremill.macwire._

abstract class FriendApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the services that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[FriendService].to(wire[FriendServiceImpl])
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = FriendSerializerRegistry

  // Register the persistent entity
  persistentEntityRegistry.register(wire[FriendEntity])
  readSide.register(wire[FriendEventProcessor])
}
