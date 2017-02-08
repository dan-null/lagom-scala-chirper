package sample.chirper.chirp.impl

import akka.Done
import com.lightbend.lagom.scaladsl.playjson.JsonSerializer
import play.api.libs.json.{Format, Json}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import sample.chirper.chirp.api.Chirp
import scala.collection.immutable.Seq

sealed trait ChirpTimelineCommand
object ChirpTimelineCommand {
  val serializers: Seq[JsonSerializer[_]] = Seq(JsonSerializer[AddChirp])
}
final case class AddChirp(chirp: Chirp) extends ChirpTimelineCommand with ReplyType[Done]
object AddChirp {
  implicit val format: Format[AddChirp] = Json.format[AddChirp]
}