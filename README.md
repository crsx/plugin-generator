# plugin-generator
CRSX Plugin Generator

This is a project to generate plugins which enable editing
Domain Specific Language files used either for or with CRSX.

There are two kinds of plugins we are working to generate:

1. PG-defined Domain Specific Language

> A PG file defines  a  domain specific  language,  generally for  a
> compiler intermediate format, and once the PG file has been defined,
> it would be nice to have plugins used to edit this syntax in various
> development environments.

> Since the PG file already defines the syntax enough to parse files
> of this format, the GUI plugins for the various development
> environments should be able to be auto-generated.

2. CRSX rule definition files

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
