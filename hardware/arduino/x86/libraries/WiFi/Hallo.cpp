#include <iostream> 

/* WiFi library that uses the Intel Centrino N-135 wireless */
#include "WiFi.h"
#include <trace.h>
extern "C" {
  #include "utility/wl_definitions.h"
  #include "utility/n135.h"
}

#define MY_TRACE_PREFIX "WiFiClass"

#ifndef ARDUINO_WLAN
#define ARDUINO_WLAN "wlan0"
#endif
#define TMP_PATH	"/tmp/tmp.tmp"

// XXX: don't make assumptions about the value of MAX_SOCK_NUM.
int16_t 	WiFiClass::_state[MAX_SOCK_NUM] = { NA_STATE, NA_STATE, NA_STATE, NA_STATE };
uint16_t 	WiFiClass::_server_port[MAX_SOCK_NUM] = { 0, 0, 0, 0 };

using namespace std; 
int main ()
{ 
    cout<<"\nHola mundo\n"; 
    
	FILE *fp = NULL;
	char cmd[128];
	uint8_t ipb[4];
	IPAddress ip;
	trace_debug("getLocalIP");
	sprintf(cmd, "ifconfig %s | egrep \"inet addr\" | cut -d: -f2- > %s",
			ARDUINO_WLAN, TMP_PATH);
	system(cmd);
	if (NULL == (fp = fopen(TMP_PATH, "r"))) {
		trace_error("can't open handle to %s", TMP_PATH);
		return ip;
	}
	fscanf(fp, "%s", cmd); /* inet addr */
	fclose(fp);
	trace_debug("my IP=%s", cmd);
	if(isdigit(cmd[0])) {
		sscanf(cmd, "%hhd.%hhd.%hhd.%hhd", &ipb[0], &ipb[1],
				&ipb[2], &ipb[3]);
		ip._sin.sin_addr.s_addr = ( ((uint32_t)ipb[3])<<24 | \
			((uint32_t)ipb[2])<<16 | ((uint32_t)ipb[1])<<8 | ((uint32_t)ipb[0]) );
		trace_debug("returning ip %3d.%3d.%3d.%3d",
			(ip._sin.sin_addr.s_addr&0x000000FF),
			(ip._sin.sin_addr.s_addr&0x0000FF00)>>8,
			(ip._sin.sin_addr.s_addr&0x00FF0000)>>16,
			(ip._sin.sin_addr.s_addr&0xFF000000)>>24);
	} else {
		ip._sin.sin_addr.s_addr = 0;
	}
	return ip;


} 
