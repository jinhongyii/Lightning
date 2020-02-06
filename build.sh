# this script is called when the judge is building your compiler.
# no argument will be passed in.
set -e
cd "$(dirname "$0")"
mkdir -p bin
find ./src -name *.java | javac -d bin -classpath "lib/antlr-4.7.2-complete.jar:/mnt/c/Users/jinho/.m2/repository/org/jetbrains/annotations/16.0.2/annotations-16.0.2.jar" @/dev/stdin