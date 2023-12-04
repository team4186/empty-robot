package frc.robot

import edu.wpi.first.wpilibj.Compressor
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import frc.commands.drive.CheesyDrive
import frc.commands.drive.TeleopDrive
import frc.subsystems.DriveTrainSubsystem

class Robot : TimedRobot() {
    private enum class DriveMode {
        Raw,
        Cheesy
    }

    private val joystick0 = Joystick(0) //drive joystick

    private val driveTrainSubsystem = DriveTrainSubsystem()
//    private val compressor = Compressor(0, PneumaticsModuleType.CTREPCM)

    private val autonomousChooser = SendableChooser<Command>()
    private val driveModeChooser = SendableChooser<DriveMode>()

    private val cheesyDrive = CheesyDrive(
        inputThrottle = { joystick0.y },
        inputYaw = { joystick0.x },
        drive = driveTrainSubsystem,
        sensitivityHigh = 0.5,
        sensitivityLow = 0.5
    )
    private val rawDrive = TeleopDrive(
        inputThrottle = { joystick0.y },
        inputTurn = { joystick0.twist },
        inputYaw = { joystick0.x },
        drive = driveTrainSubsystem
    )

    override fun robotInit() {
        driveTrainSubsystem.initialize()
//        compressor.enableDigital()

        with(autonomousChooser) {
            setDefaultOption("Nothing", null)
            SmartDashboard.putData("Autonomous Mode", this)
        }

        with(driveModeChooser) {
            setDefaultOption("Raw", DriveMode.Raw)
            addOption("Cheesy", DriveMode.Cheesy)
            SmartDashboard.putData("Drive Mode", this)
        }
    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

    override fun autonomousInit() {
        driveTrainSubsystem.setToBreak()
        val autonomous = autonomousChooser.selected
        autonomous?.schedule()
    }

    override fun autonomousPeriodic() {
    }

    override fun autonomousExit() {
        CommandScheduler.getInstance().cancelAll()
    }

    override fun teleopInit() {
        driveTrainSubsystem.setToCoast()

        when (driveModeChooser.selected) {
            DriveMode.Cheesy -> cheesyDrive
            DriveMode.Raw -> rawDrive
            else -> rawDrive
        }.schedule()
    }

    override fun teleopPeriodic() {
    }

    override fun teleopExit() {
        driveTrainSubsystem.setToBreak()
        CommandScheduler.getInstance().cancelAll()
    }

    override fun testInit() {
    }
}
