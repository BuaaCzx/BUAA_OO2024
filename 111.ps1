
# 获取当前目录下所有文件夹的名称，排除子文件夹
$folders = Get-ChildItem -Directory

# 遍历文件夹并执行git rm --cached命令
foreach ($folder in $folders) {
    git rm --cached $folder.Name
}
