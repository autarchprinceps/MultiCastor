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

int main( void ){

    if(IsWow64())
        system("start javaw -Djava.library.path=lib/windows/64 -jar MultiCastor.jar");
    else
        system("start javaw -Djava.library.path=lib/windows/32 -jar MultiCastor.jar");

    return 0;
}
