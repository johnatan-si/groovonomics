#!/bin/sh

cd $1

for f in * 
do
	cd $f
	find . -name *.groovy > all_files.txt
	cd ..
done
