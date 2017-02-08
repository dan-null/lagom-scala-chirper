package sample.chirper.chirp.impl

import java.util.concurrent.CompletionStage

import akka.NotUsed
import akka.stream.scaladsl.Source
import sample.chirper.chirp.api.Chirp

import scala.concurrent.Future

trait ChirpRepository {
  def getHistoricalChirps(userIds: List[String], timestamp: Long): Source[Chirp, NotUsed]
  def getRecentChirps(userIds: List[String]): Future[List[Chirp]]
}
