#!/bin/bash
cd athrill
git pull
cd ../athrill-sample
git pull
cd ../athrill-target
git pull
cd ..
wget -O https://etrobocon.github.io/etroboEV3/athrill-gcc_path
wget -O https://etrobocon.github.io/etroboEV3/build_athrill.sh
wget -O https://etrobocon.github.io/etroboEV3/launch.sh
./build_athrill.sh
