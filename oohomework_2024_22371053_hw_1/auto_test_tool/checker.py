import random
from sympy import symbols, simplify, parse_expr
import sys

# 随机替换x计算表达式的值
def evaluate_expression(expression, x_value):
    x = symbols('x')
    replaced_expression = expression.subs(x, x_value)
    return simplify(replaced_expression)

# 检查两个表达式是否等价
def check_equivalence(expression1_str, expression2_str):
    x = symbols('x')
    expr1 = parse_expr(expression1_str.replace('^', '**'))
    expr2 = parse_expr(expression2_str.replace('^', '**'))

    for _ in range(10):  # 尝试10次随机替换
        x_value = random.randint(-100, 100)  # 随机生成替换x的值
        value1 = evaluate_expression(expr1, x_value)
        value2 = evaluate_expression(expr2, x_value)
        if value1 != value2:
            return "Not Equivalent"  # 只要有一次计算结果不相等，就返回Not Equivalent
    return "Equivalent"

if __name__ == "__main__":
    input_expression = sys.argv[1]
    java_output = sys.argv[2]

    # 示例用法
    if check_equivalence(input_expression, java_output) == "Equivalent":
        print("Equivalent")
    else:
        print("Not Equivalent")
