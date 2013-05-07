#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <tchar.h>

typedef BOOL (WINAPI *LPFN_ISWOW64PROCESS) (HANDLE, PBOOL);

LPFN_ISWOW64PROCESS fnIsWow64Process;

BOOL IsWow64(){
    
   BOOL bIsWow64 = FALSE;

   fnIsWow64Process = (LPFN_ISWOW64PROCESS) GetProcAddress(
        GetModuleHandle(TEXT("kernel32")),"IsWow64Process");

    if(NULL != fnIsWow64Process){

        if (!fnIsWow64Process(GetCurrentProcess(),&bIsWow64)){
            printf("A error happend.\n");
        }
    }
    return bIsWow64;
}

int main()
{

    char ch;
     char *source_file, *target_file;
     short int i;
   FILE *source, *target;
/*
 * Check if a file exist using fopen() function
 * return 1 if the file exist otherwise return 0
 */
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
		source = fopen(source_file, "rb");
		target = fopen(target_file, "wb");
		
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
	   fclose(source);
	   return 1;	
	}else{
	
		printf("jnetpcap already exists in %s!\n",target_file);
		return 0;
	}
}
  

   if(IsWow64()){
      source_file=strdup("lib\\jnetpcap-1.3.0-1.win64\\jnetpcap.dll");
      //Sysnative ist ein alias für System32 (fancy shit)
	 target_file=strdup("C:\\Windows\\Sysnative\\jnetpcap.dll");
   }
		
      
   
 
   if(!cfileexists(source_file) )
   {
      printf("jnetpcap.dll not found in %s \n",source_file);
      getchar();
      exit(EXIT_FAILURE);
   }
   
	filecopy(source_file,target_file);
	
	//32 - bit Version: (wird auch im 64 bit system benötigt)
	source_file=strdup("lib\\jnetpcap-1.3.0-1.win32\\jnetpcap.dll");
    target_file=strdup("C:\\Windows\\System32\\jnetpcap.dll");
    if(!cfileexists(source_file) )
   {
      printf("jnetpcap.dll not found in %s \n",source_file);
      getchar();
      exit(EXIT_FAILURE);
   }
    filecopy(source_file,target_file);
		


    system("start javaw -jar MultiCastor.jar");
 	
   return 0;
}
