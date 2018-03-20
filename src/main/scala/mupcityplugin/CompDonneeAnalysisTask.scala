package mupcityplugin

import java.io.File
import fr.ign.task._

trait CompDonneeAnalysisTask {
  /* def apply(inputFolders: Array[File], dataFolder: File, outputFolder: File, name: String): File = {
    fr.ign.task.RasterAnalyseTask.runCompData(inputFolders, dataFolder, outputFolder, name)
  }*/
  def apply(inputFolder: Array[File], dataFolder: Array[File], name: Array[String]): File = {
    fr.ign.task.AnalyseTask.runCompData(inputFolder, dataFolder, name, true)
  }
}

object CompDonneeAnalysisTask extends CompDonneeAnalysisTask
