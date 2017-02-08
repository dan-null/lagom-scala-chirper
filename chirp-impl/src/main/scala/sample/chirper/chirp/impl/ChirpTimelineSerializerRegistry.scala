package sample.chirper.chirp.impl
import scala.collection.immutable.Seq

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

/**
  * Created by danielbarajas on 2/7/17.
  */
object ChirpTimelineSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] =
    ChirpTimelineCommand.serializers ++ ChirpTimelineEvent.serializers
}
