#include <bits/stdc++.h>
using namespace std;
int main() {

    int t = 10, dest = 1, id = 114514;
    // while (t--) {
    //     dest++;
    //     if (dest == 12) {
    //         dest = 2;
    //     }
    //     int _ = 6;
    //     // [时间戳]乘客ID-FROM-起点层-TO-终点层
    //     while (_--)
    //         printf("[1.2]%d-FROM-1-TO-%d\n", id--, dest);
    // }
    // for (int i = 1, j = 2; i <= 6; i++, j++) {
    //     printf("[1.5]RESET-Elevator-%d-3-0.8\n", i);
    // }
    int st = 1, ed = 11;
    for (int i = 1; i <= 30; i++) {
        printf("[5.5]%d-FROM-%d-TO-%d\n", i + 114514, st, ed);
        st++, ed--;
        if (st >= ed) {
            st = 1, ed = 11;
        }
    }

    return 0;
}