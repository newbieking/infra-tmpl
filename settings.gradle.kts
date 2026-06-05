pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.aliyun.com/repository/spring") }
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

include("infra-common")
include("infra-gateway")
include("infra-user")
include("infra-order")
include("infra-search")
include("infra-storage")
include("infra-event")
