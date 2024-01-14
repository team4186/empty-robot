package frc.subsystems

import com.revrobotics.CANSparkBase
import com.revrobotics.CANSparkLowLevel
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.SubsystemBase

class DriveTrainSubsystem(
    val leftMotor: CANSparkMax = driveSparkMaxMotors(
        CANSparkMax(14, CANSparkLowLevel.MotorType.kBrushless),
        CANSparkMax(13, CANSparkLowLevel.MotorType.kBrushless),
        inverted = true,
    ),
    val rightMotor: CANSparkMax = driveSparkMaxMotors(
        CANSparkMax(2, CANSparkLowLevel.MotorType.kBrushless),
        CANSparkMax(1, CANSparkLowLevel.MotorType.kBrushless),
        inverted = false,
    ),
) : SubsystemBase() {
    private val drive: DifferentialDrive = DifferentialDrive(leftMotor, rightMotor)
    private var currentForward = 0.0

    private val motorSafety: MotorSafety = object : MotorSafety() {
        override fun stopMotor() {
            leftMotor.stopMotor()
            rightMotor.stopMotor()
        }

        override fun getDescription(): String {
            return "EncoderDrive"
        }
    }

    override fun periodic() {
    }

    fun initialize() {
        drive.stopMotor()
        drive.isSafetyEnabled = false
    }

    fun stop() {
        drive.stopMotor()
        motorSafety.feed()
    }

    fun setToCoast() {
        leftMotor.idleMode = CANSparkBase.IdleMode.kCoast
        rightMotor.idleMode = CANSparkBase.IdleMode.kCoast
    }

    fun setToBreak() {
        leftMotor.idleMode = CANSparkBase.IdleMode.kBrake
        rightMotor.idleMode = CANSparkBase.IdleMode.kBrake
    }

    fun arcade(forward: Double, turn: Double, squareInputs: Boolean) {
        drive.arcadeDrive(forward, turn, squareInputs)
    }

    fun holonomic(forward: Double, turn: Double, strafe: Double, squareInputs: Boolean) {
        drive.arcadeDrive(forward, turn, squareInputs)
        currentForward = forward
    }

    fun turnOnly(turn: Double) {
        drive.arcadeDrive(currentForward, turn)
    }

    fun tank(left: Double, right: Double, squareInputs: Boolean) {
        drive.tankDrive(left, right, squareInputs)
    }

    fun setMotorOutput(left: Double, right: Double) {
        leftMotor.set(left)
        rightMotor.set(right)
        motorSafety.feed()
    }
}

fun driveSparkMaxMotors(
    lead: CANSparkMax,
    follower0: CANSparkMax,
    inverted: Boolean
): CANSparkMax {
    follower0.follow(lead)

    follower0.inverted = inverted
    lead.inverted = inverted

    lead.idleMode = CANSparkBase.IdleMode.kCoast
    follower0.idleMode = CANSparkBase.IdleMode.kCoast

    // Voltage Saturation
    // See https://docs.ctre-phoenix.com/en/stable/ch13_MC.html#voltage-compensation
    lead.enableVoltageCompensation(11.0)
    follower0.enableVoltageCompensation(11.0)

    return lead
}