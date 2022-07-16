# BluetoothLE for Medical Device

This is a project for a potential medical device in the works. An implant that would have to communicate with a phone through Bluetooth and send data
to a user and to their physician.


## What it does

Right now, all it does is scan for BLE devices, displays them in a listview, and then when one of the devices in the listview is clicked, it connects to
that device and starts listening for onCharacteristicChanged, receiving data. 

## Future

In the future I would like to expand this app and make it work with the implant to receive and interpret BLE data, on top of that, 
I will create user sign ups and a database for doctors and their patients to keep track of their health using Firebase.
