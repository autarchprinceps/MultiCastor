#include <stdio.h>
#include <stdlib.h>

int main (){

	FILE *fp;
  	char path[3];
	int bitVersion = 0;

  	fp = popen("getconf LONG_BIT", "r");
  	if (fp == NULL) {
    		printf("Failed to run command\n" );
    		exit;
  	}

  	if (fgets(path, sizeof(path), fp) != NULL) {
    		bitVersion = atoi(path);
  	}

  	pclose(fp);
	
  	switch(bitVersion){
		case 32: system("sudo java -Djava.library.path=lib/linux/32 -jar MultiCastor.jar");
			 break;
		
		case 64:system("sudo java -Djava.library.path=lib/linux/64 -jar MultiCastor.jar");
			break;
	
		default:printf("Could not start MultiCastor.");
			break;

	}
	
	return 0;	
}
