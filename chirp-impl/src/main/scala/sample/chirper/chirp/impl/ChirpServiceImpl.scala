package sample.chirper.chirp.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntity, PersistentEntityRegistry}
import sample.chirper.chirp.api.{Chirp, ChirpService, HistoricalChirpsRequest, LiveChirpsRequest}

import scala.collection.immutable.HashSet
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
//import com.softwaremill.macwire._

class ChirpServiceImpl(persistentEntities: PersistentEntityRegistry,
                       topic: ChirpTopic,
                       chirps: ChirpRepository)
                      (implicit ec: ExecutionContext) extends ChirpService {

  override def addChirp(userId: String): ServiceCall[Chirp, NotUsed] = ServiceCall { chirp =>
    if (!userId.equals(chirp.userId))
      throw new IllegalArgumentException(s"UserId $userId did not match userId in $chirp")

    persistentEntities.refFor[ChirpTimelineEntity](userId).ask(AddChirp(chirp)).map(done => NotUsed)
  }

  // originally: def getLiveChirps(): ServiceCall[LiveChirpsRequest, Source[String, _]]
  override def getLiveChirps():
      ServiceCall[LiveChirpsRequest, Source[Chirp, NotUsed]] = ServiceCall { lcr =>

    val users = lcr.userIds.toSet
    chirps.getRecentChirps(lcr.userIds).map { case recentChirps =>
      val pubSources = recentChirps.map(rc => topic.subscriber(rc.userId))
      val publishedSource =
        mergeSources[Chirp](pubSources.iterator).filter(c => users.contains(c.userId))
      val recentSource = Source.fromIterator[Chirp](() => recentChirps.iterator)

      publishedSource.concat(recentSource)
    }

    //Future.successful[Source[Chirp, NotUsed]](Source(List.empty[Chirp]))
  }

  override def getHistoricalChirps():
      ServiceCall[HistoricalChirpsRequest, Source[Chirp, NotUsed]] = ServiceCall { hcr =>

    val timestamp = hcr.formTime.toEpochMilli
    Future.successful(
      chirps.getHistoricalChirps(hcr.userIds, timestamp)
    )
  }

  // Silly, but necessary, way to combine a collection of Sources into one Source of the same type
  private def mergeSources[A:ClassTag](sources: Iterator[Source[A, NotUsed]]): Source[A, NotUsed] =
    Source.fromIterator[Source[A, NotUsed]](() => sources).flatMapMerge(sources.size, s => s)
}