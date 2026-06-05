pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/spring") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
    }
}

rootProject.name = "infra-tmpl"
