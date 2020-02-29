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
  - licm
  - strength reduction
- optims to be done  
  - alias analysis
  - loop idiom recognition
  - loop rotation
  - tail call elimination