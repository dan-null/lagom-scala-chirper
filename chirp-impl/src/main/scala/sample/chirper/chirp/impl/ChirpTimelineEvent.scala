package sample.chirper.chirp.impl

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import play.api.libs.json.Json
import sample.chirper.chirp.api.Chirp
import scala.collection.immutable.Seq

sealed trait ChirpTimelineEvent extends AggregateEvent[ChirpTimelineEvent] {
  override def aggregateTag: AggregateEventTagger[ChirpTimelineEvent] = ChirpTimelineEvent.Tag
}
object ChirpTimelineEvent {
  val Shards = 4
  val Tag: AggregateEventShards[ChirpTimelineEvent] = AggregateEventTag.sharded[ChirpTimelineEvent](Shards)

  val serializers: Seq[JsonSerializer[_]] = Seq(JsonSerializer[ChirpAdded](Json.format[ChirpAdded]))
}

final case class ChirpAdded(chirp: Chirp) extends ChirpTimelineEvent