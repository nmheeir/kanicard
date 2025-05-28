package com.nmheir.kanicard.core.domain.fsrs.algorithm

import com.nmheir.kanicard.core.domain.fsrs.model.FsrsCard
import com.nmheir.kanicard.core.domain.fsrs.model.RecordLog
import com.nmheir.kanicard.core.domain.fsrs.scheduler.SchedulingFsrsCard
import com.nmheir.kanicard.data.enums.Rating
import com.nmheir.kanicard.data.enums.State
import timber.log.Timber
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDateTime
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class FSRS(
    private val parameters: FsrsParameters = FsrsParameters()
) {

    private val internalModifier: Double = (parameters.requestRetention.pow(1 / DECAY) - 1) / FACTOR

    fun repeat(card: FsrsCard, now: LocalDateTime): RecordLog {

        val s = SchedulingFsrsCard(card, now).updateState(card.state)
        var easyInterval: Long
        var goodInterval: Long
        var hardInterval: Long

        if (card.state == State.New) {
            this.initDifficultiesAndStabilities(s)
            s.again.due = now.plusMinutes(1)
            s.hard.due = now.plusMinutes(5)
            s.good.due = now.plusMinutes(10)
            easyInterval = nextInterval(s.easy.stability)
            s.easy.scheduledDays = easyInterval
            s.easy.due = now.plusDays(easyInterval)
        }

        if (card.state == State.Learning || card.state == State.Relearning) {
            hardInterval = 0
            goodInterval = this.nextInterval(s.good.stability)
            easyInterval = max(
                this.nextInterval(s.easy.stability),
                goodInterval + 1,
            )
            s.schedule(now, hardInterval, goodInterval, easyInterval)
        }

        if (card.state == State.Review) {
            val interval = card.elapsedDays
            val lastD = card.difficulty
            val lastS = card.stability
            val retrievability = forgettingCurve(interval, lastS)
            nextDs(s, lastD, lastS, retrievability)
            hardInterval = nextInterval(s.hard.stability)
            goodInterval = nextInterval(s.good.stability)
            hardInterval = min(hardInterval, goodInterval)
            goodInterval = max(goodInterval, hardInterval + 1)
            easyInterval = max(
                nextInterval(s.easy.stability),
                goodInterval + 1,
            )
            s.schedule(now, hardInterval, goodInterval, easyInterval)
        }


        Timber.d("FSRS card: %s", card.toString())

        val abc = s.recordLog(card, now)
        Timber.d("Rating Good: %s", abc[Rating.Good])
        Timber.d("Rating Hard: %s", abc[Rating.Hard])
        Timber.d("Rating Easy: %s", abc[Rating.Easy])
        Timber.d("Rating Again: %s", abc[Rating.Again])
        return abc
    }

    private fun nextDs(
        s: SchedulingFsrsCard,
        lastD: Double,
        lastS: Double,
        retrievability: Double,
    ) {
        s.again.difficulty = nextDifficulty(lastD, Rating.Again)
        s.again.stability = nextForgetStability(
            lastD,
            lastS,
            retrievability,
        )
        s.hard.difficulty = nextDifficulty(lastD, Rating.Hard)
        s.hard.stability = nextRecallStability(
            lastD,
            lastS,
            retrievability,
            Rating.Hard,
        )
        s.good.difficulty = nextDifficulty(lastD, Rating.Good)
        s.good.stability = nextRecallStability(
            lastD,
            lastS,
            retrievability,
            Rating.Good,
        )
        s.easy.difficulty = nextDifficulty(lastD, Rating.Easy)
        s.easy.stability = nextRecallStability(
            lastD,
            lastS,
            retrievability,
            Rating.Easy,
        )
    }

    private fun initDifficultiesAndStabilities(s: SchedulingFsrsCard) {
        s.again.difficulty = this.initDifficulty(Rating.Again)
        s.again.stability = this.initStability(Rating.Again)
        s.hard.difficulty = this.initDifficulty(Rating.Hard)
        s.hard.stability = this.initStability(Rating.Hard)
        s.good.difficulty = this.initDifficulty(Rating.Good)
        s.good.stability = this.initStability(Rating.Good)
        s.easy.difficulty = this.initDifficulty(Rating.Easy)
        s.easy.stability = this.initStability(Rating.Easy)
    }

    private fun initDifficulty(rating: Rating): Double {
        return min(
            max(this.parameters.w[4] - (rating.value - 3) * this.parameters.w[5], 1.0),
            10.0,
        )
    }

    private fun initStability(rating: Rating): Double {
        return max(this.parameters.w[rating.value - 1], 0.1)
    }

    private fun nextInterval(s: Double): Long {
        val newInterval = this.applyFuzz(s * internalModifier)
        return min(
            max(Math.round(newInterval).toDouble(), 1.0),
            this.parameters.maximumInterval,
        ).toLong()
    }

    private fun applyFuzz(ivl: Double): Double {
        if (!this.parameters.enableFuzz || ivl < 2.5) return ivl
        val fuzzFactor = Random.nextDouble()
        val ivl2 = Math.round(ivl)
        val minIvl = max(2, Math.round(ivl2 * 0.95 - 1))
        val maxIvl = Math.round(ivl2 * 1.05 + 1)
        return floor(fuzzFactor * (maxIvl - minIvl + 1) + minIvl)
    }

    private fun nextDifficulty(d: Double, g: Rating): Double {
        val nextD = d - this.parameters.w[6] * (g.value - 3)
        return constrainDifficulty(
            meanReversion(this.parameters.w[4], nextD),
        )
    }

    private fun constrainDifficulty(difficulty: Double): Double {
        return min(
            max(difficulty.toBigDecimal(MathContext(2, RoundingMode.HALF_UP)).toDouble(), 1.0),
            10.0
        )
    }

    private fun meanReversion(init: Double, current: Double): Double {
        return this.parameters.w[7] * init + (1 - this.parameters.w[7]) * current
    }

    private fun nextRecallStability(d: Double, s: Double, r: Double, g: Rating): Double {
        val hardPenalty = if (Rating.Hard == g) this.parameters.w[15] else 1.0
        val easyBound = if (Rating.Easy == g) this.parameters.w[16] else 1.0
        return (
                s *
                        (1 +
                                exp(this.parameters.w[8]) *
                                (11 - d) *
                                s.pow(-this.parameters.w[9]) *
                                (exp((1 - r) * this.parameters.w[10]) - 1) *
                                hardPenalty *
                                easyBound)
                )
    }

    private fun nextForgetStability(d: Double, s: Double, r: Double): Double {

        val w11 = this.parameters.w[11]
        val w12 = this.parameters.w[12]
        val w13 = this.parameters.w[13]
        val w14 = this.parameters.w[14]

        val part1 = d.pow(-w12)
        val part2 = (s + 1).pow(w13) - 1
        val part3 = exp((1 - r) * w14)

        Timber.d("d: $d, s: $s, r: $r")
        Timber.d("w11: $w11, w12: $w12, w13: $w13, w14: $w14")
        Timber.d("part1: $part1, part2: $part2, part3: $part3")

        val result = w11 * part1 * part2 * part3
        Timber.d("result: $result")

        return (this.parameters.w[11] *
                d.pow(-this.parameters.w[12]) *
                ((s + 1).pow(this.parameters.w[13]) - 1) *
                exp((1 - r) * this.parameters.w[14]))
            .toBigDecimal(MathContext(2, RoundingMode.HALF_UP))
            .toDouble()
    }

    private fun forgettingCurve(elapsedDays: Long, stability: Double): Double {
        val factor = FACTOR
        val x = 1 + factor * elapsedDays / stability

        Timber.d("---- forgettingCurve ----")
        Timber.d("elapsedDays: $elapsedDays")
        Timber.d("stability: $stability")
        Timber.d("FACTOR: $factor")
        Timber.d("x (base of pow): $x")
        Timber.d("DECAY: $DECAY")

        if (x <= 0.0) {
            Timber.e("x <= 0, result will be NaN!")
            return Double.NaN
        }

        val result = x.pow(DECAY)
        Timber.d("forgettingCurve result: $result")

        return result
    }


    companion object {
        const val DECAY: Double = -0.5
        val FACTOR: Double = 0.9.pow(1 / DECAY) - 1
    }
}