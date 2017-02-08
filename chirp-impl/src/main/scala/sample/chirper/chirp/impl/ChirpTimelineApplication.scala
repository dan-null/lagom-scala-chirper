package sample.chirper.chirp.impl

import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server._
import play.api.libs.ws.ahc.AhcWSComponents
import sample.chirper.chirp.api.ChirpService
import com.softwaremill.macwire._

/**
  * Created by danielbarajas on 2/7/17.
  */
abstract class ChirpTimelineApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents
    with PubSubComponents {

  // Required instantiation for ChirpServiceImpl
  lazy val chirpTopic = wire[ChirpTopicImpl] // passes in PubSubComponent pubSubRegistry
  lazy val chirpRepository = wire[ChirpRepositoryImpl] // passes in C* Pers. Component cassandraSession

//  val topic = new ChirpTopicImpl(pubSubRegistry)
  // Bind the servie that this server provides
  override lazy val lagomServer = LagomServer.forServices(
    bindService[ChirpService].to(wire[ChirpServiceImpl])
  )

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = ChirpTimelineSerializerRegistry

  // Register the persistent entity
  persistentEntityRegistry.register(wire[ChirpTimelineEntity])
  readSide.register(wire[ChirpTimelineEventReadSideProcessor])
}
