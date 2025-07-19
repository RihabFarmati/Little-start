package com.app.littlestar.listener

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class MicVolumeListener {
    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    fun startListening(onVolumeChanged: (volume: Int) -> Unit) {
        if (isListening) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        isListening = true
        audioRecord?.startRecording()

        Thread {
            val buffer = ShortArray(bufferSize)
            while (isListening) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                var sum = 0.0
                for (i in 0 until read) {
                    sum += buffer[i] * buffer[i]
                }
                val amplitude = Math.sqrt(sum / read)
                onVolumeChanged(amplitude.toInt())
                Thread.sleep(100)
            }
        }.start()
    }

    fun stopListening() {
        isListening = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
