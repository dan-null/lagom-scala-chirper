package sample.chirper.friend.impl

import java.time.Instant

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag}
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer

/**
  * Created by danielbarajas on 2/3/17.
  */

sealed trait FriendEvent extends AggregateEvent[FriendEvent] {
  //override def aggregateTag = AggregateEventTag[FriendEvent](classOf[FriendEvent].toString)
  override def aggregateTag = FriendEvent.Tag
}

object FriendEvent {
  val NumShards = 4
  val Tag = AggregateEventTag.sharded[FriendEvent](NumShards)
  //val Tag = AggregateEventTag[FriendEvent]()

  import play.api.libs.json._
  import JsonSerializer.emptySingletonFormat

  val serializers: Seq[JsonSerializer[_]] = Vector(
    JsonSerializer(Json.format[UserCreated]),
    JsonSerializer(Json.format[FriendAdded])
  )
}

final case class UserCreated(userId: String, name: String) extends FriendEvent
final case class FriendAdded(userId: String,
                             friendId: String,
                             timestamp: Option[Instant] = Some(Instant.now)) extends FriendEvent

