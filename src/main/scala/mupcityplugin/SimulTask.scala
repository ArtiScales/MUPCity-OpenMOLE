package mupcityplugin

import java.io.File
//import fr.ign.task._

object SimulTask {
  def apply(projectFile: File, name: String,
    nMax: Int, strict: Boolean, ahp0: Double,
    ahp1: Double, ahp2: Double, ahp3: Double, ahp4: Double, ahp5: Double, ahp6: Double, ahp7: Double, ahp8: Double,
    mean: Boolean, seed: Long): File = {
    fr.ign.task.SimulTask.run(projectFile, name, nMax, strict, ahp0, ahp1, ahp2, ahp3, ahp4, ahp5, ahp6, ahp7, ahp8, mean, seed)
  }
}
