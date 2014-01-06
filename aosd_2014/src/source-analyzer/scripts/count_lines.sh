#!/bin/sh

cd $1

for f in * 
do
	cd $f
	find . -name *.groovy | xargs cat | wc -l > line_count.txt
	cd ..
done
