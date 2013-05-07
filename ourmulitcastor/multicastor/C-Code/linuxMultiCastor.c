#include <stdio.h>
#include <stdlib.h>

int main (){

    char ch;
	char *source_file;
   


	int cfileexists(const char * filename){
		/* try to open file to read */
		FILE *file;
		if (file = fopen(filename, "rb")){
		    fclose(file);
		    return 1;
		}
		return 0;
	}

	int filecopy(const char * source_file, const char * target_file){
		if(!cfileexists(target_file)){
			FILE *source, *target;
			source = fopen(source_file, "rb");
			target = fopen(target_file, "wb");
			if(target==NULL){
				printf("Can not create new lib file!\n");
			}else{
				size_t n, m;
				unsigned char buff[8192];
				do {
					n = fread(buff, 1, sizeof buff, source);
					if (n) m = fwrite(buff, 1, n, target);
					else   m = 0;
				} while ((n > 0) && (n == m));
				if (m) perror("copy");
	
			   printf("File copied successfully.\n");
			   fclose(target);
		   }
		   
		   fclose(source);
		   return 1;	
		}else{
	
			printf("libjnetpcap already exists in %s!\n",target_file);
			return 0;
		}
	}



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

	char target_file[]="/usr/lib/libjnetpcap.so";
  	switch(bitVersion){
		case 32: 
			source_file=strdup("lib/jnetpcap-1.3.0.ubuntu32/libjnetpcap.so");
			if(cfileexists(source_file)){
				filecopy(source_file,target_file);
				system("sudo java -jar MultiCastor.jar");
			}else
			 	printf("libjnetpcap not found in %s",source_file);
			 break;
		
		case 64:
			source_file=strdup("lib/jnetpcap-1.3.0.ubuntu64/libjnetpcap.so");
			if(cfileexists(source_file)){
				filecopy(source_file,target_file);
				system("sudo java -jar MultiCastor.jar");
			}else
			 	printf("libjnetpcap not found in %s",source_file);
			 break;
	
		default:printf("Could not start MultiCastor.");
			break;

	}
	
	return 0;	
}
