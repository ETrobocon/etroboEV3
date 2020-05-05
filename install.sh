#!/bin/bash
sudo apt -y update
sudo apt -y upgrade
sudo apt -y install build-essential
sudo apt -y install git
sudo apt -y install ruby

mkdir etrobosim
cd etrobosim

git clone https://github.com/tmori/athrill.git
git clone https://github.com/tmori/athrill-target.git
git clone https://github.com/tmori/athrill-sample.git

ln -s ~/etrobosim/athrill-sample/ev3rt/ev3rt-beta7-release/asp3/sdk/workspace ws

wget https://github.com/tmori/athrill-gcc/releases/download/v1.0/athrill-gcc-package.tar.gz
tar xzvf athrill-gcc-package.tar.gz
cd athrill-gcc-package
tar xzvf athrill-gcc.tar.gz
sudo mv usr/local/athrill-gcc /usr/local
sudo chmod -R 755 /usr/local/athrill-gcc
sudo chown -R root:root /usr/local/athrill-gcc
cd ..
#rm -rf athrill-gcc*
