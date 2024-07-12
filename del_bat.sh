#!/bin/bash

# 遍历当前目录下的所有子目录
find . -type d -name ".git" -exec echo "Deleting .git folder in {}" \; -exec rm -rf {} \;

echo "Done."
