package sample.chirper.chirp.api

import java.time.Instant

final case class HistoricalChirpsRequest(formTime: Instant, userIds: List[String])

object HistoricalChirpsRequest {
  import play.api.libs.json._

  implicit val format: Format[HistoricalChirpsRequest] = Json.format[HistoricalChirpsRequest]
}