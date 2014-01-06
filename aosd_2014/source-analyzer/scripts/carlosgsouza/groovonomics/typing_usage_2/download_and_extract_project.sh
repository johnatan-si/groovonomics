#!/bin/sh

projectId=$1
tempDir=$2

cd $tempDir
unzip $projectId.zip
mv opt/groovonomics/temp/* .
rm -rf opt/
