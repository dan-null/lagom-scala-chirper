package sample.chirper.friend.impl

import sample.chirper.friend.api.User
import play.api.libs.json.{Format, Json}

final case class FriendState(user: Option[User]) {
  def addFriend(friendUserId: String): FriendState = user match {
    case None =>
      throw new IllegalStateException("friend can't be added before user is created");
    case Some(u) =>
      val userWithNewFriends = user.map(u => u.copy(friends = u.friends ++ List(friendUserId)))
      FriendState(userWithNewFriends)
  }
}

object FriendState {
  implicit val format: Format[FriendState] = Json.format[FriendState]
}