package sample.chirper.chirp.api

import akka.stream.scaladsl.Source
import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait ChirpService extends Service {
  def addChirp(userId: String): ServiceCall[Chirp, NotUsed]
  // originally: def getLiveChirps(): ServiceCall[LiveChirpsRequest, Source[String, _]]
  // might need to figure out the generic type to use instead of 'NotUsed'
  def getLiveChirps(): ServiceCall[LiveChirpsRequest, Source[Chirp, NotUsed]]
  def getHistoricalChirps(): ServiceCall[HistoricalChirpsRequest, Source[Chirp, NotUsed]]

  override final def descriptor = {
    import Service._

    // @formatter:off
    named("chirpservice").withCalls(
      pathCall("/api/chirps/live/:userId", addChirp _),
      pathCall("/api/chirps/live", getLiveChirps _),
      pathCall("/api/chirps/history", getHistoricalChirps _)
    ).withAutoAcl(true)
    // @formatter:on
  }
}
