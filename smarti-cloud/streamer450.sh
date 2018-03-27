#!/bin/bash
cat streamer450.sh
cd /usr/local/src/mjpg-streamer/mjpg-streamer
killall mjpg_streamer
./mjpg_streamer -i "./input_uvc.so -n -f 10 -r 800x450" -o "./output_http.so -p 
10088 -w ./www"

