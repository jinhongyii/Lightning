# Lightning: A powerful compiler from a C-like language to RISCV
##### introduction of all parts:

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
- optimizations
  - transformations
    - mem2reg 
    - adce
    - sccp
    - cse
    - cfg simplify
    - inst combine
    - inlining
    - licm
    - strength reduction
    - redundant load elimination 
    - dead store elimination
  - analysis
    - loop analysis
    - alias analysis
    - domtree
- codegen
  - graph allocator
  - live range splitting
  - peephole optimization

