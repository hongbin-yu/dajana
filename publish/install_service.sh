#!/bin/bash
dajana="/var/dajana"
target="/home/dajana/publish/target/publish-1.0.0.jar"

if [ ! -d "@dajana" ]; then
mkdir "$dajana"
fi

cp -f "$target" "/var/dajana/publish.jar"

sudo ln -s /var/dajana/publish.jar /etc/init.d/dajana

update-rc.d dajana defaults