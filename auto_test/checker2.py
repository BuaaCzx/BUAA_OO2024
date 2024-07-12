from sympy import symbols, exp, simplify, parse_expr
import sys

# 定义符号变量
x = symbols('x')

# 要比较的两个表达式
expr1 = 'x**2*exp(2*x)'
expr2 = 'x*x*exp(2*x)'
input_expression = sys.argv[1]
java_output = sys.argv[2]

input_expression = parse_expr(input_expression.replace('^', '**'))
java_output = parse_expr(java_output.replace('^', '**'))

# 使用 SymPy 的 simplify 函数对表达式进行化简
simplified_expr1 = simplify(input_expression)
simplified_expr2 = simplify(java_output)

# 判断化简后的表达式是否相等
if simplified_expr1 == simplified_expr2:
    print("Equivalent")
else:
    print("not Equivalent")
