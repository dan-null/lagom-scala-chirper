package sample.chirper.chirp.api

import play.api.libs.json.{Format, Json}
import java.time.Instant
import java.util.UUID

final case class Chirp(userId: String,
                 message: String,
                 timestamp: Instant = Instant.now,
                 uuid: String = UUID.randomUUID().toString
                )

// todo: way to auto generator UUID OR might need to add two apply methods Chirp object

object Chirp {
  /**
    * Format for converting chirp to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[Chirp] = Json.format[Chirp]
}
