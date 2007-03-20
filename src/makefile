# This makefile requires the following variables to be set
# and to be pointing to existing directories:
#
# - GROOVE_JAR_DIR   : directory where the external jars can be found
#                      default value: ../lib
# - GROOVE_CLASS_DIR : directory into which the classes are to be compiled
#                      default value: ../classes
# - GROOVE_RESOURCE_DIR : directory where the resource files are to be sent
#                      default value: ../resources
# - GROOVE_DOC_DIR   : directory where the javadoc is to be sent
#                      default value: ../doc

# The directory where the external jars can be found
    GROOVE_JAR_DIR = ../lib
# The top directory into which all classes are to be compiled
    GROOVE_CLASS_DIR = ../classes
# The directory into which all resource files are to be copied
    GROOVE_RESOURCE_DIR = ../resources
# The directory where the javadoc is to be sent
    GROOVE_DOC_DIR = ../doc
# The name of the top-level groove package
    GROOVE_ROOT_PACKAGE = groove

# The directory into which the groove (sub)packages are to be compiled
GROOVE_PACKAGE_DIR = $(GROOVE_CLASS_DIR)/$(GROOVE_ROOT_PACKAGE)
# Options for the java compiler (to be overridden)
JAVAC_OPTIONS = -g

#Validator
validator_classes = Validator
validator_files = $(validator_classes:%=io/%)

validator: $(validator_files:%=$(GROOVE_PACKAGE_DIR)/%.class)


#Java bytecode translator
jvm_classes = JBCTranslator ClassfileVisitor GraphVarReplacer Processed
jvm_files = $(jvm_classes:%=jvm/%)

jvm: $(jvm_files:%=$(GROOVE_PACKAGE_DIR)/%.class)

gif_files = $(wildcard resources/*.gif)

jpg_files = $(wildcard resources/*.jpg)

property_files = $(wildcard resources/*.properties)

resources : $(gif_files:resources/%=$(GROOVE_RESOURCE_DIR)/%) \
            $(jpg_files:resources/%=$(GROOVE_RESOURCE_DIR)/%) \
	    $(property_files:resources/%=$(GROOVE_RESOURCE_DIR)/%)

core_packages = graph trans rel lts util gui io gxl

all_packages = $(core_packages) test

appls = Editor Generator Simulator Imager ModelChecker

$(all_packages) :
	./ljavac $(JAVAC_OPTIONS) $(GROOVE_ROOT_PACKAGE)/$@/*.java

$(GROOVE_CLASS_DIR)/%.class : %.java
	./ljavac $(JAVAC_OPTIONS) $*.java

$(GROOVE_RESOURCE_DIR)/% : resources/%
	cp resources/$* $(GROOVE_RESOURCE_DIR)

all : $(all_packages) root

core : $(core_packages) root

root : $(appls:%=$(GROOVE_PACKAGE_DIR)/%.class) resources

doc : 
	./ljavadoc -d $(GROOVE_DOC_DIR) $(all_packages:%=$(GROOVE_ROOT_PACKAGE).%)

core_doc : 
	./ljavadoc $(core_packages:%=$(GROOVE_ROOT_PACKAGE).%)

clean :
	rm -rf *.jar */*.class */*.wmf
	rm -rf $(GROOVE_CLASS_DIR)/*
	rm -rf $(GROOVE_DOC_DIR)/*
	rm -rf $(GROOVE_RESOURCE_DIR)/*
