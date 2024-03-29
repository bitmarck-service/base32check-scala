# base32check-scala

[![test](https://github.com/bitmarck-service/base32check-scala/actions/workflows/test.yml/badge.svg)](https://github.com/bitmarck-service/base32check-scala/actions/workflows/test.yml)
[![Release Notes](https://img.shields.io/github/release/bitmarck-service/base32check-scala.svg?maxAge=3600)](https://github.com/bitmarck-service/base32check-scala/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/de.bitmarck.bms/base32check-scala_2.13)](https://search.maven.org/artifact/de.bitmarck.bms/base32check-scala_2.13)
[![Apache License 2.0](https://img.shields.io/github/license/bitmarck-service/base32check-scala.svg?maxAge=3600)](https://www.apache.org/licenses/LICENSE-2.0)

This project is a Scala implementation of [base32](https://en.wikipedia.org/wiki/Base32) as specified in [RFC 4648](https://tools.ietf.org/html/rfc4648#section-6) and the [base32check1](https://base32check.org/) algorithm.
For more information see https://base32check.org.

## Usage

### build.sbt

```sbt
// use this snippet for the JVM
libraryDependencies += "de.bitmarck.bms" %% "base32check-scala" % "0.1.0"

// use this snippet for JS, or cross-building
libraryDependencies += "de.bitmarck.bms" %%% "base32check-scala" % "0.1.0"
```

## License
This project uses the Apache 2.0 License. See the file called LICENSE.
