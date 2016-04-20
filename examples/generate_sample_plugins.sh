#!/bin/sh

if [ -z "$M2_HOME" ]; then
	echo "Maven home M2_HOME variable seems not to be set in your environment."
	echo "Please set M2_HOME to your Maven installation directory"
	exit;
fi

PGENERATOR_EXECUTABLE="./pgenerator.sh"

#Generate Eclipse plugins from pascal_simple.pg
cd ..
$PGENERATOR_EXECUTABLE --all --updatesite examples/pascal_simple.pg --start program --output examples/sample_repository
