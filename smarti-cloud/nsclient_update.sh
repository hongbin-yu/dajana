#!bash/sh
ADDR=$(curl ns2.dajana.ca/myip/hostname)
HOST="hostname.dajana.ca"
echo 'IP='$ADDR
echo "server ns2.dajana.ca" > /var/nsupdate.txt
echo "zone dajana.ca" >> /var/nsupdate.txt
echo "update delete $HOST A" >> /var/nsupdate.txt
echo "update add $HOST 86400 A $ADDR" >> /var/nsupdate.txt
echo "update delete $HOST PTR" >> /var/nsupdate.txt
echo "update add $HOST 86400 PTR $ADDR" >> /var/nsupdate.txt
nsupdate /var/nsupdate.txt -k /etc/bind/Ksb.dajana.ca.+165+31669.key