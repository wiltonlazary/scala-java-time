This are the steps to make a release

```bash
sbt
+publishSigned
sonatypeRelease
docs/publishMicrosite
```

Important: Remember to clean between different scala.js versions

on 1.0.0-M6

```
SCALAJS_VERSION=1.0.0-M6 sbt
clean
+publishSigned
sonatyeRelease
```
