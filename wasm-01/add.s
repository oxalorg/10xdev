	.file	"add.c"
	.globaltype	__stack_pointer, i32
	.functype	add (i32, i32) -> (i32)
	.section	.text.add,"",@
	.hidden	add                             # -- Begin function add
	.globl	add
	.type	add,@function
add:                                    # @add
	.functype	add (i32, i32) -> (i32)
	.local  	i32
# %bb.0:
	global.get	__stack_pointer
	i32.const	16
	i32.sub 
	local.tee	2
	local.get	0
	i32.store	12
	local.get	2
	local.get	1
	i32.store	8
	local.get	2
	i32.load	12
	local.get	2
	i32.load	12
	i32.mul 
	local.get	2
	i32.load	8
	i32.add 
                                        # fallthrough-return
	end_function
                                        # -- End function
	.ident	"Homebrew clang version 20.1.2"
	.section	.custom_section.producers,"",@
	.int8	1
	.int8	12
	.ascii	"processed-by"
	.int8	1
	.int8	14
	.ascii	"Homebrew clang"
	.int8	6
	.ascii	"20.1.2"
	.section	.text.add,"",@
	.section	.custom_section.target_features,"",@
	.int8	8
	.int8	43
	.int8	11
	.ascii	"bulk-memory"
	.int8	43
	.int8	15
	.ascii	"bulk-memory-opt"
	.int8	43
	.int8	22
	.ascii	"call-indirect-overlong"
	.int8	43
	.int8	10
	.ascii	"multivalue"
	.int8	43
	.int8	15
	.ascii	"mutable-globals"
	.int8	43
	.int8	19
	.ascii	"nontrapping-fptoint"
	.int8	43
	.int8	15
	.ascii	"reference-types"
	.int8	43
	.int8	8
	.ascii	"sign-ext"
	.section	.text.add,"",@
