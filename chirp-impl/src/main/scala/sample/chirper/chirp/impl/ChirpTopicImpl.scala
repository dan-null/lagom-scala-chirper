package sample.chirper.chirp.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.pubsub._
import sample.chirper.chirp.api.Chirp

import scala.reflect.ClassTag

/**
  * Created by danielbarajas on 2/7/17.
  */
class ChirpTopicImpl(pubSub: PubSubRegistry) extends ChirpTopic {

  private val MAX_TOPICS = 1024

  override def publish(chirp: Chirp): Unit = refFor(chirp.userId).publish(chirp)

  override def subscriber(userId: String): Source[Chirp, NotUsed] = refFor(userId).subscriber

  private def topicQualifier[A: ClassTag](userId: String): TopicId[A] =
    TopicId[A](String.valueOf(Math.abs(userId.hashCode) % MAX_TOPICS))

  private def refFor(userId: String): PubSubRef[Chirp] = pubSub.refFor[Chirp](topicQualifier[Chirp](userId))
}
