package sample.chirper.chirp.api

import play.api.libs.json.{Format, Json}
/**
  * Created by danielbarajas on 2/3/17.
  */
final case class LiveChirpsRequest(userIds: List[String])

object LiveChirpsRequest {
  implicit val format: Format[LiveChirpsRequest] = Json.format[LiveChirpsRequest]
}
