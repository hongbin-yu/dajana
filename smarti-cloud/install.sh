#!/bin/bash
git pull
mvn install
/var/lib/tomcat8/conf/update.sh
