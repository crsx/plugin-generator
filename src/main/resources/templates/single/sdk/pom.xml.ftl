<?xml version="1.0" encoding="UTF-8"?>
<project>
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <relativePath>../${package}.parent/pom.xml</relativePath>
    <groupId>${package}</groupId>
    <artifactId>parent</artifactId>
    <version>${arguments.version}<#if arguments.snapshot>-SNAPSHOT</#if></version>
  </parent>

  <artifactId>${package}.sdk</artifactId>
  <packaging>eclipse-feature</packaging>

  <name>${name} Feature</name>
</project>
