package sample.chirper.chirp.impl

import java.time.Instant
import java.util.concurrent.CompletionStage

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.datastax.driver.core.Row
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import sample.chirper.chirp.api.Chirp

import scala.List._
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

/**
  * Created by danielbarajas on 2/7/17.
  */
class ChirpRepositoryImpl(db: CassandraSession)(implicit ec: ExecutionContext) extends ChirpRepository {
  private final val NUM_RECENT_CHIRPS = 10
  private final val SELECT_HISTORICAL_CHIRPS =
    "SELECT * FROM chirp WHERE userId = ? AND timestamp >= ? ORDER BY timestamp ASC"
  private final val SELECT_RECENT_CHIRPS =
    "SELECT * FROM chirp WHERE userId = ? ORDER BY timestamp DESC LIMIT ?"

  override def getHistoricalChirps(userIds: List[String], timestamp: Long): Source[Chirp, NotUsed] = {
    val sources: List[Source[Chirp, NotUsed]] = userIds.map { case userId =>
      db.select(SELECT_HISTORICAL_CHIRPS, userId, timestamp.toString).map(toChirp _) //
    }
    mergeSources[Chirp](sources.iterator)
  }

  override def getRecentChirps(userIds: List[String]): Future[List[Chirp]] = {
    val chirps = userIds.map { case userId =>
      db.selectAll(SELECT_RECENT_CHIRPS, userId, NUM_RECENT_CHIRPS.toString).map(_.map(toChirp _).toList) //
    }

    Future.sequence(chirps).map { case l =>
      l.flatten
        .sortBy(c => c.timestamp)(Ordering[Instant].reverse)
        .take(NUM_RECENT_CHIRPS)
    }
  }

  private def toChirp(row: Row): Chirp = Chirp(row.getString("userId"),
                                               row.getString("message"),
                                               Instant.ofEpochMilli(row.getLong("timestamp")),
                                               row.getString("uuid"))

  // Silly, but necessary, way to combine a collection of Sources into one Source of the same type
  private def mergeSources[A:ClassTag](sources: Iterator[Source[A, NotUsed]]): Source[A, NotUsed] =
    Source.fromIterator[Source[A, NotUsed]](() => sources).flatMapMerge(sources.size, s => s)
}
