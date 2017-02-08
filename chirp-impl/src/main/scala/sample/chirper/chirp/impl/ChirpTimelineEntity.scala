package sample.chirper.chirp.impl

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity


class ChirpTimelineEntity(topic: ChirpTopic) extends PersistentEntity {
  override type Command = ChirpTimelineCommand
  override type Event = ChirpTimelineEvent
  override type State = NotUsed

  override def initialState: NotUsed = NotUsed

  override def behavior: Behavior = Actions()
    .onCommand[AddChirp, Done] { case (AddChirp(chirp), ctx, _) =>
      ctx.thenPersist(ChirpAdded(chirp))(_ => {
        ctx.reply(Done)
      })
      topic.publish(chirp)
      ctx.done
    }
    .onEvent { case (_, state) => state }
}