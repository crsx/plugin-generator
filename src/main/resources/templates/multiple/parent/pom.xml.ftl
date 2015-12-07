<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>${mainPackage}.multiple</groupId>
    <artifactId>parent</artifactId>
    <version>${arguments.version}<#if arguments.snapshot>-SNAPSHOT</#if></version>
    <packaging>pom</packaging>
    <modules>
<#list plugins as plugin>
        <module>../${plugin.pluginPackage}.parent</module>
</#list>
        <module>../${mainPackage}.multiple.updatesite</module>
    </modules>

    <properties>
        <tycho-version>0.23.0</tycho-version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <xtext.version>${arguments.xtextVersion}</xtext.version>
    </properties>

<#if !arguments.targetDefinitionUse>
    <repositories>
        <repository>
            <id>eclipse</id>
            <layout>p2</layout>
            <url>http://download.eclipse.org/releases/mars/201506241002/</url>
        </repository>
        <repository>
            <id>Xtext Update Site</id>
            <layout>p2</layout>
            <url>${arguments.xtextUpdateSite}</url>
        </repository>
    </repositories>
</#if>
    <build>
        <pluginManagement>
            <plugins>
                <!-- xtend-maven-plugin is in pluginManagement instead of in plugins
                    so that it doesn't run before the exec-maven-plugin's *.mwe2 gen; this way
                    we can list it after. -->

                <plugin>
                    <groupId>org.eclipse.xtend</groupId>
                    <artifactId>xtend-maven-plugin</artifactId>
                    <version>${r"${xtext.version}"}</version>
                    <executions>
                        <execution>
                            <goals>
                                <goal>compile</goal>
                                <goal>xtend-install-debug-info</goal>
                                <goal>testCompile</goal>
                                <goal>xtend-test-install-debug-info</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <outputDirectory>xtend-gen</outputDirectory>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-clean-plugin</artifactId>
                    <version>2.5</version>
                    <executions>
                        <execution>
                            <id>gen-clean</id>
                            <goals>
                                <goal>clean</goal>
                            </goals>
                            <configuration>
                                <filesets>
                                    <fileset>
                                        <directory>${r"${basedir}"}/xtend-gen</directory>
                                    </fileset>
                                </filesets>
                            </configuration>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>

        <plugins>
            <plugin>
                <groupId>org.eclipse.tycho</groupId>
                <artifactId>tycho-maven-plugin</artifactId>
                <version>${r"${tycho-version}"}</version>
                <extensions>true</extensions>
            </plugin>
<#if arguments.targetDefinitionUse>
            <plugin>
                <groupId>org.eclipse.tycho</groupId>
                <artifactId>target-platform-configuration</artifactId>
                <version>${r"${tycho-version}"}</version>
                <configuration>
                    <target>
                        <artifact>
                            <groupId>${mainPackage}</groupId>
                            <artifactId>${mainPackage}.target</artifactId>
                            <version>${arguments.version}<#if arguments.snapshot>-SNAPSHOT</#if></version>
                        </artifact>
                    </target>
                    <environments>
                        <environment>
                            <os>macosx</os>
                            <ws>cocoa</ws>
                            <arch>x86_64</arch>
                        </environment>
                        <environment>
                            <os>win32</os>
                            <ws>win32</ws>
                            <arch>x86_64</arch>
                        </environment>
                        <environment>
                            <os>linux</os>
                            <ws>gtk</ws>
                            <arch>x86_64</arch>
                        </environment>
                    </environments>
                </configuration>
            </plugin>
</#if>
        </plugins>
    </build>
</project>
