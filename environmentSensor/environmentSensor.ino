#include <SPI.h>
#include <WiFi101.h>
#include <WiFiUdp.h>

#include <Adafruit_BME280.h>

const char ssid[] = "boynes2";
const char pass[] = "thequickbrown";

char id[32];
unsigned long lastHello;

#define BUFFER_SIZE 512
char buffer[BUFFER_SIZE];

int status = WL_IDLE_STATUS;
WiFiUDP Udp;

Adafruit_BME280 bme;

boolean led = false;

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  bme.begin();

  WiFi.setPins(8,7,4,2); //Configure pins for Adafruit ATWINC1500 Feather
  connect();

  byte mac[6];
  WiFi.macAddress(mac);
  sprintf(id, "%02x%02x%02x", mac[0], mac[1], mac[2]);
  
  sendHello();
}

void loop() {
  unsigned long start = millis();
  
  digitalWrite(LED_BUILTIN, HIGH);
  delay(200);
  float temp = bme.readTemperature() + 273.15;
  int pressure = bme.readPressure();
  float humidity = bme.readHumidity();
  digitalWrite(LED_BUILTIN, LOW);

  connect();
  if (start - lastHello > 10000) {
    sendHello();
  }

  sprintf(buffer, "{\"devices\":{\"acme-BME280-%s\":{\"data\":{"
      "\"temperature\":%d.%02d,"
      "\"pressure\":%d,"
      "\"humidity\":%d.%02d"
      "}}}}",
    id, (int) temp, (int)(temp*100)%100, pressure, (int) humidity, (int)(humidity*100)%100);
  send(buffer);
  
  delay(2000);
}

void sendHello() {
/*
{
  "devices": {
    "acme-BME280-123456": {
      "manufacturerName": "acme",
      "productName": "BME280",
      "serialNumber": "123456"
    },
    "data": {
      "$schema": "http://devices.acme.example/signalk/bme280.json"
    }
  }
}
*/  
  sprintf(buffer, 
  "{\"devices\":{\"acme-BME280-%s\":{"
    "\"manufacturerName\":\"acme\","
    "\"productName\":\"BME280\","
    "\"serialNumber\":\"%s\","
    "\"data\":{\"$schema\":\"http://devices.acme.example/signalk/bme280.json\"}"
  "}}}", id, id);
  send(buffer);
  lastHello = millis();
}

void connect() {
  if (status != WL_CONNECTED) {
    status = WiFi.begin(ssid, pass);
    Udp.begin(2000);
  }
}

void send(char* buffer) {
  if (status == WL_CONNECTED) {
    Udp.beginPacket("225.4.5.6", 3858);
    Udp.write(buffer);
    Udp.flush();
    Udp.endPacket();
  }
}

