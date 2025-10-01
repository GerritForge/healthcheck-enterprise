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
        "Gerrit-PluginName: healthcheck",
        "Gerrit-Module: com.gerritforge.gerrit.plugins.healthcheck.enterprise.Module",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        ":healthcheck-neverlink",
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
        ":healthcheck-neverlink",
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
    name = "healthcheck-neverlink",
    neverlink = 1,
    exports = ["//plugins/healthcheck"],
)
