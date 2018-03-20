package mupcityplugin

import java.io.File
import fr.ign.task._

trait StabAnalysisTask {
  /* def apply(inputFolders: Array[File], dataFolder: File, outputFolder: File, name: String): File = {
    fr.ign.task.AnalyseTask.runStab(inputFolders, dataFolder, outputFolder, name)
  }*/
  def apply(inputFolder: File, dataFolder: File, name: String): File = {
    fr.ign.task.AnalyseTask.runStab(inputFolder, dataFolder, name, true)
  }
}

object StabAnalysisTask extends StabAnalysisTask
