
set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.7.2-complete.jar:./bin Main"
cat > code.txt   # save everything in stdin to program.txt
$CCHK | $RISCV/bin/llc --march=riscv32 --mattr=+m | cat

