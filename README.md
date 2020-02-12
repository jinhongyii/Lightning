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
  - adce
  - sccp
  - cse
  - cfg simplify
  - inst combine
  - inlining
- optims to be done  
  - loop invariant
  - alias analysis
  - dead argument elimination
  - tail call elimination