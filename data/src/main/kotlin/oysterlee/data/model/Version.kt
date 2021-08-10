package oysterlee.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Version(
  val filename: String = "",
  val version: String = "",
  val notes: String = "",
  val publishDate: Instant = Clock.System.now(),
  val downloadUrl: String = "",
  val size: Double = 0.0,
)

data class DownloadProgress(
  val size: Long = 0L,
  val currentSize: Long = 0L,
  val percentage: Int = 0,
)
