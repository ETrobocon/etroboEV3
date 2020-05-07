#!/bin/bash
#
# auto EV3 mounter for EV3RT
#
# mount.sh
#
# Ver 0.10a.200507
# Copyright (c) 2020 jtLab, Hokkaido Information University
# by TANAHASHI, Jiro(aka jtFuruhata) <jt@do-johodai.ac.jp>
# Released under the MIT license
# https://opensource.org/licenses/mit-license.php
#
Caption=""
VolumeName=""
tmpFile=$(mktemp)

wmic.exe LogicalDisk get Caption,VolumeName > $tmpFile
while read line; do
    if [ ${line:1:1} = ":"  ]; then
        if [ "${line:9:5}" = "EV3RT" ]; then
            Caption="${line:0:2}"
            VolumeName=`echo ${line:9:-2}`
        fi
    fi
done < $tmpFile
rm $tmpFile

if [ $Caption ]; then
    if [ ! -d /mnt/ev3 ]; then
        sudo mkdir /mnt/ev3
    fi
    sudo mount -t drvfs $Caption /mnt/ev3
    echo "$Caption $VolumeName"
    if [ -d /mnt/ev3/ev3rt/apps ]; then
        ls /mnt/ev3/ev3rt/apps
    fi
else
    exit 1
fi
