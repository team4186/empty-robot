package frc.subsystems

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import com.revrobotics.CANSparkMax
import edu.wpi.first.wpilibj.MotorSafety
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.SubsystemBase

class DriveTrainSubsystem(
    val leftMotor: WPI_TalonSRX = driveTalonVictorMotors(
        WPI_TalonSRX(14),
        inverted = true,
        WPI_VictorSPX(13),
        WPI_VictorSPX(15),
    ),
    val rightMotor: WPI_TalonSRX = driveTalonVictorMotors(
        WPI_TalonSRX(2),
        inverted = false,
        WPI_VictorSPX(1),
        WPI_VictorSPX(3),
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
        if (leftMotor.statorCurrent > 60.0) {
            leftMotor.stopMotor()
            motorSafety.feed()
        }
        if (rightMotor.statorCurrent > 60.0) {
            rightMotor.stopMotor()
            motorSafety.feed()
        }
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
        leftMotor.setNeutralMode(NeutralMode.Coast)
        rightMotor.setNeutralMode(NeutralMode.Coast)
    }

    fun setToBreak() {
        leftMotor.setNeutralMode(NeutralMode.Brake)
        rightMotor.setNeutralMode(NeutralMode.Brake)
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

fun driveTalonVictorMotors(
    lead: WPI_TalonSRX,
    inverted: Boolean,
    vararg followers: WPI_VictorSPX,
): WPI_TalonSRX {
    lead.inverted = inverted
    followers.forEach {
        it.follow(lead)
        it.inverted = inverted
    }
    return lead
}

fun driveSparkMaxMotors(
    lead: CANSparkMax,
    follower0: CANSparkMax,
    invert: Boolean
): CANSparkMax {
    follower0.follow(lead)

    follower0.inverted = invert
    lead.inverted = invert

    lead.idleMode = CANSparkMax.IdleMode.kCoast
    follower0.idleMode = CANSparkMax.IdleMode.kCoast

    // Voltage Saturation
    // See https://docs.ctre-phoenix.com/en/stable/ch13_MC.html#voltage-compensation
    lead.enableVoltageCompensation(11.0)
    follower0.enableVoltageCompensation(11.0)

    return lead
}