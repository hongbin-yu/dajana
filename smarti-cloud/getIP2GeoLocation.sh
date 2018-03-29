#!/bin/bash
cd /mnt/device/database
rm GeoLite2-City.tar.gz
wget http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz
tar xvzf GeoLite2-City.tar.gz