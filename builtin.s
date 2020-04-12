	.text
	.file	"builtin-function.c"
	.globl	print                   # -- Begin function print
	.p2align	2
	.type	print,@function
print:                                  # @print
	.cfi_startproc
# %bb.0:
	lui	a1, %hi(.L.str)
	addi	a1, a1, %lo(.L.str)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end0:
	.size	print, .Lfunc_end0-print
	.cfi_endproc
                                        # -- End function
	.globl	println                 # -- Begin function println
	.p2align	2
	.type	println,@function
println:                                # @println
	.cfi_startproc
# %bb.0:
	tail	puts
.Lfunc_end1:
	.size	println, .Lfunc_end1-println
	.cfi_endproc
                                        # -- End function
	.globl	printInt                # -- Begin function printInt
	.p2align	2
	.type	printInt,@function
printInt:                               # @printInt
	.cfi_startproc
# %bb.0:
	lui	a1, %hi(.L.str.2)
	addi	a1, a1, %lo(.L.str.2)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end2:
	.size	printInt, .Lfunc_end2-printInt
	.cfi_endproc
                                        # -- End function
	.globl	printlnInt              # -- Begin function printlnInt
	.p2align	2
	.type	printlnInt,@function
printlnInt:                             # @printlnInt
	.cfi_startproc
# %bb.0:
	lui	a1, %hi(.L.str.3)
	addi	a1, a1, %lo(.L.str.3)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end3:
	.size	printlnInt, .Lfunc_end3-printlnInt
	.cfi_endproc
                                        # -- End function
	.globl	getString               # -- Begin function getString
	.p2align	2
	.type	getString,@function
getString:                              # @getString
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	addi	a0, zero, 1024
	mv	a1, zero
	call	malloc
	mv	s0, a0
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	mv	a1, s0
	call	__isoc99_scanf
	mv	a0, s0
	lw	s0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end4:
	.size	getString, .Lfunc_end4-getString
	.cfi_endproc
                                        # -- End function
	.globl	getInt                  # -- Begin function getInt
	.p2align	2
	.type	getInt,@function
getInt:                                 # @getInt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	lui	a0, %hi(.L.str.2)
	addi	a0, a0, %lo(.L.str.2)
	addi	a1, sp, 8
	call	__isoc99_scanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end5:
	.size	getInt, .Lfunc_end5-getInt
	.cfi_endproc
                                        # -- End function
	.globl	toString                # -- Begin function toString
	.p2align	2
	.type	toString,@function
toString:                               # @toString
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -96
	.cfi_def_cfa_offset 96
	sw	ra, 92(sp)
	sw	s0, 88(sp)
	sw	s1, 84(sp)
	sw	s2, 80(sp)
	sw	s3, 76(sp)
	sw	s4, 72(sp)
	sw	s5, 68(sp)
	sw	s6, 64(sp)
	sw	s7, 60(sp)
	sw	s8, 56(sp)
	sw	s9, 52(sp)
	sw	s10, 48(sp)
	sw	s11, 44(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	.cfi_offset s2, -16
	.cfi_offset s3, -20
	.cfi_offset s4, -24
	.cfi_offset s5, -28
	.cfi_offset s6, -32
	.cfi_offset s7, -36
	.cfi_offset s8, -40
	.cfi_offset s9, -44
	.cfi_offset s10, -48
	.cfi_offset s11, -52
	beqz	a0, .LBB6_6
# %bb.1:
	srli	s10, a0, 31
	beqz	s10, .LBB6_3
# %bb.2:
	neg	a0, a0
.LBB6_3:
	addi	a1, zero, 1
	blt	a0, a1, .LBB6_7
# %bb.4:
	mv	a5, zero
	addi	a6, sp, 16
	lui	a2, 838861
	addi	a2, a2, -819
	addi	a3, zero, 10
	addi	a4, zero, 9
.LBB6_5:                                # =>This Inner Loop Header: Depth=1
	mv	s0, a0
	addi	s4, a5, 1
	slli	a0, a5, 16
	srai	a0, a0, 16
	slli	a0, a0, 1
	add	a5, a6, a0
	mulhu	a0, s0, a2
	srli	a0, a0, 3
	mul	a1, a0, a3
	sub	a1, s0, a1
	sh	a1, 0(a5)
	mv	a5, s4
	bltu	a4, s0, .LBB6_5
	j	.LBB6_8
.LBB6_6:
	addi	a0, zero, 2
	mv	a1, zero
	call	malloc
	addi	a1, zero, 48
	sb	a1, 0(a0)
	sb	zero, 1(a0)
	j	.LBB6_23
.LBB6_7:
	mv	s4, zero
.LBB6_8:
	slli	a0, s4, 16
	srai	s1, a0, 16
	add	s2, s10, s1
	addi	a0, s2, 1
	srai	a1, a0, 31
	call	malloc
	beqz	s10, .LBB6_10
# %bb.9:
	addi	a1, zero, 45
	sb	a1, 0(a0)
.LBB6_10:
	addi	a1, zero, 1
	blt	s1, a1, .LBB6_22
# %bb.11:
	lui	a1, 16
	addi	a2, a1, -1
	and	s0, s4, a2
	addi	a2, zero, 16
	addi	t0, s1, -1
	bgeu	s0, a2, .LBB6_13
# %bb.12:
	mv	s3, zero
	mv	a1, zero
	j	.LBB6_20
.LBB6_13:
	sw	s0, 4(sp)
	sw	s2, 8(sp)
	addi	a1, a1, -16
	and	s3, s4, a1
	addi	a2, s3, -16
	sltu	a1, a2, s3
	addi	a1, a1, -1
	slli	a4, a1, 28
	srli	a5, a2, 4
	or	s0, a5, a4
	addi	a4, s0, 1
	or	a2, a2, a1
	andi	a3, a4, 1
	sw	a3, 0(sp)
	sw	s10, 12(sp)
	beqz	a2, .LBB6_16
# %bb.14:
	mv	s2, zero
	mv	t4, zero
	srli	a1, a1, 4
	sltu	s0, a4, s0
	add	a1, a1, s0
	sub	t3, a4, a3
	sltu	a4, a4, a3
	sub	t2, a1, a4
	lw	t1, 12(sp)
.LBB6_15:                               # =>This Inner Loop Header: Depth=1
	sub	a1, t0, s2
	slli	a1, a1, 1
	addi	a3, sp, 16
	add	a1, a3, a1
	lh	t5, -14(a1)
	lh	t6, -12(a1)
	lh	s5, -10(a1)
	lh	s6, -8(a1)
	lh	ra, -6(a1)
	lh	s1, -4(a1)
	lh	a5, -2(a1)
	lh	a4, 0(a1)
	lh	s7, -30(a1)
	lh	s8, -28(a1)
	lh	s9, -26(a1)
	lh	s10, -24(a1)
	lh	s11, -22(a1)
	lh	s0, -20(a1)
	lh	a6, -18(a1)
	lh	a1, -16(a1)
	addi	a7, a4, 48
	addi	a5, a5, 48
	addi	s1, s1, 48
	addi	ra, ra, 48
	addi	s6, s6, 48
	addi	s5, s5, 48
	addi	t6, t6, 48
	addi	a2, t5, 48
	addi	t5, a1, 48
	addi	a6, a6, 48
	addi	s0, s0, 48
	addi	s11, s11, 48
	addi	s10, s10, 48
	addi	s9, s9, 48
	addi	s8, s8, 48
	addi	a1, s7, 48
	or	a4, s2, t1
	add	a4, a0, a4
	sb	a2, 7(a4)
	sb	t6, 6(a4)
	sb	s5, 5(a4)
	sb	s6, 4(a4)
	sb	ra, 3(a4)
	sb	s1, 2(a4)
	sb	a5, 1(a4)
	sb	a7, 0(a4)
	sb	a1, 15(a4)
	sb	s8, 14(a4)
	sb	s9, 13(a4)
	sb	s10, 12(a4)
	sb	s11, 11(a4)
	sb	s0, 10(a4)
	sb	a6, 9(a4)
	sb	t5, 8(a4)
	ori	s9, s2, 16
	sub	a2, t0, s9
	slli	a2, a2, 1
	add	a2, a3, a2
	lh	a6, -14(a2)
	lh	a7, -12(a2)
	lh	t5, -10(a2)
	lh	t6, -8(a2)
	lh	s11, -6(a2)
	lh	a5, -4(a2)
	lh	s1, -2(a2)
	lh	s0, 0(a2)
	lh	s5, -30(a2)
	lh	s6, -28(a2)
	lh	s7, -26(a2)
	lh	s8, -24(a2)
	lh	s10, -22(a2)
	lh	a1, -20(a2)
	lh	a4, -18(a2)
	lh	a2, -16(a2)
	addi	ra, s0, 48
	addi	s1, s1, 48
	addi	a5, a5, 48
	addi	s11, s11, 48
	addi	t6, t6, 48
	addi	t5, t5, 48
	addi	a7, a7, 48
	addi	s0, a6, 48
	addi	a6, a2, 48
	mv	a3, t0
	addi	t0, a4, 48
	addi	a1, a1, 48
	addi	s10, s10, 48
	addi	s8, s8, 48
	addi	s7, s7, 48
	addi	s6, s6, 48
	addi	a2, s5, 48
	or	a4, s9, t1
	add	a4, a0, a4
	sb	s0, 7(a4)
	sb	a7, 6(a4)
	sb	t5, 5(a4)
	sb	t6, 4(a4)
	sb	s11, 3(a4)
	sb	a5, 2(a4)
	sb	s1, 1(a4)
	sb	ra, 0(a4)
	sb	a2, 15(a4)
	sb	s6, 14(a4)
	sb	s7, 13(a4)
	sb	s8, 12(a4)
	sb	s10, 11(a4)
	sb	a1, 10(a4)
	sb	t0, 9(a4)
	mv	t0, a3
	sb	a6, 8(a4)
	addi	a1, s2, 32
	sltu	a2, a1, s2
	addi	a4, t3, -2
	sltu	a5, a4, t3
	add	a5, t2, a5
	addi	t2, a5, -1
	or	a5, a4, t2
	add	t4, t4, a2
	mv	s2, a1
	mv	t3, a4
	bnez	a5, .LBB6_15
	j	.LBB6_17
.LBB6_16:
	mv	a1, zero
.LBB6_17:
	lw	s10, 12(sp)
	lw	a2, 0(sp)
	beqz	a2, .LBB6_19
# %bb.18:
	sub	a2, t0, a1
	slli	a2, a2, 1
	addi	a4, sp, 16
	add	a2, a4, a2
	lh	a6, -14(a2)
	lh	a7, -12(a2)
	mv	a3, t0
	lh	t0, -10(a2)
	lh	t2, -8(a2)
	lh	s5, -6(a2)
	lh	a5, -4(a2)
	lh	s1, -2(a2)
	lh	s0, 0(a2)
	lh	t3, -30(a2)
	lh	t4, -28(a2)
	lh	t5, -26(a2)
	lh	t6, -24(a2)
	lh	s2, -22(a2)
	lh	s6, -20(a2)
	lh	a4, -18(a2)
	lh	a2, -16(a2)
	addi	s7, s0, 48
	addi	s8, s1, 48
	addi	s9, a5, 48
	addi	s5, s5, 48
	addi	t2, t2, 48
	addi	a5, t0, 48
	addi	s0, a7, 48
	addi	s1, a6, 48
	addi	a6, a2, 48
	addi	a7, a4, 48
	addi	t0, s6, 48
	addi	s2, s2, 48
	addi	t6, t6, 48
	addi	t5, t5, 48
	addi	a2, t4, 48
	addi	a4, t3, 48
	or	a1, a1, s10
	add	a1, a0, a1
	sb	s1, 7(a1)
	sb	s0, 6(a1)
	sb	a5, 5(a1)
	sb	t2, 4(a1)
	sb	s5, 3(a1)
	sb	s9, 2(a1)
	sb	s8, 1(a1)
	sb	s7, 0(a1)
	sb	a4, 15(a1)
	sb	a2, 14(a1)
	sb	t5, 13(a1)
	sb	t6, 12(a1)
	sb	s2, 11(a1)
	sb	t0, 10(a1)
	mv	t0, a3
	sb	a7, 9(a1)
	sb	a6, 8(a1)
.LBB6_19:
	lui	a1, 16
	addi	a1, a1, -1
	and	a1, s4, a1
	xor	a2, s3, a1
	mv	a1, zero
	lw	s2, 8(sp)
	lw	s0, 4(sp)
	beqz	a2, .LBB6_22
.LBB6_20:
	addi	a2, sp, 16
.LBB6_21:                               # =>This Inner Loop Header: Depth=1
	sub	a4, t0, s3
	slli	a4, a4, 1
	add	a4, a2, a4
	lb	a4, 0(a4)
	addi	a4, a4, 48
	add	a5, s3, s10
	add	a5, a0, a5
	addi	a3, s3, 1
	sltu	s1, a3, s3
	add	a1, a1, s1
	xor	s1, a3, s0
	or	s1, s1, a1
	sb	a4, 0(a5)
	mv	s3, a3
	bnez	s1, .LBB6_21
.LBB6_22:
	add	a1, a0, s2
	sb	zero, 0(a1)
.LBB6_23:
	lw	s11, 44(sp)
	lw	s10, 48(sp)
	lw	s9, 52(sp)
	lw	s8, 56(sp)
	lw	s7, 60(sp)
	lw	s6, 64(sp)
	lw	s5, 68(sp)
	lw	s4, 72(sp)
	lw	s3, 76(sp)
	lw	s2, 80(sp)
	lw	s1, 84(sp)
	lw	s0, 88(sp)
	lw	ra, 92(sp)
	addi	sp, sp, 96
	ret
.Lfunc_end6:
	.size	toString, .Lfunc_end6-toString
	.cfi_endproc
                                        # -- End function
	.globl	string_length           # -- Begin function string_length
	.p2align	2
	.type	string_length,@function
string_length:                          # @string_length
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strlen
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end7:
	.size	string_length, .Lfunc_end7-string_length
	.cfi_endproc
                                        # -- End function
	.globl	string_substring        # -- Begin function string_substring
	.p2align	2
	.type	string_substring,@function
string_substring:                       # @string_substring
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -32
	.cfi_def_cfa_offset 32
	sw	ra, 28(sp)
	sw	s0, 24(sp)
	sw	s1, 20(sp)
	sw	s2, 16(sp)
	sw	s3, 12(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	.cfi_offset s2, -16
	.cfi_offset s3, -20
	mv	s2, a2
	mv	s3, a0
	sub	s0, a1, a0
	addi	a0, s0, 1
	srai	a1, a0, 31
	call	malloc
	mv	s1, a0
	add	a1, s2, s3
	mv	a2, s0
	call	memcpy
	add	a0, s1, s0
	sb	zero, 0(a0)
	mv	a0, s1
	lw	s3, 12(sp)
	lw	s2, 16(sp)
	lw	s1, 20(sp)
	lw	s0, 24(sp)
	lw	ra, 28(sp)
	addi	sp, sp, 32
	ret
.Lfunc_end8:
	.size	string_substring, .Lfunc_end8-string_substring
	.cfi_endproc
                                        # -- End function
	.globl	string_parseInt         # -- Begin function string_parseInt
	.p2align	2
	.type	string_parseInt,@function
string_parseInt:                        # @string_parseInt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	lui	a1, %hi(.L.str.2)
	addi	a1, a1, %lo(.L.str.2)
	addi	a2, sp, 8
	call	__isoc99_sscanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end9:
	.size	string_parseInt, .Lfunc_end9-string_parseInt
	.cfi_endproc
                                        # -- End function
	.globl	string_ord              # -- Begin function string_ord
	.p2align	2
	.type	string_ord,@function
string_ord:                             # @string_ord
	.cfi_startproc
# %bb.0:
	add	a0, a1, a0
	lb	a0, 0(a0)
	ret
.Lfunc_end10:
	.size	string_ord, .Lfunc_end10-string_ord
	.cfi_endproc
                                        # -- End function
	.globl	_array_size             # -- Begin function _array_size
	.p2align	2
	.type	_array_size,@function
_array_size:                            # @_array_size
	.cfi_startproc
# %bb.0:
	lw	a0, -8(a0)
	ret
.Lfunc_end11:
	.size	_array_size, .Lfunc_end11-_array_size
	.cfi_endproc
                                        # -- End function
	.globl	string_add              # -- Begin function string_add
	.p2align	2
	.type	string_add,@function
string_add:                             # @string_add
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	sw	s1, 4(sp)
	sw	s2, 0(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	.cfi_offset s1, -12
	.cfi_offset s2, -16
	mv	s2, a1
	mv	s1, a0
	call	strlen
	mv	s0, a0
	mv	a0, s2
	call	strlen
	add	a0, a0, s0
	addi	a0, a0, 1
	srai	a1, a0, 31
	call	malloc
	mv	s0, a0
	mv	a1, s1
	call	strcpy
	mv	a0, s0
	mv	a1, s2
	call	strcat
	mv	a0, s0
	lw	s2, 0(sp)
	lw	s1, 4(sp)
	lw	s0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end12:
	.size	string_add, .Lfunc_end12-string_add
	.cfi_endproc
                                        # -- End function
	.globl	string_eq               # -- Begin function string_eq
	.p2align	2
	.type	string_eq,@function
string_eq:                              # @string_eq
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	seqz	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end13:
	.size	string_eq, .Lfunc_end13-string_eq
	.cfi_endproc
                                        # -- End function
	.globl	string_ne               # -- Begin function string_ne
	.p2align	2
	.type	string_ne,@function
string_ne:                              # @string_ne
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	snez	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end14:
	.size	string_ne, .Lfunc_end14-string_ne
	.cfi_endproc
                                        # -- End function
	.globl	string_lt               # -- Begin function string_lt
	.p2align	2
	.type	string_lt,@function
string_lt:                              # @string_lt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	srli	a0, a0, 31
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end15:
	.size	string_lt, .Lfunc_end15-string_lt
	.cfi_endproc
                                        # -- End function
	.globl	string_le               # -- Begin function string_le
	.p2align	2
	.type	string_le,@function
string_le:                              # @string_le
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	slti	a0, a0, 1
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end16:
	.size	string_le, .Lfunc_end16-string_le
	.cfi_endproc
                                        # -- End function
	.globl	string_gt               # -- Begin function string_gt
	.p2align	2
	.type	string_gt,@function
string_gt:                              # @string_gt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	sgtz	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end17:
	.size	string_gt, .Lfunc_end17-string_gt
	.cfi_endproc
                                        # -- End function
	.globl	string_ge               # -- Begin function string_ge
	.p2align	2
	.type	string_ge,@function
string_ge:                              # @string_ge
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	call	strcmp
	not	a0, a0
	srli	a0, a0, 31
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end18:
	.size	string_ge, .Lfunc_end18-string_ge
	.cfi_endproc
                                        # -- End function
	.type	.L.str,@object          # @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"%s"
	.size	.L.str, 3

	.type	.L.str.2,@object        # @.str.2
.L.str.2:
	.asciz	"%d"
	.size	.L.str.2, 3

	.type	.L.str.3,@object        # @.str.3
.L.str.3:
	.asciz	"%d\n"
	.size	.L.str.3, 4

	.ident	"clang version 6.0.0-1ubuntu2 (tags/RELEASE_600/final)"
	.section	".note.GNU-stack","",@progbits
