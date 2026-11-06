# Hide an old smartphone as a time-lapse.

An old smartphone takes photos at regular intervals until the battery runs out.

The images have the timestamp as their filename. This means you can then use ffmpeg to create a time-lapse video.

´ffmpeg -r 12 -pattern_type glob -i '*.jpg' -c:v libx264 test.mp4'

<img width="400" height="800"  alt="adbScreenShot20261106_050653" src="https://github.com/user-attachments/assets/66919d49-37f7-4934-a573-d29c8ca55ac4" />
<img width="400" height="800"  alt="adbScreenShot20261106_050647" src="https://github.com/user-attachments/assets/51d6dfa7-443a-4992-9387-fd3497f80780" />
