@PLUGIN@ configuration
======================

The @PLUGIN@
------------

The plugin does not require any configuration at all and exposes the additional enterprise checks
to the healthcheck HTTP endpoint for querying the service status.

```
GET /config/server/healthcheck~status

)]}'
{
  "ts": 139402910202,
  "elapsed": 120,
  "enterprisecheck": {
    "ts": 139402910202,
    "elapsed": 30,
    "result": "passed"
  }
}
```

> **NOTE**: The above example is for explanatory purpose only and it does not represent the real
> payload of the REST-API.

Settings
--------

The plugin allows to customize its behaviour through a specific
`healthcheck.config` file in the `$GERRIT_SITE/etc` directory.

Each section of the form `[healthcheck "<checkName>"]` can tailor the
behaviour of an individual `<checkName>`. The section `[healthcheck]`
defines the global defaults valid for all checks.

The following check names are available:

- `jvm_heap`: check JVM Heap usage percentage. `Enabled` by default.

Each check name can be disabled by setting the `enabled` parameter to **false**,
by default this parameter is set to **true**

The following parameters are available:

- `healthcheck.jvm_heap.threshold` :  JVM heap usage percentage above which the check is considered failed.

  Default: 80
