###############################################################################
# Makefile for NUANCE
#
# Most conventions in this Makefile were adapted from:
# 
# Miller, P.A. (1998). Recursive Make Considered Harmful,
#    AUUGN Journal of AUUG Inc., 19(1), pp. 14-25.
#
###############################################################################

# compiler to use
CC = javac

# the packages that are a part of NUANCE
PACKAGES := testsuite                \
            config                   \
            mode                     \
            fitness                  \
            problem                  \
            breeder                  \
            logger                   \
            stopcondition            \
            agent agent/s_expression \
            agent/ann                \
            ui ui/tui ui/gui         \
            numerics                 \
	    islands                  \
	    connection

# each package will add to this from its package.mk file
SRC     := Nuance.java

# include SRC files for each package
include $(patsubst %,%/package.mk, $(PACKAGES))

# determine the object files
CLASS_FILES := $(patsubst %.java,%.class, $(SRC))

# When we package up all of the code for the program, it will go to this file
JARFILE = ../nuance.jar

# The manifest file needed by the jar program. In its basic form, this just
# contains a listing for the class that contains our main() function
MANIFEST = manifest.txt

# we'd like some messages we print out to be in color. Here is the string for
# the color (cyan) that we print all important messages in. Any echos that use
# color must also have the -e option (e.g. @echo -e "echo here")
COLOR   := \x1B[36m
NOCOLOR := \x1B[0m



################################################################################
# variables involved in creating backups of NUANCE
################################################################################

# when performing a backup, these are the directories we'd like to include
BACKUP_DIRS := src

# the name of the backup file we will be creating (time-stamping added)
BACKUP_FILE := backup/nuance-$(shell date +%Y-%m-%d-%Hh%Mm).tar.tgz



################################################################################
# make commands
################################################################################

# Build all of our .class files
all: $(CLASS_FILES)

# Package everything up as a .jar file. Make sure everything is up-to-date first
jar: all
	@jar cvfm $(JARFILE) $(MANIFEST) \
	*.class $(patsubst %,%/*.class, $(PACKAGES))

# back up everything worth backing up
backup: clean
	@echo "Backing up: $(BACKUP_DIRS)"
	@(cd ..; tar -zcf $(BACKUP_FILE) $(BACKUP_DIRS))
	@echo -e "$(COLOR)New backup created: $(BACKUP_FILE)$(NOCOLOR)"

# Make a .class file from a .java file if the .class file is 
# older than the current .java file. 
#
# SHIT! This is broken; we should also be updating if our interface, extended
# class, or imported classes also change. Fix this soon!!
%.class : %.java
	$(CC) $<

# Clear out all of our class files, jar files, and anything we might have 
# edited with emacs
clean:
	@echo "nuance source files cleaned"
	@rm -f *.class $(patsubst %,%/*.class, $(PACKAGES))
	@rm -f *~ $(patsubst %,%/*~, $(PACKAGES))
