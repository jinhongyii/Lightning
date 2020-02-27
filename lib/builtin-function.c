//
// Created by jinho on 1/29/2020.
//
//todo: inline external functions
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
void print(char* str){
    printf("%s",str);
}
void println(char* str){
    printf("%s\n",str);
}
void printInt(int a){
    printf("%d",a);
}

void printlnInt(int a){
    printf("%d\n",a);
}

char* getString(){
    char* buffer=malloc(1024);
    scanf("%s",buffer);
    return buffer;
}
int getInt(){
    int tmp;
    scanf("%d",&tmp);
    return tmp;
}
char* toString(int  v){
    if (v == 0){
        char *r = (char *)malloc(sizeof(char) * 2);
        r[0] = '0'; r[1] = '\0';
        return r;
    }
    short digits[10];
    short neg = v < 0;
    if (neg) v=-v;
    short len = 0;
    while (v>0){
        digits[len++] = v%10;
        v/=10;
    }
    char *r = (char *)malloc(sizeof(char) * (len + neg + 1));
    short p = 0;
    if (neg) r[0] = '-';
    while (p < len){
        r[p + neg] = digits[len - p - 1] + '0';
        ++p;
    }
    r[len + neg] = '\0';
    return r;
}

int string_length (char *string){
    return strlen(string);
}

char* string_substring(int left,int right,char* string){
    char* newstr=malloc(right-left+1);
    memcpy(newstr,string+left,right-left);
    newstr[right-left]=0;
    return newstr;
}

int string_parseInt(char* str){
    int tmp;
    sscanf(str,"%d",&tmp);
    return tmp;
}

int string_ord(int index,char* string){
    return string[index];
}

int _array_size(char* array){
   return *(((long long*)array)-1);
}

char* string_add(char* str1,char* str2){
    int len1=strlen(str1);
    int len2=strlen(str2);
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




