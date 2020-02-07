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
- optims to be done  
  - inst combine
  - inlining
  - loop invariant
  - alias analysis
