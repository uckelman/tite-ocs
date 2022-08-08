# 
# To compile:
#
# 1. Put all JARs from VASSAL in lib.
# 2. Run 'make'.
# 3. Class file hierarchy will now be in classes.
#

SRCDIR:=src
CLASSDIR:=classes
CLASSPATH:=$(CLASSDIR):lib/Vengine.jar

JAVAPATH:=/usr/bin
JAVAC:=$(JAVAPATH)/javac
JAVACFLAGS:=-d $(CLASSDIR) -source 11 -target 11 -Xlint -classpath $(CLASSPATH) -sourcepath $(SRCDIR)

all: $(CLASSDIR)
	$(JAVAC) $(JAVACFLAGS) $(shell find $(SRCDIR) -name '*.java')

$(CLASSDIR):
	mkdir -p $(CLASSDIR)

clean:
	$(RM) -r $(CLASSDIR)/*

.PHONY: all clean
