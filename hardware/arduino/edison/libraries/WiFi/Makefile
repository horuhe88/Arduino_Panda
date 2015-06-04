# Here are all the search directories for the various files:
 
NANOLIB ?= ../../../coretaio/tools/silverbutte/compiler/sysroot_nano-newlib
NANOC   = g++

################################################################################
# List of files comprising the various modules
################################################################################
C_FILES     	  ?= $(wildcard *.c)
CPP_FILES         ?= $(wildcard *.cpp)
H_FILES     	  ?= $(wildcard *.h)
S_FILES     	  ?= $(wildcard *.s)
LNK_FILE    	  ?= SilverButte.ld

.PHONY: sw clean tarball

sw : mybin.elf

stag.elf.map: sw
stag.elf.lst: sw

################################################################################
# Commands to build the silverbutte software
################################################################################

mybin.elf : $(C_FILES) $(CPP_FILES) $(H_FILES) $(S_FILES) 
	mkdir ./out
	$(NANOC) -Wall -fno-exceptions -fpermissive -Waddress -Wcomment -Wwrite-strings -Wconversion-null -Wsign-compare -Wunused-variable -Wformat-security -Wformat-extra-args -O0 -o ./out/mybin.out -static $(filter %.c %.cpp %.s, $^)
	#objdump -DSw ./out/mybin.elf > ./out/mybin.elf.lst


################################################################################
# Clean up the directory
################################################################################
clean:
	rm -fr  ./out/*.*
	rm -rf ./out
