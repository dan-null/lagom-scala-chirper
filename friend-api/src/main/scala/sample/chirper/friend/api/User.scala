package sample.chirper.friend.api

import play.api.libs.json.{Format, Json}

/**
  * Created by danielbarajas on 2/3/17.
  */
final case class User(userId: String, name: String, friends: List[String] = List.empty[String])

object User {
  implicit val format: Format[User] = Json.format[User]
}