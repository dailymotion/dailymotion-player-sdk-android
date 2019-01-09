import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File


object LibraryProject {
    private var libraryVersionCode: Int
        get() {
            return file("properties.gradle").readLines()[0].split("=")[1].trim().toInt()
        }
        set(value) {
            val contents = """
                ext.libraryVersionCode=$value
                ext.libraryVersionName='${versionName(value)}'
                """.trimIndent()
            file("properties.gradle").writeText(contents)
            file("README.md").let {
                it.writeText(it.readText().replace("com.dailymotion.dailymotion-sdk-android:sdk:${versionName(value-2)}", "com.dailymotion.dailymotion-sdk-android:sdk:${versionName(value-1)}"))
            }
        }

    var libraryVersionName = "0"
        private set
        get() = file("properties.gradle").readLines()[1].split("=")[1].trim().replace("'", "")


    private val repository by lazy {
        FileRepositoryBuilder().setGitDir(file(".git"))
                .readEnvironment()
                .findGitDir()
                .build()!!
    }

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

    private fun versionName(versionCode: Int) = String.format("%d.%d.%02d", (versionCode - 100000)/10000, (versionCode/100) % 100, versionCode % 100)

    private fun tag(name: String) {
        val git = Git(repository)
        val tag = git.tag()
        tag.name = "v$name"
        tag.call()
    }

    fun tagAndIncrement(newVersionCode: Int) {
        tag(libraryVersionName)
        commitVersion(newVersionCode)
        //the CI will push
    }

    private fun commit(message: String) {
        val git = Git(repository)
        val commit = git.commit()
        commit.message = message
        commit.setAll(true)
        commit.call()
    }

    private fun commitVersion(newVersionCode: Int) {
        LibraryProject.libraryVersionCode = newVersionCode
        commit("Bump versionCode to $newVersionCode")
    }
}