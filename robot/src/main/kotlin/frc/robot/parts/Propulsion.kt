package frc.robot.parts

import com.revrobotics.spark.SparkBase.PersistMode
import com.revrobotics.spark.SparkBase.ResetMode
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import java.util.function.DoubleConsumer


private val BaseConfig = SparkMaxConfig()
    .smartCurrentLimit(50)
    .idleMode(SparkBaseConfig.IdleMode.kCoast)

val DefaultLeftConfig: SparkBaseConfig = SparkMaxConfig()
    .apply(BaseConfig)
    .inverted(true)

val DefaultRightConfig: SparkBaseConfig = SparkMaxConfig()
    .apply(BaseConfig)
    .inverted(false)


class MotorSet(
    val lead: SparkMax,
    val follower0: SparkMax,
    val baseConfig: SparkBaseConfig,
) : DoubleConsumer {
    init {
        lead.configure(baseConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters)

        SparkMaxConfig()
            .apply(baseConfig)
            .follow(lead)
            .let { follower0.configure(it, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters) }
    }

    override fun accept(value: Double) {
        lead.set(value)
    }

    fun stop() {
        lead.stopMotor()
    }

    fun setIdleMode(mode: SparkBaseConfig.IdleMode) {
        lead.configure(
            SparkMaxConfig()
                .apply(baseConfig)
                .idleMode(mode),
            ResetMode.kResetSafeParameters,
            PersistMode.kPersistParameters,
        )
    }
}
