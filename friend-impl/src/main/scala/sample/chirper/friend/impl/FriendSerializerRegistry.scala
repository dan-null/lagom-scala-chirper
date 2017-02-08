package sample.chirper.friend.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

/**
  * Created by danielbarajas on 2/6/17.
  */
object FriendSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] =
    Seq(JsonSerializer[FriendState]) ++ FriendCommand.serializers ++ FriendEvent.serializers
}