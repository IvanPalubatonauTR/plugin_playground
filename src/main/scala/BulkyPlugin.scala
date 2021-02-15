import sbt._, Keys._

object BulkyPlugin extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    val bulkyThresholdInLines = settingKey[Int]("bulky threshold in lines")
    val bulkySources = taskKey[Seq[(Int, File)]]("bulky sources")
  }

  import autoImport._

  override lazy val globalSettings: Seq[Setting[_]] = Seq(
    bulkyThresholdInLines := 100,
  )

  def getBulkyLines(files: Seq[File], threshold: Int): Seq[(Int, File)] = {
    (for {
      file <- files
      size = sbt.IO.readLines(file).size
      if size >= threshold
      result = (size, file)
    } yield result).sortBy({ case (a, _) => a }).reverse
  }

  override val projectSettings: Seq[Setting[_]] = Seq(
    bulkySources := getBulkyLines((Compile / sources).value, bulkyThresholdInLines.value),
    (Test / bulkySources) := getBulkyLines((Test / sources).value, bulkyThresholdInLines.value))
}