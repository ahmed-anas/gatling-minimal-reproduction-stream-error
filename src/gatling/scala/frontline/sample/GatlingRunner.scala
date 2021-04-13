package frontline.sample

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

// Reference: https://www.james-willett.com/debug-gatling/
object GatlingRunner {

  def main(args: Array[String]): Unit = {

    val simClass = classOf[BasicSimulation].getName

    val props = new GatlingPropertiesBuilder
    props.simulationClass(simClass)

    Gatling.fromMap(props.build)
  }
}
