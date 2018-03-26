#!/bin/bash
cat streamer450.sh
#cd /usr/local/src/mjpg-streamer/mjpg-streamer
killall mjpg_streamer
/usr/local/bin/mjpg_streamer -i "/usr/local/lib/input_uvc.so -n -f 10 -r 800x450" -o "/usr/local/lib/output_http.so -p 
10088 -w /usr/local/www" &

