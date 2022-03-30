# GetSensorData_Android
Mirror of [GetSensorData_Android](https://gitlab.com/getsensordatasuite/getsensordata_android).

Change: fix the output folder, build and release.

# Install

Open the browser on an Android phone,
navigate to https://github.com/JzHuai0108/GetSensorData_Android/releases/
download the apk,
then install it from the browser.


# Record

The GUI is easy to understand.
Details can be found [here](https://gitlab.com/getsensordatasuite/getsensordata_documentation.git).


# Retrieve data

The data are located at Files/Android/data/es.csic.getsensordata/files/data and can be browsed with the Files app.

To copy the data to a computer,
```
adb pull /sdcard/Android/data/es.csic.getsensordata/files/data
```

# Parse data

See the [matlab scripts](https://gitlab.com/getsensordatasuite/getsensordata_matlab.git).

