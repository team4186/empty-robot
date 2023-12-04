package frc.commands.drive

import edu.wpi.first.wpilibj2.command.CommandBase
import frc.subsystems.DriveTrainSubsystem

class BrakeMotors(
    private val drive: DriveTrainSubsystem
) : CommandBase() {
    private var wait = 0

    override fun execute() {
        if (wait == 0) {
            drive.setToBreak()
            wait++
        }
    }

    override fun end(interrupted: Boolean) {
        drive.setToCoast()
        wait = 0
    }
}