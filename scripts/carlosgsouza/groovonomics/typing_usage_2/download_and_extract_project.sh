#!/bin/sh

projectId=$1

cd /opt/groovonomics/temp/
rm -rf *
s3cmd get --config=/opt/groovonomics/s3cfg s3://carlosgsouza.groovonomics/dataset/projects/source/$projectId.zip $projectId.zip --force
unzip $projectId.zip
mv opt/groovonomics/temp/* .
rm -rf opt/
