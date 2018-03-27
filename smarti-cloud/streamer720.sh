#!/bin/bash
#cat streamer720.sh
cd /usr/local/src/mjpg-streamer/mjpg-streamer
killall mjpg_streamer
./mjpg_streamer -i "./input_uvc.so -n -f 10 -r 1280x720" -o "./output_http.so -p 
10088 -w ./www" &

