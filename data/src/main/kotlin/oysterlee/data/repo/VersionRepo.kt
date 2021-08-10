package oysterlee.data.repo

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import java.awt.Desktop
import java.io.File
import java.io.IOException
import kotlin.math.round
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import oysterlee.data.model.DownloadProgress
import oysterlee.data.model.Version

class VersionRepo {

  private val client = HttpClient(CIO) {
    if (!System.getenv("DEBUG").isNullOrBlank()) {
      install(Logging)
    }
    install(JsonFeature) {
      serializer = KotlinxSerializer()
    }
  }
  private var _os = ""

  companion object {
    private val UPDATER_URL = System.getenv("UPDATER_URL") ?: "http://localhost:1000"
  }

  init {
    val os = System.getProperty("os.name")
    if (os.startsWith("Mac")) {
      _os = "mac"
    } else if (os.startsWith("Windows")) {
      _os = "windows"
    }
  }

  suspend fun checkForUpdate() = flow<Version> {
    try {
      emit(client.get("$UPDATER_URL/update/$_os/latest") {
        headers {
          append(HttpHeaders.Accept, "application/json")
        }
      })
    } catch (e: Throwable) {
      throw e
    }
  }

  @ExperimentalCoroutinesApi
  suspend fun downloadUpdate(downloadUrl: String) = channelFlow {
    val response: HttpResponse = client.get(downloadUrl) {
      headers {
        append(HttpHeaders.Accept, "application/octet-stream")
        append(HttpHeaders.Connection, "keep-alive")
      }
      onDownload { bytesSentTotal, contentLength ->
        val percentage = round(((bytesSentTotal * 100) / contentLength).toDouble())
        val bar = round((25 * percentage) / 100)

        val formatted = "$percentage% [${"=".repeat(bar.toInt())}>${" ".repeat(25 - bar.toInt())}]"
        println("Downloading ($bytesSentTotal / ${contentLength}) [$formatted]")
        launch {
          send(
            DownloadProgress(
              size = contentLength,
              currentSize = bytesSentTotal,
              percentage = percentage.toInt()
            )
          )
        }
      }
    }
    val body: ByteArray = response.receive()
    val headers = response.headers

    var filename = ""
    val disposition = headers[HttpHeaders.ContentDisposition]
    if (!disposition.isNullOrBlank() && disposition.indexOf("attachment") != -1) {
      val filenameRegex = Regex("filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)")
      val matches = filenameRegex.find(disposition)
      if (matches != null) {
        filename = matches.value.replace(Regex("['\"]"), "").replace("filename=", "")
      }
    }
    val path = "${System.getProperty("java.io.tmpdir")}$filename"
    val file = File(path)
    file.writeBytes(body)
    withContext(Dispatchers.IO) {
      _openInstaller(file)
    }
  }

  private fun _openInstaller(file: File) {
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().open(file)
        exitProcess(0)
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }
}
