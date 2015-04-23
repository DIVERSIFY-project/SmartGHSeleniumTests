from __future__ import with_statement
from fabric.api import *
from fabric.operations import run, put

env.hosts = ['schumi.dsg.cs.tcd.ie', '10.63.0.19']
#env.hosts = ['10.63.0.19']
home_dir = '/home/vivek'
code_dir = home_dir + '/github/SmartGHSeleniumTests/'
mvn_dir = home_dir + '/.m2'

def sayHello():
    run("uname -a")

def ensureDir():
    with settings(warn_only=True):
        if run("test -d %s" % code_dir).failed:
            run("mkdir -p %s" % code_dir)


def getFromRepo():
    ensureDir()
    run("git clone \
https://github.com/DIVERSIFY-project/SmartGHSeleniumTests.git \
%s" % code_dir)
    with cd(code_dir):
        run("git pull origin master")

def copyMavenSettings():
    """
    Copy the settings file for maven to access repo through proxy
    """
    with settings(warn_only=True):
        if run("test -d %s" % mvn_dir).failed:
            run("mkdir -p %s" % mvn_dir)
    put(mvn_dir + '/settings.xml', mvn_dir + '/settings.xml')

def copyPom():
    """
    Only needs to be called if we have changed the pom. This will copy the pom 
    from the fab machine to all the target machines
    """
    ensureDir()
    put('pom.xml', code_dir)

def copyAllTests():
    """
    The default location of firefox in the source code is wonky. Fix it before 
    compiling and running
    """
    ensureDir()
    test_location = 'src/test/java/org/diversify/sgh/test/'
    test_file = 'AllTests.java'
    with cd(code_dir):
        put(test_location + test_file, test_location + test_file)

@parallel
def mavenTest():
    """
    Calls 'mvn clean && mvn package'
    """
    ensureDir()
    with cd(code_dir):
        run("mvn package")

