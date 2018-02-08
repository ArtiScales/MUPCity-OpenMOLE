package mupcityplugin

import java.io.File
import fr.ign.task._

trait ProjectCreationDecompTask {
  def apply(name: String, inputFolder: File, outputFolder: File,
    xmin: Double, ymin: Double, width: Double, height: Double, shiftX: Double, shiftY: Double,
    maxSize: Double, minSize: Double, seuilDensBuild: Double): File = {
    fr.ign.task.ProjectCreationDecompTask.run(name, inputFolder, outputFolder, xmin, ymin, width, height, shiftX, shiftY, maxSize, minSize, seuilDensBuild)
  }
}

object ProjectCreationDecompTask extends ProjectCreationDecompTask
