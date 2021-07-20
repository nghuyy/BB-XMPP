defaultTasks(
        "clean",
        "setupVersion",
        "generateFiles",
        "build",
        "zip",
        "Release"
)

var buildNumber = 8
var localVersion = "1.0.${buildNumber}"
var projectName = "BBChat"

var bb_buildfile = listOf<String>(
        "**/*.cod",
        "**/*.debug",
        "**/*.jad",
        "**/*.jar",
        "**/*.export.xml",
        "**/*.csl",
        "**/*.cso",
        "**/*.mak"
)

var folder = project.projectDir
var api5_path = "C:\\Program Files (x86)\\Research In Motion\\BlackBerry JDE 5.0.0"
var api7_path = "C:\\Program Files (x86)\\Research In Motion\\BlackBerry JDE 7.1.0"
var jdk_path = "C:\\Program Files (x86)\\Java\\jdk1.5.0_22\\bin"
var warnkeyRelease = "warnkey=0x52424200;0x52525400;0x5242534b;0x42424944;0x52435200;0x4e464352;0x52455345"
var warnkey = "warnkey=0x52424200;0x52525400;0x52435200"
var packID = "blackberry.sig"
var passwordPath = rootProject.file(System.getProperty("user.home") + "/.gradle/.keystore").readText(charset("utf-8"))
var password = rootProject.file("${passwordPath}\\${packID}").readText(charset("utf-8"))

var appversion = File("${project.projectDir}\\${projectName}.jdp").readText(charset("utf-8")).split("Version=")[1].trim()

task("setupVersion") {
    doLast {
        //setup files
        File("${projectName}.jdp").writeText(File("${projectName}.jdp").readText(charset("utf-8")).replace("Version=${appversion}", "Version=${localVersion}"), charset("utf-8"))
        File("${projectName}.rapc").writeText(File("${projectName}.rapc").readText(charset("utf-8")).replace("MIDlet-Version: ${appversion}", "MIDlet-Version: ${localVersion}"), charset("utf-8"))
    }
}


task("generateFiles") {
    doLast {
        //setup files
        var filesStr = "import=${api7_path}\\lib\\net_rim_api.jar\n"
        var shortPathStr = ""
        project.fileTree("src").filter { it.isFile() }.files.forEach {
            filesStr += it.path + "\n"
            shortPathStr += it.path.replace(project.projectDir.toString() + "\\", "") + "\n"
        }
        project.fileTree("res").filter { it.isFile() }.files.forEach {
            filesStr += it.path + "\n"
            shortPathStr += it.path.replace(project.projectDir.toString() + "\\", "") + "\n"
        }
        File("${projectName}.files").writeText(filesStr, charset("utf-8"))
        var jdp_str = File("${projectName}.jdp").readText(charset("utf-8"))
        var arraystr = jdp_str.split("[Files\r\n")
        var firsttmp = arraystr[0]
        var lasttmp = arraystr[1]
        var laststr = lasttmp.split(Regex("\\]"),2)[1]
        File("${projectName}.jdp").writeText(firsttmp + "[Files\r\n" + shortPathStr + "]" + laststr,charset("utf-8"))
    }
}


task("build") {
    doLast {
        exec {
            commandLine = listOf(
                    "${api7_path}\\bin\\rapc.exe",
                    "-quiet",
                    "codename=build\\${projectName}",
                    "${projectName}.rapc",
                    warnkeyRelease,
                    "@${projectName}.files"
            )
        }
    }
}




tasks.create("signSource") {
    doLast {
        exec {
            commandLine("${jdk_path}\\javaw.exe",
                    "-jar",
                    "${api5_path}\\bin\\SignatureTool.jar",
                    "-a",
                    "-p",
                    password,
                    "-r",
                    "${folder}/build"
            )
            workingDir(api5_path)
        }
        delete("${folder}/build/cache")
    }
}

tasks.create<Copy>("copy") {
    dependsOn(tasks.getByName("signSource"))
    from("build")
    into("build/cache")
    include("*.cod", "*.jad")
}

task("Merge") {
    dependsOn(tasks.getByName("copy"))
}

tasks.register<Zip>("zip") {
    dependsOn(tasks.getByName("Merge"))
    archiveFileName.set("${projectName}-${localVersion}.zip")
    destinationDirectory.set(layout.projectDirectory.dir("build"))
    from("${folder}/build/cache")
}

tasks.register("Release") {
    doLast {
        delete("dist")
        exec {
            commandLine = listOf("git", "clone", "git@github.com:nghuyy/BBChat_Release.git", "dist")
        }
        copy {
            from("build/")
            into("dist/")
            include("*.zip")
        }
        if (File("build/cache").exists()) {
            copy {
                from("build/cache")
                into("dist/")
                include("*.cod", "*.jad")
                exclude("Mail.cod")
            }
            copy {
                from(zipTree("build/cache/BBChat.cod"))
                into("dist/")
            }
        }

        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "add", ".")
        }
        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "commit", "-m", "\"Update\"")
        }
        exec {
            workingDir = File("./dist")
            commandLine = listOf("git", "push", "-f", "origin", "main")
        }
    }
}

task("clean") {
    doLast {
        delete("build")
        delete(fileTree(".").matching {
            include(bb_buildfile)
        })
        delete("dist")
    }
}

