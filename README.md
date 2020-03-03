# Compiler2020
##### progress:

- parser 

  - use antlr4 to generate
- ast
  - quite normal
- semantic
  - quite straight-forward
  - follow the styles of tiger book
  - maybe have some bug now
- ir
  - use llvm ir
  - can test without having to implement a interpreter
- optims finished
  - mem2reg pass (ssa construction)
  - adce (dsa-based)
  - sccp
  - cse
  - cfg simplify
  - inst combine
  - inlining
  - licm (dsa-based)
  - strength reduction
  - redundant load elimination (dsa-based)
- optims to be done  (sorted by priority)
  - promote memory to scalar in licm
  - dead store elimination (dsa-based)
  - gvn
  - loop idiom recognition
  - loop rotation
  - tail call elimination