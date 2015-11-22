#!/bin/sh
# Builds the webbit server dependency. Run as `sh build-webbit.sh`

git submodule init
git submodule update

cd dependencies/webbit \
  && make clean all\
  && cp dist/webbit-all-in-one.jar ../../lib \
  && cd ../..