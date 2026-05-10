plugins {
    java
    id("com.gradleup.shadow") version "9.2.0"
    //id("com.github.johnrengelman.shadow") version "8.1.1"
}

configurations.all {
    resolutionStrategy {
        force("org.ow2.asm:asm:9.4")
        force("org.ow2.asm:asm-tree:9.4")
        force("org.ow2.asm:asm-commons:9.4")
        force("org.ow2.asm:asm-analysis:9.4")
        force("org.ow2.asm:asm-util:9.4")

        exclude("org.objectweb.asm", "asm")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.sigpipe:jbsdiff:1.0")
    implementation("net.fabricmc:lorenz-tiny:4.0.2") // mapping generation
    implementation("org.cadixdev:lorenz-io-proguard:0.5.7") // mojmap reading
    implementation("org.sharegov:mjson:1.4.1") {
        isTransitive = false
    }
}

tasks.shadowJar {
    val prefix = "paperclip.libs"
    listOf("org.apache", "org.tukaani", "io.sigpipe", "mjson").forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }

    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/NOTICE.txt")
    relocate("org.objectweb.asm", "org.teneted.shadowed.asm")
}

/*
tasks.processResources {
    inputs.property("gameVersion", rootProject.property("gameVersion"))
    inputs.property("bannerVersion", rootProject.property("bannerVersion"))
    /*
    filesMatching("banner-install.properties") {
        expand(
            Pair("gameVersion", rootProject.property("gameVersion")),
            Pair("bannerVersion", rootProject.property("bannerVersion"))
        )
    }*/
}*/
