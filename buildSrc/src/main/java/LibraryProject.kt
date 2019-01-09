import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object LibraryProject {

    private var libraryVersionCode: Int
        get() = file("current_version").readText().toInt()
        set(value) {
            file("current_version").writeText(value.toString())
            file("README.md").let {
                it.writeText(it.readText().replace(Regex("com.dailymotion.dailymotion-sdk-android:sdk:[0-9.]*"), "com.dailymotion.dailymotion-sdk-android:sdk:${versionName(value - 1)}"))
            }
        }

    var libraryVersionName = "0"
        private set
        get() = versionName(libraryVersionCode)

    private fun versionName(versionCode: Int) = String.format("%d.%d.%d", versionCode / 10000, (versionCode / 100) % 100, versionCode % 100)

    private val projectDir by lazy { findBaseDir() }

    private fun file(path: String): File = File(projectDir, path)

    private fun isBaseDir(dir: File) = dir.list().contains(".git")

    private fun findBaseDir(): File? {
        var dir = File(File(".").absolutePath)

        while (!isBaseDir(dir)) {
            if (dir.parent == null) {
                throw Exception("you need to call this from a git repo")
            }
            dir = File(dir.parent)
        }

        return dir
    }

    fun tagAndIncrement(newVersionCode: Int) {
        executeCommand("git tag v$libraryVersionName")
        libraryVersionCode = newVersionCode
        executeCommand("git add -u")
        executeCommand("git commit -a -m Bump_versionCode_to_$newVersionCode")
        executeCommand("git push")
        executeCommand("git push --tags")
    }

    private fun executeCommand(commandLine: String) {
        println("==> Executing command line: $commandLine")
        val process = ProcessBuilder(commandLine.split(" "))
                .directory(projectDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
        val result = process.waitFor()
        if (result != 0) {
            BufferedReader(InputStreamReader(process.errorStream)).useLines { lines ->
                val results = StringBuilder()
                lines.forEach { results.append(it) }
                throw Exception("$results")
            }
        }
    }

}