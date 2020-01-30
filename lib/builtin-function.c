//
// Created by jinho on 1/29/2020.
//
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
void print(char* str){
    printf("%s",str);
}
void println(char* str){
    printf("%s\n",str);
}
void printInt(long long a){
    printf("%lld",a);
}

void printlnInt(long long a){
    printf("%lld\n",a);
}

char* getString(){
    char* buffer=malloc(1024);
    scanf("%s",buffer);
    return buffer;
}
long long getInt(){
    long long tmp;
    scanf("%lld",&tmp);
    return tmp;
}
char* toString(long long  a){
    char* newstr=malloc(30);
    sprintf(newstr,"%lld",a);
    return newstr;
}

long long string_length (char *string){
    return strlen(string);
}

char* string_substring(long long left,long long right,char* string){
    char* newstr=malloc(right-left+1);
    memcpy(newstr,string+left,right-left);
    newstr[right-left]=0;
    return newstr;
}

long long string_parseInt(char* str){
    char **aa = NULL;
    return strtoll(str,aa,10);
}

long long string_ord(long long index,char* string){
    return string[index];
}

long long _array_size(char* array){
    return *(((long long*)array)-1);
}

char* string_add(char* str1,char* str2){
    long long len1=strlen(str1);
    long long len2=strlen(str2);
    char* newstr=malloc(len1+len2+1);
    strcpy(newstr,str1);
    strcat(newstr,str2);
    return newstr;
}

char string_eq(char* str1,char* str2){
    return strcmp(str1,str2)==0;
}
char string_ne(char* str1,char* str2){
    return strcmp(str1,str2)!=0;
}
char string_lt(char* str1,char* str2){
    return strcmp(str1,str2)<0;
}
char string_le(char* str1,char* str2){
    return strcmp(str1,str2)<=0;
}
char string_gt(char* str1,char* str2){
    return strcmp(str1,str2)>0;
}
char string_ge(char* str1,char* str2){
    return strcmp(str1,str2)>=0;
}




