<?xml version="1.0" encoding="UTF-8"?>
<project>
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <relativePath>../${mainPackage}.multiple.parent/pom.xml</relativePath>
    <groupId>${mainPackage}.multiple</groupId>
    <artifactId>parent</artifactId>
    <version>${arguments.version}<#if arguments.snapshot>-SNAPSHOT</#if></version>
  </parent>

  <artifactId>${mainPackage}.multiple.updatesite</artifactId>
  <packaging>eclipse-repository</packaging>

  <name>CRSX Update Site</name>

    <build>
        <plugins>
            <plugin>
                <groupId>org.eclipse.tycho</groupId>
                <artifactId>tycho-p2-director-plugin</artifactId>
                <version>${r"${tycho-version}"}</version>
                <executions>
                    <execution>
                        <id>materialize-products</id>
                        <goals>
                            <goal>materialize-products</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>archive-products</id>
                        <goals>
                            <goal>archive-products</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.eclipse.tycho</groupId>
                <artifactId>tycho-p2-repository-plugin</artifactId>
                <version>${r"${tycho-version}"}</version>
                <configuration>
                    <includeAllDependencies>${arguments.includeAllDependencies?c}</includeAllDependencies>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
