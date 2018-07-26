#!/bin/bash
cd /home/device/workingDir
cat video2mp4.sh >> running2mp4.sh
cat doc2pdf.sh >> running2mp4.sh
echo '#!/bin/sh' > video2mp4.sh
bash ./running2mp4.sh
echo '#!/bin/sh' > running2mp4.sh