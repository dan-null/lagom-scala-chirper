package sample.chirper.chirp.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import sample.chirper.chirp.api.Chirp

/**
  * Created by danielbarajas on 2/7/17.
  */
trait ChirpTopic {
  def publish(chirp: Chirp): Unit
  def subscriber(userId: String): Source[Chirp, NotUsed]
}
