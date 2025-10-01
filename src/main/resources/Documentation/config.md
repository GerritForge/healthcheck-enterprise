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