<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <relativePath>../${package}.parent/pom.xml</relativePath>
        <groupId>${package}</groupId>
        <artifactId>parent</artifactId>
        <version>${arguments.version}<#if arguments.snapshot>-SNAPSHOT</#if></version>
    </parent>

    <artifactId>${package}.ui</artifactId>
    <packaging>eclipse-plugin</packaging>

    <name>CRSX UI</name>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-clean-plugin</artifactId>
                <executions>
                    <execution>
                        <id>gen-clean</id>
                        <phase>clean</phase>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.eclipse.xtend</groupId>
                <artifactId>xtend-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
