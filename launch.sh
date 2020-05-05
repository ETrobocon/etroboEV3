#!/bin/bash
. athrill-gcc_path
cd ws
if [ $# -eq 1 ]; then
    make app=$1 clean
    make app=$1
    if [ $? -ne 0 ]; then
        exit 1
    fi
    cp $1/asp ./
    cp $1/memory.txt ./
    cp $1/device_config.txt ./
fi
./athrill2 -c1 -m memory.txt -d device_config.txt -t -1 asp
