# Ladder11
Android application that runs on the robot









Notes:

11-29-15
--------------------
Finally got the Arduino with stock LINX Firmware to respond to Android phone.
Fixes from last time are:
Set the length byte to the correct value, and added the value to the
checksum calculation.
Currently only communicates at 9600 baud, but I am getting the
expected responses from the Arduino
During testing it looks like it takes about 15 ms from sending the packet to
get the first part of the response, and about 20 ms for the full packet to be
received.
When testing with an echo program on the Arduino at 115200 buad
timing was around 5 msec for a full transaction to complete.
*** Note that the full packet is not received in one read frame ***

Doing simple echo tests at 9600 baud with a linx resync packet:
7 msec to first bytes, 15 msec to last bytes
5 msec to first bytes, 11 msec to last bytes
4 msec to first bytes, 16 msec to last bytes
5 msec to first bytes, 13 msec to last bytes

Doing simple echo tests at 115200 baud with a LINX resync packet:
4 msec to full packet
2 msec to first packet, 6 msec to full packet
7 msec to full packet
3 msec to full packet


Raw output from the timing tests with LINX Firmware at 9600 baud:
11-29 21:05:25.049 16812-16812/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070001000007
11-29 21:05:25.059 16812-16921/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448849125075
11-29 21:05:25.059 16812-16921/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=2
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (2): 
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 06                                           ..
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849125090
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=4
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (4): 
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 01 00 06                                     ....
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849125094
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.079 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:25.690 16812-16812/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070002000008
11-29 21:05:25.700 16812-16927/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448849125714
11-29 21:05:25.710 16812-16927/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 21:05:25.710 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.710 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=4
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (4): 
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 06 00 02                                     ....
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849125729
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=2
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (2): 
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 07                                           ..
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849125736
11-29 21:05:25.720 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:26.521 16812-16812/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070003000009
11-29 21:05:26.521 16812-16937/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448849126534
11-29 21:05:26.521 16812-16937/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 21:05:26.531 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:26.531 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=2
11-29 21:05:26.531 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (2): 
11-29 21:05:26.531 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 06                                           ..
11-29 21:05:26.531 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849126548
11-29 21:05:26.531 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=4
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (4): 
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 03 00 08                                     ....
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849126553
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:26.541 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:27.342 16812-16812/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF07000400000A
11-29 21:05:27.352 16812-16945/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448849127363
11-29 21:05:27.352 16812-16945/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=1
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (1): 
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF                                              .
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849127376
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:27.362 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=4
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (4): 
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 06 00 04 00                                     ....
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849127380
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=1
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Received: (1): 
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 09                                              .
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448849127384
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 21:05:27.372 16812-16837/com.lader11.ladder11 D/UsbRequestJNI: init



Raw output from echo timing tests at 9600 baud sending the LINX resync packet:

11-29 22:17:05.344 12323-12323/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070001000007
11-29 22:17:05.354 12323-12601/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853425357
11-29 22:17:05.354 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:05.354 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=4
11-29 22:17:05.354 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853425364
11-29 22:17:05.354 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (4): 
11-29 22:17:05.354 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 07 00 01                                     ....
11-29 22:17:05.354 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853425372
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 00 07                                        ...
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:05.364 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:05.374 12323-12601/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:17:06.445 12323-12323/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070002000008
11-29 22:17:06.445 12323-12628/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853426451
11-29 22:17:06.445 12323-12628/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:17:06.445 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:06.445 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=1
11-29 22:17:06.445 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853426456
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (1): 
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF                                              .
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853426458
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 07 00 02                                        ...
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853426462
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 00 08                                        ...
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:06.455 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:07.866 12323-12323/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070003000009
11-29 22:17:07.866 12323-12649/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853427884
11-29 22:17:07.866 12323-12649/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=1
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853427888
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (1): 
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF                                              .
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:07.876 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853427896
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 07 00 03                                        ...
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853427901
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 00 09                                        ...
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:07.886 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:09.148 12323-12323/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF07000400000A
11-29 22:17:09.148 12323-12667/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853429165
11-29 22:17:09.158 12323-12667/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=1
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853429170
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (1): 
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF                                              .
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853429173
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 07 00 04                                        ...
11-29 22:17:09.158 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=3
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853429178
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/ArduinoBridge: Received: (3): 
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 00 00 0A                                        ...
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:17:09.168 12323-12347/com.lader11.ladder11 D/UsbRequestJNI: init

Raw output from echo timing tests at 115200 baud sending the LINX resync packet:

11-29 22:11:32.307 8320-8320/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070001000007
11-29 22:11:32.317 8320-8882/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853092327
11-29 22:11:32.317 8320-8882/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=7
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853092331
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Received: (7): 
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 07 00 01 00 00 07                            .......
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:32.317 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:33.428 8320-8320/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070002000008
11-29 22:11:33.438 8320-8907/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853093452
11-29 22:11:33.438 8320-8907/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=6
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853093454
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Received: (6): 
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 07 00 02 00 00                               ......
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:33.438 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=1
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853093458
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Received: (1): 
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 08                                              .
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:33.448 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:34.529 8320-8320/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF070003000009
11-29 22:11:34.529 8320-8921/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853094545
11-29 22:11:34.539 8320-8921/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=7
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853094552
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Received: (7): 
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 07 00 03 00 00 09                            .......
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:34.539 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:34.549 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:35.690 8320-8320/com.lader11.ladder11 D/Ladder11MainActivity: Sending Packet (7): FF07000400000A
11-29 22:11:35.690 8320-8942/com.lader11.ladder11 D/ArduinoBridge: Packet Sent at: 1448853095703
11-29 22:11:35.690 8320-8942/com.lader11.ladder11 D/CdcAcmSerialDriver: Wrote amt=7 attempted=7
11-29 22:11:35.690 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:35.690 8320-8337/com.lader11.ladder11 D/SerialInputOutputManager: Read data len=7
11-29 22:11:35.690 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Packet Received at: 1448853095706
11-29 22:11:35.701 8320-8337/com.lader11.ladder11 D/ArduinoBridge: Received: (7): 
11-29 22:11:35.701 8320-8337/com.lader11.ladder11 D/ArduinoBridge: 0x00000000 FF 07 00 04 00 00 0A                            .......
11-29 22:11:35.701 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init
11-29 22:11:35.701 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: close
11-29 22:11:35.701 8320-8337/com.lader11.ladder11 D/UsbRequestJNI: init