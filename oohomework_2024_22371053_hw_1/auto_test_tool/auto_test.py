import subprocess

max_test_num = 1000
cur_num = 0
max_depth = 1

while(cur_num < max_test_num) :
    cur_num += 1

    # Step 1: 生成随机表达式作为输入input
    input_expression = subprocess.check_output(['python', 'maker1.py']).decode().strip()
    print("生成的随机表达式为: ", input_expression)

    # Step 2: 调用 Java 程序处理input得到output
    java_process = subprocess.Popen(['java', '-cp', 'out/production/oohomework_2024_22371053_hw_1', 'MainClass'], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    java_output, java_error = java_process.communicate(input=input_expression.encode())
    print("Java程序输出: ", java_output.decode().strip())

    # Step 3: 使用checker.py判断input和output是否等价
    checker_output = subprocess.check_output(['python', 'checker.py', input_expression, java_output.decode().strip()]).decode().strip()

    if checker_output == "Equivalent":
        print("\033[32;47mAccepted!\033[0m")
    else:
        print("\033[31;43mWrong Answer!\033[0m")
        break

print("Finished ", max_depth, " tests.")
print("\033[32;47mAll Accepted!\033[0m")
