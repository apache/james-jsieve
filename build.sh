#!/bin/sh

echo
echo "jSieve Build System"
echo "-------------------"

export OLD_ANT_HOME=$ANT_HOME

ANT_HOME=./tools
export ANT_HOME

export OLD_CLASSPATH=$CLASSPATH

${ANT_HOME}/bin/ant -emacs $@

export CLASSPATH=$OLD_CLASSPATH
export ANT_HOME=$OLD_ANT_HOME
