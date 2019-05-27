# screenshot-detect-sample

Workaround to detect/prevent screenshot in android
- use window flag FLAG_SECURE - you can't take screenshot and the screen will blank in the recent screen (because unsecure display).
- listen to new file using content observer and check if the file is located in screenshots/ folder and the name prefixed by screenshot*.
