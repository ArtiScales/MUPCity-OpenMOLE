package mupcityplugin

import java.io.File
import fr.ign.task._

trait CompDonneeAnalysisTask {
  def apply(inputFolders: File, dataFolder: File, name: String): File = {
    fr.ign.task.AnalyseTask.runCompData(inputFolders, dataFolder, name, true)
  }
  def apply(inputFolder: Array[File], dataFolder: Array[File], mainFile: File, name: Array[String]): File = {
    fr.ign.task.AnalyseTask.runCompData(inputFolder, dataFolder, mainFile, name, true)
  }
}

object CompDonneeAnalysisTask extends CompDonneeAnalysisTask

