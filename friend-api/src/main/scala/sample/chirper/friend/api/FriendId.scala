package sample.chirper.friend.api

import play.api.libs.json.{Format, Json}

/**
  * Created by danielbarajas on 2/3/17.
  */
final case class FriendId(friendId: String)

object FriendId {
  implicit val format: Format[FriendId] = Json.format[FriendId]
}


