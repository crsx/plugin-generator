<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <relativePath>../${package}.parent/pom.xml</relativePath>
        <groupId>${package}</groupId>
        <artifactId>parent</artifactId>
        <version>${arguments.version}<#if arguments.snapshot>-SNAPSHOT</#if></version>
    </parent>

    <artifactId>${package}</artifactId>
    <packaging>eclipse-plugin</packaging>

    <name>${name} Core</name>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-clean-plugin</artifactId>
                <executions>
                    <execution>
                        <id>gen-clean</id>
                        <phase>clean</phase>
                        <!-- Clean up generated files in all projects -->
                        <configuration>
                            <filesets combine.children="append">
                                <fileset>
                                    <directory>${r"${basedir}"}/src-gen/</directory>
                                </fileset>
                                <fileset>
                                    <directory>${r"${basedir}"}/model/generated/</directory>
                                </fileset>
                                <fileset>
                                    <directory>${r"${basedir}"}/../${r"${project.artifactId}"}.ui/src-gen/</directory>
                                </fileset>
                                <fileset>
                                    <directory>${r"${basedir}"}/../${r"${project.artifactId}"}.tests/src-gen/</directory>
                                </fileset>
                            </filesets>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.2.1</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>java</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <includeProjectDependencies>false</includeProjectDependencies>
                    <includePluginDependencies>true</includePluginDependencies>
                    <mainClass>org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher</mainClass>
                    <arguments>
                        <argument>file://${r"${project.basedir}"}/src/${mwe2path}</argument>
                    </arguments>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>org.eclipse.xtext</groupId>
                        <artifactId>org.eclipse.xtext.xtext</artifactId>
                        <version>${r"${xtext.version}"}</version>
                    </dependency>
                    <dependency>
                        <groupId>org.eclipse.xtext</groupId>
                        <artifactId>org.eclipse.xtext.xbase</artifactId>
                        <version>${r"${xtext.version}"}</version>
                    </dependency>
                    <dependency>
                        <groupId>org.eclipse.equinox</groupId>
                        <artifactId>common</artifactId>
                        <version>3.6.200-v20130402-1505</version>
                    </dependency>
                </dependencies>
            </plugin>
            <plugin>
                <groupId>org.eclipse.xtend</groupId>
                <artifactId>xtend-maven-plugin</artifactId>
            </plugin>
        </plugins>
        <pluginManagement>
            <plugins>
                <!--This plugin's configuration is used to store Eclipse m2e settings
                    only. It has no influence on the Maven build itself. -->
                <plugin>
                    <groupId>org.eclipse.m2e</groupId>
                    <artifactId>lifecycle-mapping</artifactId>
                    <version>1.0.0</version>
                    <configuration>
                        <lifecycleMappingMetadata>
                            <pluginExecutions>
                                <pluginExecution>
                                    <pluginExecutionFilter>
                                        <groupId>
                                            org.codehaus.mojo
                                        </groupId>
                                        <artifactId>
                                            exec-maven-plugin
                                        </artifactId>
                                        <versionRange>
                                            [1.2.1,)
                                        </versionRange>
                                        <goals>
                                            <goal>java</goal>
                                        </goals>
                                    </pluginExecutionFilter>
                                    <action>
                                        <ignore></ignore>
                                    </action>
                                </pluginExecution>
                            </pluginExecutions>
                        </lifecycleMappingMetadata>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
