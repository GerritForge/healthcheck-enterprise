# Plugin to verify the Gerrit health status with extended enterprise-level checks

Gerrit plugin that exposes enterprise-level configuration and runtime health check
allowing to have a single entry point to check the availability of the services
that Gerrit exposes.

This plugin inherits all the existing checks of the equivalent
[Open-Source healthcheck](https://gerrit.googlesource.com/plugins/healthcheck/) plugin.

## How to build

Clone or link this plugin to the plugins directory of Gerrit's source tree,
and then run bazel build on the plugin's directory.

**NOTE**: In order to build this plugin, you also need to have the Open-Source healthcheck
plugin cloned into the `$GERRIT_SITE/plugins/healthcheck` directory.

Example:

```
git clone --recursive https://gerrit.googlesource.com/gerrit
git clone https://gerrit.googlesource.com/plugins/healthcheck
git clone https://review.gerrithub.io/GerritForge/healthcheck-enterprise
pushd gerrit/plugins && ln -s ../../healthcheck . && popd
pushd gerrit/plugins && ln -s ../../healthcheck-enterprise . && popd
cd gerrit && bazel build plugins/healthcheck-enterprise
```

The output plugin jar is created in:

```
bazel-genfiles/plugins/healthcheck-enterprise/healthcheck-enterprise.jar
```

## How to install

Copy the healthcheck-enterprise.jar into the Gerrit's /plugins directory and wait for
the plugin to be automatically loaded.
The healthcheck-enterprise plugin is compatible with both primary Gerrit setups and
Gerrit replicas. The only difference to bear in mind is that some checks will be
automatically disabled on replicas (e.g. query changes) because the associated
subsystem is switched off.

## How to use

The healthcheck-enterprise plugin exposes a single endpoint under its root URL and
provides a JSON output of the Gerrit health status.

The HTTP status code returned indicates whether Gerrit is healthy (HTTP status 200)
or has some issues (HTTP status 500).

The HTTP response payload is a JSON output that contains the details of the checks
performed.

- ts: epoch timestamp in millis of the test
- elapsed: elapsed time in millis to perform the check
- querychanges: check that Gerrit can query changes
- reviewdb: check that Gerrit can connect and query ReviewDb
- projectslist: check that Gerrit can list projects
- jgit: check that Gerrit can access repositories

Each check returns a JSON payload with the following information:

- ts: epoch timestamp in millis of the individual check
- elapsed: elapsed time in millis to complete the check
- result: result of the health check

    - passed: the check passed successfully
    - disabled: the check was disabled
    - failed: the check failed with an error
    - timeout: the check took too long and timed out

Example of a healthy Gerrit response:

```
GET /config/server/healthcheck-enterprise~status

200 OK
Content-Type: application/json

)]}'
{
  "ts": 139402910202,
  "elapsed": 100,
  "querychanges": {
    "ts": 139402910202,
    "elapsed": 20,
    "result": "passed"
  },
  "reviewdb": {
    "ts": 139402910202,
    "elapsed": 50,
    "result": "passed"
  },
  "projectslist": {
    "ts": 139402910202,
    "elapsed": 100,
    "result": "passed"
  },
  "jgit": {
    "ts": 139402910202,
    "elapsed": 80,
    "result": "passed"
  }
}
```

Example of a Gerrit instance with the projects list timing out:

```
GET /config/server/healthcheck-enterprise~status

500 ERROR
Content-Type: application/json

)]}'
{
  "ts": 139402910202,
  "elapsed": 100,
  "querychanges": {
    "ts": 139402910202,
    "elapsed": 20,
    "result": "passed"
  },
  "reviewdb": {
    "ts": 139402910202,
    "elapsed": 50,
    "result": "passed"
  },
  "projectslist": {
    "ts": 139402910202,
    "elapsed": 100,
    "result": "timeout"
  },
  "jgit": {
    "ts": 139402910202,
    "elapsed": 80,
    "result": "passed"
  }
}
```

It's also possible to artificially make the healthcheck-enterprise fail by placing a file
at a configurable path specified like:

```
[healtcheck]
  failFileFlaPath="data/healthcheck-enterprise/fail"
```

This will make the healthcheck-enterprise endpoint return 500 even if the node is otherwise
healthy. This is useful when a node needs to be removed from the pool of
available Gerrit instance while it undergoes maintenance.

**NOTE**: If the path starts with `/` then even paths outside of Gerrit's home
will be checked. If the path starts WITHOUT `/` then the path is relative to
Gerrit's home.

**NOTE**: The file needs to be a real file rather than a symlink.


## Metrics

As for all other endpoints in Gerrit, some metrics are automatically emitted when the
`/config/server/healthcheck-enterprise~status` endpoint is hit
(thanks to the [Dropwizard](https://metrics.dropwizard.io/3.1.0/manual/core/) library).

Some additional metrics are also produced to give extra insights on their result about results and latency of healthcheck
sub component, such as jgit, reviewdb, etc.

More information can be found in the [metrics.md](resources/Documentation/metrics.md) file.
