package sample.chirper.friend.impl

import java.util.concurrent.CompletionStage

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence._
import com.lightbend.lagom.scaladsl.persistence.cassandra._
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor.ReadSideHandler

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

final case class FriendEventProcessor(session: CassandraSession,
                                      readSide: CassandraReadSide)
                                     (implicit ec: ExecutionContext)
    extends ReadSideProcessor[FriendEvent] {

  private var writeFollowers: Option[PreparedStatement] = None
  private def setWriteFollowers(writeFollowers: PreparedStatement): Unit = {
    this.writeFollowers = Some(writeFollowers)
  }

  override def buildHandler(): ReadSideHandler[FriendEvent] =
    readSide.builder[FriendEvent]("friendOffset")
      .setGlobalPrepare(prepareCreateTables)
      .setPrepare(_ => prepareWriteFollowers)
      .setEventHandler[FriendAdded](processFriendChanged _)
      .build()

  override def aggregateTags: Set[AggregateEventTag[FriendEvent]] = FriendEvent.Tag.allTags
  //override def aggregateTags: Set[AggregateEventTag[FriendEvent]] = Set(FriendEvent.Tag)

  private def prepareCreateTables(): Future[Done] = {
    // @formatter:off
    session.executeCreateTable(
      "CREATE TABLE IF NOT EXISTS follower ("
      + "userId text, followedBy text, "
      + "PRIMARY KEY (userId, followedBy))"
    )
    // @formatter:on
  }

  private def prepareWriteFollowers(): Future[Done] = {
    session.prepare("INSERT INTO follower (userId, followedBy) VALUES (?, ?)").andThen {
      case Success(ps) => setWriteFollowers(ps)
      case Failure(ex) => throw new Exception(s"problem preparing write followers $ex")
    }
    Future.successful(Done)
  }

  private def processFriendChanged(es: EventStreamElement[FriendAdded]): Future[List[BoundStatement]] = {
    writeFollowers match {
      case None => throw new Exception("write followers not set when running event handler processFriendChanged")
      case Some(wfs) =>
        Future.successful(
          List(wfs.bind().setString("userId", es.event.friendId).setString("followBy", es.event.userId))
        )
    }
  }
}