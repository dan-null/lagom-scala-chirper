package sample.chirper.friend.impl


import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry, ReadSide}
import sample.chirper.friend.api._

import scala.concurrent.ExecutionContext

class FriendServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                        readSide: ReadSide,
                        db: CassandraSession)
                       (implicit val ec: ExecutionContext) extends FriendService {

  override def getUser(userId: String): ServiceCall[NotUsed, User] = ServiceCall { _ =>
    friendEntityRef(userId).ask[GetUser.type](GetUser).map {
      case GetUserReply(Some(user)) => user
      case GetUserReply(None) => throw NotFound(s"user $userId not found")
    }
  }

  override def createUser(): ServiceCall[User, NotUsed] = ServiceCall { user =>
    friendEntityRef(user.userId).ask[CreateUser](CreateUser(user)).map(_ => NotUsed)
  }

  override def addFriend(userId: String): ServiceCall[FriendId, NotUsed] = ServiceCall { friendId =>
    friendEntityRef(userId).ask[AddFriend](AddFriend(friendId.friendId)).map(_ => NotUsed)
  }

  override def getFollowers(userId: String): ServiceCall[NotUsed, List[String]] = ServiceCall { _ =>
    db.selectAll("SELECT * FROM follower WHERE userId = ?", userId).map { case rows =>
      rows.map(_.getString("followedBy")).toList
    }
  }

  private def friendEntityRef(userId: String): PersistentEntityRef[FriendCommand] =
    persistentEntityRegistry.refFor[FriendEntity](userId)
}
