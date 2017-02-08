package sample.chirper.friend.impl

import java.time.LocalDateTime
import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import sample.chirper.friend.api.User

/**
  * Created by danielbarajas on 2/4/17.
  */
class FriendEntity extends PersistentEntity {
  override type Command = FriendCommand
  override type Event = FriendEvent
  override type State = FriendState

  override def initialState: FriendState = FriendState(None)

  override def behavior: Behavior = Actions()
    .onCommand[CreateUser, Done] { case (CreateUser(user), ctx, state) =>
      state.user match {
        case Some(u) =>
          ctx.invalidCommand(s"User $entityId is already created")
          ctx.done
        case None =>
          val events = scala.collection.mutable.ArrayBuffer.empty[FriendEvent]
          events += UserCreated(user.userId, user.name)
          user.friends.foreach { case friendId =>
            events += FriendAdded(user.userId, friendId)
          }
          ctx.thenPersistAll(events:_*)(() => ctx.reply(Done))
      }
    }
    .onCommand[AddFriend, Done] { case (AddFriend(friendUserId), ctx, state) =>
      state.user match {
        case Some(u) if u.friends.contains(friendUserId) =>
          ctx.reply(Done)
          ctx.done
        case Some(u) =>
          ctx.thenPersist(FriendAdded(u.userId, friendUserId))((_: FriendEvent) => ctx.reply(Done))
          ctx.done
        case None =>
          ctx.invalidCommand(s"User $entityId is not created")
          ctx.done
      }
    }
    .onReadOnlyCommand[GetUser.type, GetUserReply] { case (GetUser, ctx, state) =>
      ctx.reply(new GetUserReply(state.user))
    }
    .onEvent {
      case (FriendAdded(_, friendId, _), state) =>
        state.addFriend(friendId)
      case (UserCreated(userId, name), state) =>
        FriendState(Some(User(userId, name)))
    }
}