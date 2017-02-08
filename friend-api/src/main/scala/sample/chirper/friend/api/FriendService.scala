package sample.chirper.friend.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}


/**
  * Created by danielbarajas on 2/3/17.
  */
trait FriendService extends Service {
  def getUser(userId: String): ServiceCall[NotUsed, User]
  def createUser(): ServiceCall[User, NotUsed]
  def addFriend(userId: String): ServiceCall[FriendId, NotUsed]
  def getFollowers(userId: String): ServiceCall[NotUsed, List[String]]

  override final def descriptor = {
    import Service._

    named("friendservice").withCalls(
      pathCall("/api/users/:userId", getUser _),
      namedCall("/api/users", createUser _),
      pathCall("/api/users/:userId/friends", addFriend _),
      pathCall("/api/users/:userId/followers", getFollowers _)
    ).withAutoAcl(true);
    // @formatter:on
  }
}