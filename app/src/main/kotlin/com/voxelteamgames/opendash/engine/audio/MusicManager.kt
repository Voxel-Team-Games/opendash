package com.voxelteamgames.opendash.engine.audio

import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.openal.AL10.*
import org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.Decoder
import javazoom.jl.decoder.Header
import javazoom.jl.decoder.SampleBuffer

class MusicManager {

    private val device: Long
    private val context: Long

    private var source: Int = 0
    private var buffer: Int = 0

    private var currentUrl: String? = null

    private val cacheDirectory: Path =
        Path.of(
            System.getProperty("user.home"),
            ".opendash",
            "music"
        )

    private val httpClient =
        HttpClient.newHttpClient()

    init {

        Files.createDirectories(
            cacheDirectory
        )

        device =
            alcOpenDevice(null as ByteBuffer?)

        if (device == 0L) {
            error(
                "Não foi possível inicializar o dispositivo OpenAL."
            )
        }

        context =
            alcCreateContext(
                device,
                null as IntArray?
            )

        if (context == 0L) {

            alcCloseDevice(device)

            error(
                "Não foi possível criar o contexto OpenAL."
            )
        }

        alcMakeContextCurrent(
            context
        )

        AL.createCapabilities(
            ALC.createCapabilities(device)
        )

        source =
            alGenSources()

        if (
            alGetError() != AL_NO_ERROR
        ) {
            error(
                "Não foi possível criar a fonte de áudio."
            )
        }
    }

    // =================================================
    // PLAY
    // =================================================

    fun play(
        url: String?
    ) {

        if (
            url.isNullOrBlank()
        ) {
            stop()
            return
        }

        if (
            currentUrl == url &&
            alGetSourcei(
                source,
                AL_SOURCE_STATE
            ) == AL_PLAYING
        ) {
            return
        }

        stop()

        val file =
            downloadOrGetCached(
                url
            )

val extension =
    file.fileName
        .toString()
        .substringAfterLast('.')
        .lowercase()

when (extension) {

    "ogg" ->
        loadOgg(file)

    "mp3" ->
        loadMp3(file)

    else ->
        error(
            "Formato de áudio não suportado: $extension"
        )
}

        currentUrl =
            url

        alSourcei(
            source,
            AL_LOOPING,
            AL_TRUE
        )

        alSourcePlay(
            source
        )

        println(
            "Música iniciada: $url"
        )
    }

    // =================================================
    // RESTART
    // =================================================

fun restart() {

    if (buffer == 0) {
        return
    }

    println("Reiniciando música...")

    alSourceStop(source)

    alSourceRewind(source)

    alSourcePlay(source)

    val error = alGetError()

    if (error != AL_NO_ERROR) {
        println(
            "Erro OpenAL ao reiniciar música: $error"
        )
    }
}
    // =================================================
    // STOP
    // =================================================

    fun stop() {

        if (
            source != 0
        ) {
            alSourceStop(
                source
            )
        }

        if (
            buffer != 0
        ) {

            alDeleteBuffers(
                buffer
            )

            buffer = 0
        }

        currentUrl = null
    }

    // =================================================
    // DOWNLOAD / CACHE
    // =================================================

private fun downloadOrGetCached(
    url: String
): Path {

    val hash =
        sha256(url)

    val extension =
        when {
            url.lowercase().contains(".mp3") -> "mp3"
            url.lowercase().contains(".ogg") -> "ogg"
            else -> "audio"
        }

    val file =
        cacheDirectory.resolve(
            "$hash.$extension"
        )

    if (Files.exists(file)) {

        println(
            "Música encontrada no cache."
        )

        return file
    }

    println(
        "Baixando música..."
    )

    val request =
        HttpRequest.newBuilder(
            URI.create(url)
        )
            .GET()
            .build()

    val response =
        httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofByteArray()
        )

    if (
        response.statusCode() !in 200..299
    ) {

        error(
            "Não foi possível baixar a música. " +
            "HTTP ${response.statusCode()}"
        )
    }

    Files.write(
        file,
        response.body()
    )

    println(
        "Música salva no cache: $file"
    )

    return file
}
    // =================================================
    // OGG
    // =================================================

    private fun loadOgg(
        file: Path
    ) {

        val bytes =
            Files.readAllBytes(
                file
            )

        val encoded =
            BufferUtils.createByteBuffer(
                bytes.size
            )

        encoded.put(
            bytes
        )

        encoded.flip()

        val channels =
            BufferUtils.createIntBuffer(
                1
            )

        val sampleRate =
            BufferUtils.createIntBuffer(
                1
            )

        val pcm =
            stb_vorbis_decode_memory(
                encoded,
                channels,
                sampleRate
            )
                ?: error(
                    "Não foi possível decodificar o arquivo OGG: $file"
                )

        val channelCount =
            channels.get(0)

        val frequency =
            sampleRate.get(0)

        val format =
            when (
                channelCount
            ) {

                1 ->
                    AL_FORMAT_MONO16

                2 ->
                    AL_FORMAT_STEREO16

                else ->
                    error(
                        "Número de canais não suportado: $channelCount"
                    )
            }

        buffer =
            alGenBuffers()

        alBufferData(
            buffer,
            format,
            pcm,
            frequency
        )

        alSourcei(
            source,
            AL_BUFFER,
            buffer
        )
    }
    private fun loadMp3(
    file: Path
) {

    val input =
        Files.newInputStream(file)

    val bitstream =
        Bitstream(input)

    val decoder =
        Decoder()

    val pcmData =
        java.io.ByteArrayOutputStream()

    var sampleRate = 0
    var channels = 0

    try {

        while (true) {

            val header =
                bitstream.readFrame()
                    ?: break

            val sampleBuffer =
                decoder.decodeFrame(
                    header,
                    bitstream
                ) as SampleBuffer

            if (sampleRate == 0) {

                sampleRate =
                    sampleBuffer.sampleFrequency

                channels =
                    sampleBuffer.channelCount
            }

            val buffer =
                sampleBuffer.buffer

            val length =
                sampleBuffer.bufferLength

            for (
                i in 0 until length
            ) {

                val sample =
                    buffer[i].toInt()

                pcmData.write(
                    sample and 0xFF
                )

                pcmData.write(
                    (sample shr 8) and 0xFF
                )
            }

            bitstream.closeFrame()
        }

    } finally {

        input.close()
    }

    val pcm =
        BufferUtils.createByteBuffer(
            pcmData.size()
        )

    pcm.put(
        pcmData.toByteArray()
    )

    pcm.flip()

    val format =
        when (channels) {

            1 ->
                AL_FORMAT_MONO16

            2 ->
                AL_FORMAT_STEREO16

            else ->
                error(
                    "Número de canais não suportado: $channels"
                )
        }

    buffer =
        alGenBuffers()

    alBufferData(
        buffer,
        format,
        pcm,
        sampleRate
    )

    alSourcei(
        source,
        AL_BUFFER,
        buffer
    )
}

    // =================================================
    // HASH
    // =================================================

    private fun sha256(
        text: String
    ): String {

        val digest =
            MessageDigest.getInstance(
                "SHA-256"
            )

        return digest.digest(
            text.toByteArray()
        )
            .joinToString("") {
                "%02x".format(it)
            }
    }

    // =================================================
    // DESTROY
    // =================================================

    fun destroy() {

        stop()

        if (
            source != 0
        ) {

            alDeleteSources(
                source
            )

            source = 0
        }

        alcMakeContextCurrent(
            0L
        )

        if (
            context != 0L
        ) {

            alcDestroyContext(
                context
            )
        }

        if (
            device != 0L
        ) {

            alcCloseDevice(
                device
            )
        }
    }
}