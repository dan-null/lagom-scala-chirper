package sample.chirper.friend.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import akka.Done
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import sample.chirper.friend.api.User
import play.api.libs.json.{Format, Json}

// Friend Command trait and serializers
sealed trait FriendCommand
object FriendCommand {
  import play.api.libs.json._
  import JsonSerializer.emptySingletonFormat

  val serializers: Seq[JsonSerializer[_]] = Vector(
    JsonSerializer(Json.format[CreateUser]),
    JsonSerializer(Json.format[AddFriend]),
    JsonSerializer(emptySingletonFormat(GetUser))
  )
}

// Friend related Commands and ReplyTypes
final case class CreateUser(user: User) extends FriendCommand with ReplyType[Done]
case object GetUser extends FriendCommand with ReplyType[GetUserReply]
final case class AddFriend(friendUserId: String) extends FriendCommand with ReplyType[Done]

// Non-Done ReplyType
final case class GetUserReply(user: Option[User])
object GetUserReply {
  implicit val format: Format[GetUserReply] = Json.format[GetUserReply]
}