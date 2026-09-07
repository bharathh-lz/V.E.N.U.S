package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * VenusEngineSoundGenerator: Hypercar Acoustic Synthesizer via AudioTrack PCM generation.
 * Synthesizes high-fidelity automotive and cybernetic acoustics:
 * - V8 Engine Starter Motor Crank & Aggressive Rev-Up Roar Sequence
 * - V8 Engine Deceleration & Spin-Down Sequence
 * - High-tech UI Affirmation Beeps & Action Executions
 */
object VenusEngineSoundGenerator {

    var isSoundEffectsEnabled: Boolean = true
    // Reduced response sound volume as requested (default comfortable soft level: 0.45f)
    var soundVolume: Float = 0.45f
        set(value) {
            field = value.coerceIn(0.0f, 1.0f)
        }

    private var activeSoundJob: Job? = null
    private const val SAMPLE_RATE = 22050

    /**
     * Synthesizes a high-performance Ignition Engine sequence:
     * - Phase 1: High-precision direct electric starter spin with starter pinion whine (0ms - 620ms)
     * - Phase 2: Instant electronic direct combustion catch, cold-start roar & twin-turbo throttle blip flare (620ms - 1650ms)
     * - Phase 3: Resonant sport exhaust burble settling into a velvet-smooth rhythmic idle (1650ms - 2400ms)
     * Note: All harsh mechanical clicking or relay click transients have been removed for a clean, pure exhaust & starter acoustic profile.
     */
    fun playEngineStartSound(onCompleted: (() -> Unit)? = null) {
        if (!isSoundEffectsEnabled) {
            CoroutineScope(Dispatchers.Main).launch { onCompleted?.invoke() }
            return
        }

        activeSoundJob?.cancel()
        activeSoundJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val durationMs = 2400
                val totalSamples = (SAMPLE_RATE * (durationMs / 1000f)).toInt()
                val audioData = ShortArray(totalSamples)

                val strokePeriod = 0.058 // High-speed electric starter stroke rate (~17.2 pulses/sec)

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / SAMPLE_RATE

                    val sampleVal: Double = when {
                        // PHASE 1: Electric Starter Pinion Spin & Compression Pulses (0.0s - 0.62s)
                        t < 0.62 -> {
                            val starterProgress = t / 0.62

                            // Characteristic high-speed starter electric whine (sweeps 280Hz -> 390Hz + distinct 2nd order planetary gear harmonic)
                            val starterFreq = 280.0 + (110.0 * starterProgress)
                            val starterWhine = 0.38 * sin(2.0 * Math.PI * starterFreq * t) +
                                    0.18 * sin(4.0 * Math.PI * starterFreq * t) +
                                    0.08 * sin(6.0 * Math.PI * starterFreq * t)

                            // Smooth rhythmic compression chugs (clean low-frequency cylinder thumps, zero clicking)
                            val strokePhase = (t % strokePeriod) / strokePeriod
                            val strokeEnv = exp(-10.0 * strokePhase)
                            val compressionThump = 0.55 * sin(2.0 * Math.PI * 52.0 * t) * strokeEnv

                            // Soft low-frequency starter rumble
                            val starterRumble = 0.25 * sin(2.0 * Math.PI * 38.0 * t)

                            // Smooth starter fade-in envelope
                            val env = (t / 0.08).coerceAtMost(1.0)
                            (starterWhine + compressionThump + starterRumble) * env
                        }

                        // PHASE 2: Core Ignition Catch, Cold Start Roar & Throttle Flare (0.62s - 1.65s)
                        t < 1.65 -> {
                            val revT = t - 0.62
                            val revProgress = revT / 1.03 // 0.0 to 1.0

                            // Authentic rev curve: Instant catch at 92Hz, sharp throttle flare to 360Hz peak at ~1.05s, then settling down to 88Hz
                            val currentFreq = when {
                                revProgress < 0.42 -> {
                                    val p = revProgress / 0.42
                                    92.0 + (268.0 * sin(p * Math.PI * 0.5))
                                }
                                else -> {
                                    val p = (revProgress - 0.42) / 0.58
                                    360.0 - (272.0 * (1.0 - cos(p * Math.PI * 0.5)))
                                }
                            }

                            // Harmonic Tuning (Smooth cylinder balance: 1st, 2nd, 3rd, and 4th order harmonics)
                            val f0 = currentFreq
                            val h1 = sin(2.0 * Math.PI * f0 * t)
                            val h2 = 0.72 * sin(4.0 * Math.PI * f0 * t + 0.25)
                            val h3 = 0.48 * sin(6.0 * Math.PI * f0 * t + 0.65)
                            val h4 = 0.32 * sin(8.0 * Math.PI * f0 * t + 1.10)
                            val subBass = 0.65 * sin(Math.PI * f0 * t)

                            // Throaty exhaust resonance (twin turbo spool & chamber resonance)
                            val turboSpool = 0.22 * sin(2.0 * Math.PI * (f0 * 2.85) * t) * revProgress.coerceAtMost(0.7)
                            val exhaustResonance = 1.0 + 0.20 * sin(2.0 * Math.PI * 22.0 * t)

                            // Deep exhaust gas air pulse (velvety puff, soft low-pass characteristics without sharp clicks)
                            val puffCycle = (sin(2.0 * Math.PI * f0 * 1.5 * t)).coerceAtLeast(0.0).pow(2.0)
                            val exhaustBassPressure = puffCycle * 0.22 * sin(2.0 * Math.PI * 45.0 * t)

                            // Initial ignition catch punch (smooth deep bass swell, no click)
                            val initialCatchPunch = if (revT < 0.15) {
                                0.70 * sin(2.0 * Math.PI * 55.0 * revT) * exp(-14.0 * revT)
                            } else 0.0

                            val tone = ((h1 + h2 + h3 + h4 + subBass) * exhaustResonance + turboSpool + exhaustBassPressure + initialCatchPunch)
                            tone * 0.88
                        }

                        // PHASE 3: Sport Idle Burble & Velvet Hum (1.65s - 2.40s)
                        else -> {
                            val idleT = t - 1.65
                            val idleProgress = idleT / 0.75
                            val idleFreq = 78.0 - (10.0 * idleProgress) // Settles at deep 68Hz (~820 RPM idle)

                            val h1 = sin(2.0 * Math.PI * idleFreq * t)
                            val h2 = 0.62 * sin(4.0 * Math.PI * idleFreq * t)
                            val h3 = 0.35 * sin(6.0 * Math.PI * idleFreq * t)
                            val subBass = 0.75 * sin(Math.PI * idleFreq * t)

                            // Velvet refined idle pulsation (smooth and balanced)
                            val idleBurble = 1.0 + 0.16 * sin(2.0 * Math.PI * 15.0 * t)

                            // Smooth fade-out towards end of transition
                            val fadeOut = if (idleProgress > 0.70) (1.0 - idleProgress) / 0.30 else 1.0
                            (h1 + h2 + h3 + subBass) * idleBurble * fadeOut * 0.72
                        }
                    }

                    val sample = sampleVal * (Short.MAX_VALUE * 0.85 * soundVolume)
                    audioData[i] = sample.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }

                playPcmTrack(audioData)
            } catch (_: Exception) {
            } finally {
                CoroutineScope(Dispatchers.Main).launch {
                    onCompleted?.invoke()
                }
            }
        }
    }

    /**
     * Synthesizes a high-performance Engine Deceleration & Spin-Down sequence:
     * - Phase 1: Throttle cutoff & sport exhaust deceleration rumble (0ms - 450ms)
     * - Phase 2: Decaying smooth compression braking pulses (450ms - 1050ms)
     * - Phase 3: Quiet velvet exhaust sigh (1050ms - 1350ms) — without clicking sounds or relay snaps
     */
    fun playEngineStopSound(onCompleted: (() -> Unit)? = null) {
        if (!isSoundEffectsEnabled) {
            CoroutineScope(Dispatchers.Main).launch { onCompleted?.invoke() }
            return
        }

        activeSoundJob?.cancel()
        activeSoundJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val durationMs = 1350
                val totalSamples = (SAMPLE_RATE * (durationMs / 1000f)).toInt()
                val audioData = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / SAMPLE_RATE

                    val sampleVal: Double = when {
                        // Phase 1: Rapid throttle drop & deceleration rumble (0.0s - 0.45s)
                        t < 0.45 -> {
                            val p = t / 0.45
                            val currentFreq = 72.0 - (42.0 * p) // Drops from 72Hz to 30Hz
                            val fundamental = sin(2.0 * Math.PI * currentFreq * t)
                            val secondHarmonic = 0.4 * sin(4.0 * Math.PI * currentFreq * t)
                            val subBass = 0.6 * sin(Math.PI * currentFreq * t)
                            val rumble = (fundamental + secondHarmonic + subBass) * (1.0 - (0.35 * p))
                            rumble * 0.72
                        }

                        // Phase 2: Dying smooth compression strokes (0.45s - 1.05s)
                        t < 1.05 -> {
                            val compT = t - 0.45
                            val p = compT / 0.60
                            // Smooth dying chugs at decelerating intervals (zero clicking)
                            val chugPhase = (compT * 6.5).let { it - it.toInt() }
                            val chugEnv = exp(-9.0 * chugPhase) * (1.0 - p)
                            val chugThump = sin(2.0 * Math.PI * 36.0 * t) * chugEnv * 0.60
                            chugThump
                        }

                        // Phase 3: Soft velvet exhaust sigh (1.05s - 1.35s) — clean fade out without clicking
                        else -> {
                            val sighT = t - 1.05
                            val p = (sighT / 0.30).coerceIn(0.0, 1.0)
                            val sighEnv = (1.0 - p) * 0.20
                            val softSub = sin(2.0 * Math.PI * 26.0 * t) * sighEnv
                            softSub
                        }
                    }

                    val sample = sampleVal * (Short.MAX_VALUE * 0.75 * soundVolume)
                    audioData[i] = sample.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }

                playPcmTrack(audioData)
            } catch (_: Exception) {
            } finally {
                CoroutineScope(Dispatchers.Main).launch {
                    onCompleted?.invoke()
                }
            }
        }
    }

    /**
     * Synthesized ignition pulse.
     */
    fun playIgnitionSound(onCompleted: (() -> Unit)? = null) {
        playEngineStartSound(onCompleted)
    }

    /**
     * Plays a high-tech two-tone futuristic affirmative beep / chime (880Hz -> 1320Hz).
     */
    fun playBeepSound(onCompleted: (() -> Unit)? = null) {
        if (!isSoundEffectsEnabled) {
            CoroutineScope(Dispatchers.Main).launch { onCompleted?.invoke() }
            return
        }

        activeSoundJob?.cancel()
        activeSoundJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val durationMs = 180
                val totalSamples = (SAMPLE_RATE * (durationMs / 1000f)).toInt()
                val audioData = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val progress = i.toDouble() / totalSamples
                    val currentFreq = if (progress < 0.5) 880.0 else 1320.0
                    val envelope = when {
                        progress < 0.1 -> progress / 0.1
                        progress > 0.85 -> (1.0 - progress) / 0.15
                        else -> 0.9
                    }

                    val t = i.toDouble() / SAMPLE_RATE
                    val sample = sin(2.0 * Math.PI * currentFreq * t) * envelope * (Short.MAX_VALUE * 0.55 * soundVolume)
                    audioData[i] = sample.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
                }

                playPcmTrack(audioData)
            } catch (_: Exception) {
            } finally {
                CoroutineScope(Dispatchers.Main).launch {
                    onCompleted?.invoke()
                }
            }
        }
    }

    /**
     * Plays an action confirmation pulse when multitasking or actions succeed.
     */
    fun playActionSound(onCompleted: (() -> Unit)? = null) {
        playBeepSound(onCompleted)
    }

    fun playActionConfirmationSound(onCompleted: (() -> Unit)? = null) {
        playActionSound(onCompleted)
    }

    private fun playPcmTrack(audioData: ShortArray) {
        var track: AudioTrack? = null
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = if (minBufferSize > 0) minBufferSize * 2 else audioData.size * 2

            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (track.state == AudioTrack.STATE_INITIALIZED) {
                track.play()
                track.write(audioData, 0, audioData.size, AudioTrack.WRITE_BLOCKING)
                val sleepMs = ((audioData.size.toDouble() / SAMPLE_RATE) * 1000).toLong()
                Thread.sleep(sleepMs + 80)
                try {
                    track.stop()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
        } finally {
            try {
                track?.release()
            } catch (_: Exception) {}
        }
    }
}
