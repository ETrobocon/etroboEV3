#!/bin/bash
. athrill-gcc_path
cd athrill-target/v850e2m/build_linux
make timer32=true clean
make timer32=true
cp ./athrill2 ../../../ws/
