#!/bin/bash
version=1.0-SNAPSHOT
jar=target/unia-oc-robotcontrol-examples-$version.jar

target=robotcontrol.jar
oldtargetname=robotcontrol-old.jar
address="robot-pi1.local"

workingdir=/home/pi/dev/ba-raspberry-setup

echo "DEPLOYTING $jar TO $address AT $workingdir/$target"

ssh pi@$address RWD=$workingdir RTG=$target ROT=$oldtargetname 'bash -s' <<-'ENDSSH'
  cd $RWD
  rm $ROT
  mv $RTG $ROT
ENDSSH

rsync $jar pi@$address:$workingdir/$target

# ssh pi@$address <<-'ENDSSH'
#   sudo service robotcontrol restart
# ENDSSH
