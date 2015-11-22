#!/bin/sh

sh build_webbit.sh
ant -f resources/build.xml

git add --all

git commit -m "Making new release"

git push

git subtree push --prefix distribution origin gh-pages
