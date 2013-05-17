#!/bin/sh

s3cmd put --config=/opt/groovonomics/s3cfg /opt/groovonomics/temp/*.json s3://carlosgsouza.groovonomics/data/type_usage/class/ --force
