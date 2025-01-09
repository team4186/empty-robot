package frc.robot.actions

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.withSign


enum class Direction {
    Forward {
        override val multiplier get() = -1.0
    },
    Backward {
        override val multiplier get() = 1.0
    };

    abstract val multiplier: Double

    operator fun times(other: Double): Double = multiplier * other
}

fun manualDrive(
    forward: Double,
    turn: Double,
    direction: Direction = Direction.Forward,
    drive: (forward: Double, turn: Double) -> Unit,
) {
    drive(
        attenuated(direction * forward),
        attenuated(direction * -0.75 * turn),
    )
}

private fun attenuated(value: Double): Double {
    return 0.90 * value.absoluteValue.pow(2).withSign(value)
}
