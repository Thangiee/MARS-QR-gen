name := "QR-gen-gui"
version := "1.0"

mainClass in assembly := Some("Main")
assemblyOutputPath in assembly := new File("./QR-gen-gui.jar")

libraryDependencies ++= Seq(
  "net.glxn.qrgen" % "javase" % "2.0"
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)