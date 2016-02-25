# plugin-generator
CRSX Plugin Generator

This is a project to generate plugins which enable editing
Domain Specific Language files used either for or with CRSX.

There are two kinds of plugins we are working to generate:

(1) PG-defined Domain Specific Language

> A PG file defines  a  domain specific  language,  generally for  a
> compiler intermediate format, and once the PG file has been defined,
> it would be nice to have plugins used to edit this syntax in various
> development environments.

> Since the PG file already defines the syntax enough to parse files
> of this format, the GUI plugins for the various development
> environments should be able to be auto-generated.

(2) CRSX rule definition files

> One special feature of CRSX is the ability to use the "native"
> format defined in the PG file within CRSX rules definitions.

> This means that when editing code used to define a CRSX-based
> compiler, the code includes CRSX code and also includes embedded
> DSL-code for the various intermediate representations, as well as
> CRSX code embedded within the DSL code.

> Again, a developer would prefer a GUI plugin to the various
> development frameworks that understands and assists with editing
> this highly-customized format.

These reasonable PG-specific plugins have long been dreamed-of as an
aid that makes writing CRSX-based compilers much easier and more
maintainable.  This project hopes to provide that functionality.

This project is provided under the Eclipse Public License 1.0.

## Building and usage of the plugin-generator

You can build the tool using maven by simply by invoking default goal:

```
mvn
```

The tool can be executed using wrapper shell script `pgenerator.sh`

### Command line options

If you want to see list of supported command line options run

```
./pgenerator.sh --help
```

### Usage examples:

* Take `grammar.pg` file and produce `grammarPlugin.zip` containing sources for
eclipse plugin supporting single language from `grammar.pg`:

```
./pgenerator.sh --zip --output grammarPlugin.zip grammar.pg
```

* Take `grammar.pg` and produce sources for eclipse plugin supporting single
language from `grammar.pg` into directory /home/user/pluginsources:

```
./pgenerator.sh --output /home/user/pluginsources grammar.pg
```

* Take `grammar.pg` and produce `grammar.xtext` containing just xtext grammar
for the language from `grammar.pg`:

```
./pgenerator.sh --xtext grammar.pg
```

* Take `grammar.pg` and produce archive `repository.zip` containing update site with CRSX plugin supporting embedded language specified by `grammar.pg`.

```
./pgenerator.sh --crsx gramar.pg --updatesite --zip --output repository.zip
```

* Take `grammar.pg`, generate sources for both CRSX plugin with support for embedded language
defined by `grammar.pg` and build those plugins using maven located at `/opt/maven`, producing updatesite containing both plugins in `com.example.language.multiple.updatesite/target/repository`

```
./pgenerator.sh --all grammar.pg --output ~/plugins_sources/ --maven /opt/maven --build
```
### Building generated plugin with maven

When running with `--single` or `--crsx` switch, the utility will produce five project
directories with following names (expecting the grammar package to be
`com.example.language` ):

 * `com.example.language`
 * `com.example.language.parent`
 * `com.example.language.sdk`
 * `com.example.language.ui`
 * `com.example.language.updatesite`

Generated plugin can be built with maven version 3+ by running following commands:

```
cd com.example.language.parent
mvn package
```

After succesfull build of the plugin you can install it just by pointing your
eclipse to `com.example.language.updatesite/target/repository` directory.

### Building generated plugin directly from utility

You need to have maven v3+ installed on your system in order to build the plugins directly from the utility. Location of maven home directory must be specified either by setting M2_HOME environment variable, or by explicitly providing path to the directory with `--maven <MAVEN_HOME>` command line attribute. You can choose one of the two build modes:

* `--build` - utility will invoke `maven package` goal in parent project
  directory right after the sources are generated. When build process is finished you should find eclipse update site repository in `com.example.language.updatesitee/target/repository` directory

* `--updatesite` - utility will generate all sources into temporary directory and then builds them using maven. After the build is done
eclipse update site repository is moved to location specified by `--output` command line argument. This option can be also combined with `--zip` switch (in that case, zip archive containing eclipse update repository is created).

### Plugin dependencies

By default maven pom.xml files in generated plugins are configured not to include dependencies in the resulting repositories. 
This can be changed by using `-d`/`--include_dependencies` when running plugin generator. Plugin repository will then contain all dependencies. 
This can increase repository size significantly (Eclipse repository of simple plugin with all Xtext dependecies has ~90MiB).

