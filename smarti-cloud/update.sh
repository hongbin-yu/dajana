echo "update tomcat8 ROOT.war"
new_war=/home/device/dajana/smarti-cloud/target/smarti-cloud-1.0.war
old_war=/var/lib/tomcat8/webapps/ROOT.war
if [ "$new_war" -nt "$old_war" ]
then
echo "stop tomcat8"
service tomcat8 stop
echo "copy war file"
cp -f "$new_war" "$old_war"
echo "start tomcat8"
service tomcat8 start
else
echo "It is new version"
fi


