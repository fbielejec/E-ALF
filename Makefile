BOARD_TAG    = megaADK #uno
MONITOR_PORT  = /dev/ttyACM0
ARDUINO_LIBS = Wire Adafruit_Motorshield MemoryFree
#CPP=avr-g++

#NO_CORE = Yes
#MCU = atmega16
#F_CPU = 8000000L

include $(ARDMK_DIR)/Arduino.mk

