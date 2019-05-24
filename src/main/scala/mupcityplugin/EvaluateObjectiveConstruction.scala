package mupcityplugin

import java.io.File
//import fr.ign.task._

object EvaluateObjectiveConstruction {
  def apply(simulFile : File, folderIn : File ): Double = {
    fr.ign.exp.EvaluateObjectiveOfConstructionWithCells.EvaluateObjectiveConstructionWithCells(simulFile, folderIn)
  }
}