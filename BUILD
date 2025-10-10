load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)
load("//tools/bzl:junit.bzl", "junit_tests")

gerrit_plugin(
    name = "healthcheck-enterprise",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: healthcheck-enterprise",
        "Gerrit-Module: com.gerritforge.gerrit.plugins.healthcheck.enterprise.EnterpriseModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.healthcheck.HttpModule",
        "Gerrit-ApiModule: com.googlesource.gerrit.plugins.healthcheck.HealthCheckExtensionApiModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "//plugins/healthcheck",
    ],
)

junit_tests(
    name = "healthcheck-enterprise_tests",
    srcs = glob(
        [
            "src/test/java/**/*Test.java",
        ],
        exclude = ["src/test/java/**/Abstract*.java"],
    ),
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":healthcheck-enterprise__plugin_test_deps",
        ":healthcheck_enterprise_util",
        "//plugins/healthcheck",
    ],
)

java_library(
    name = "healthcheck-enterprise__plugin_test_deps",
    testonly = 1,
    srcs = glob(["src/test/java/**/Abstract*.java"]),
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":healthcheck-enterprise__plugin",
    ],
)

java_library(
    name = "healthcheck_enterprise_util",
    testonly = 1,
    srcs = glob(
        ["src/test/java/**/*.java"],
        exclude = [
            "src/test/java/**/*Test.java",
            "src/test/java/**/*IT.java",
        ],
    ),
    deps = PLUGIN_TEST_DEPS + PLUGIN_DEPS + [
        ":healthcheck-enterprise__plugin",
        "//plugins/healthcheck",
    ],
)
