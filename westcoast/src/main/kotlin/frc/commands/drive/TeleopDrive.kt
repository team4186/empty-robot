package frc.commands.drive

import edu.wpi.first.wpilibj2.command.CommandBase
import frc.subsystems.DriveTrainSubsystem
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.withSign

class TeleopDrive(
    private val inputTurn: () -> Double,
    private val inputThrottle: () -> Double,
    private val inputYaw: () -> Double,
    private val shouldAttenuate: () -> Boolean = { true },
    private val forward: () -> Double = { -1.0 },
    private val drive: DriveTrainSubsystem
) : CommandBase() {
    init {
        addRequirements(drive)
    }

    override fun execute() {
        val throttle: Double
        val turn: Double
        val strafe: Double
        if (shouldAttenuate()) {
            throttle = attenuated(forward() * inputThrottle())
            turn = attenuated(-0.5 * inputTurn())
            strafe = attenuated(-inputYaw())
        } else {
            throttle = full(forward() * inputThrottle())
            turn = full(-inputTurn()).coerceIn(-0.4, 0.4)
            strafe = full(-inputYaw())
        }
        drive.holonomic(
            forward = -throttle,
            turn = turn,
            strafe = -strafe,
            squareInputs = false
        )
    }

    override fun end(interrupted: Boolean) {
        drive.stop()
    }

    private fun full(value: Double): Double {
        return value
            .absoluteValue
            .pow(1.6)
            .withSign(value)
    }

    private fun attenuated(value: Double): Double {
        return 0.75 * value.absoluteValue.pow(2).withSign(value)
    }
}