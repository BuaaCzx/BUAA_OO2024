#include <bits/stdc++.h>
using namespace std;
int main() {
    cin.tie(0)->sync_with_stdio(0);
    int id = 1;
    int desti = 2;
    for (int i = 1; i <= 6; i++) {
        printf("[1.5]RESET-DCElevator-%d-7-3-0.6\n", i);
    }
    double time = 20.0;
    for (int i = 1; i <= 32; i++) {
        printf("[%.1lf]%d-FROM-6-TO-7\n", time, id++);
        printf("[%.1lf]%d-FROM-8-TO-7\n", time, id++);
        // time += 1.0;
        desti++;
        if (desti > 10) {
            desti = 2;
        }
    }
    return 0;
}