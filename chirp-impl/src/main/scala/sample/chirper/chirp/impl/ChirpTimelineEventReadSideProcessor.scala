package sample.chirper.chirp.impl

import java.time.Instant

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by danielbarajas on 2/7/17.
  */
final case class ChirpTimelineEventReadSideProcessor(db: CassandraSession,
                                                     readSide: CassandraReadSide)
                                                    (implicit ec: ExecutionContext)
    extends ReadSideProcessor[ChirpTimelineEvent] {

  private var insertChirp: Option[PreparedStatement] = None

  override def buildHandler(): ReadSideHandler[ChirpTimelineEvent] = {
    readSide.builder[ChirpTimelineEvent]("chirpTimelineOffset")
      .setGlobalPrepare(prepareCreateTable)
      .setPrepare(tag => prepareInsertChirp)
      .setEventHandler(processAddChirp _)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[ChirpTimelineEvent]] = ChirpTimelineEvent.Tag.allTags

  private def prepareCreateTable(): Future[Done] = db.executeCreateTable(
    "CREATE TABLE IF NOT EXISTS chirp" +
    "userId text, timestamp bigint, uuid text, message text, " +
    "PRIMARY KEY (userId, timestamp, uuid))"
  )

  private def prepareInsertChirp(): Future[Done] = db.prepare(
    "INSERT INTO chirp (userId, uuid, timestamp, message) VALUES (?, ?, ?, ?)"
  ).map { case preparedStmt =>
    insertChirp = Some(preparedStmt)
    Done
  }

  private def processAddChirp(es: EventStreamElement[AddChirp]): Future[List[BoundStatement]] = {

    insertChirp match {
      case None => throw new Exception(s"insert chirp not set when running event handler $AddChirp")
      case Some(is) =>
        Future.successful(
          List(is.bind()
            .setString("userId", es.event.chirp.userId)
            .setString("message", es.event.chirp.message)
            .setLong("timestamp", es.event.chirp.timestamp.toEpochMilli)
            .setString("uuid", es.event.chirp.uuid))
        )
    }
  }
}
