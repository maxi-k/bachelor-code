#!/bin/bash
version=1.0-SNAPSHOT
jar=target/unia-oc-robotcontrol-examples-$version.jar

target=robotcontrol.jar
address="robot-pi1.local"

workingdir=/home/pi/dev/ba-raspberry-setup

echo "DEPLOYTING $jar TO $address AT $workingdir/$target"

rsync $jar pi@$address:$workingdir/$target

ssh pi@$address <<-'ENDSSH'
  sudo service robotcontrol restart
ENDSSH
