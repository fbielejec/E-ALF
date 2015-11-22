BOARD_TAG    = uno #uno megaADK
MONITOR_PORT  = /dev/ttyACM0 #/dev/ttyUSB0
ARDUINO_LIBS = Wire Adafruit_Motorshield MemoryFree SoftwareSerial
#CPP=avr-g++

#NO_CORE = Yes
#MCU = atmega16
#F_CPU = 8000000L

include $(ARDMK_DIR)/Arduino.mk

