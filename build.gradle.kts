plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.xettuyen.main.MainApp")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    jvmArgs("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8")
}