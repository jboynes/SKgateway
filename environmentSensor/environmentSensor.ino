#include <SPI.h>
#include <WiFi101.h>
#include <WiFiUdp.h>

#include <Adafruit_BME280.h>

#define LOCAL_PORT 2000
#define MULTICAST_PORT 8375
#define MULTICAST_GROUP "225.4.5.6"

const char ssid[] = "***";
const char pass[] = "***";


char id[32];
unsigned long lastHello;

#define BUFFER_SIZE 512
char buffer[BUFFER_SIZE];

int status = WL_IDLE_STATUS;
WiFiUDP Udp;

Adafruit_BME280 bme;

float temp;
int pressure;
float humidity;

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  bme.begin();

  WiFi.setPins(8,7,4,2); //Configure pins for Adafruit ATWINC1500 Feather
  connect();

  byte mac[6];
  WiFi.macAddress(mac);
  sprintf(id, "%02x%02x%02x", mac[0], mac[1], mac[2]);

  takeReading();
  sendHello();
}

void loop() {
  unsigned long start = millis();
  
  takeReading();
  connect();
  if (start - lastHello > 10000) {
    sendHello();
  } else {
    sendData();
  }

  delay(2000);
}

void takeReading() {
  digitalWrite(LED_BUILTIN, HIGH);
  temp = bme.readTemperature() + 273.15;
  pressure = bme.readPressure();
  humidity = bme.readHumidity();
  digitalWrite(LED_BUILTIN, LOW);
}

void sendHello() {
/*
{
  "devices": {
    "acme-bme280-f1df70": {
      "_type": "http://devices.acme.example/signalk/bme280.json#/metadata",
      "serialNumber": "f1df70",
      "lastReading": {
        "_timestamp": "2016-11-26T21:23:28.454Z",
        "temperature": 293.15,
        "pressure": 99845,
        "humidity": 45.65
      }
    }
  }
}
*/  
  sprintf(buffer, 
  "{\"devices\":{\"acme-bme280-%s\":{"
    "\"_type\":\"http://devices.acme.example/signalk/bme280.json#/metadata\","
    "\"serialNumber\":\"%s\","
    "\"lastReading\":{"
      "\"temperature\":%d.%02d,"
      "\"pressure\":%d,"
      "\"humidity\":%d.%02d"
  "}}}}", id, id, (int) temp, (int)(temp*100)%100, pressure, (int) humidity, (int)(humidity*100)%100);
  send(buffer);
  lastHello = millis();
}

void sendData() {
    sprintf(buffer, "{\"devices\":{\"acme-bme280-%s\":{\"lastReading\":{"
      "\"temperature\":%d.%02d,"
      "\"pressure\":%d,"
      "\"humidity\":%d.%02d"
      "}}}}",
    id, (int) temp, (int)(temp*100)%100, pressure, (int) humidity, (int)(humidity*100)%100);
  send(buffer);
}

void connect() {
  if (status != WL_CONNECTED) {
    status = WiFi.begin(ssid, pass);
    Udp.begin(LOCAL_PORT);
  }
}

void send(char* buffer) {
  if (status == WL_CONNECTED) {
    Udp.beginPacket(MULTICAST_GROUP, MULTICAST_PORT);
    Udp.write(buffer);
    Udp.flush();
    Udp.endPacket();
  }
}

