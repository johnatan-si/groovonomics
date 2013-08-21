#!/bin/sh

projectId=$1

cd /opt/groovonomics/temp/
unzip $projectId.zip
mv opt/groovonomics/temp/* .
rm -rf opt/
