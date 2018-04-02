#!/bin/bash
cd /mnt/device/database
apt-get install geoip-bin
rm GeoLiteCity.dat.gz

wget http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz
gunzip GeoLiteCity.dat.gz
cp GeoLiteCity.dat /usr/share/GeoIP/