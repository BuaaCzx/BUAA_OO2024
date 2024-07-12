import random
import sys

def generate_expression(length, depth, max_depth):
    return generate_additive_expression(length, depth, max_depth)

def generate_additive_expression(length, depth, max_depth):
    expression = generate_term(length, depth, max_depth)
    while length + len(expression) < 100 and random.choice([True, False]):
        if random.choice([True, False]):
            expression += ' + ' + generate_term(length + len(expression) + 3, depth, max_depth)
        else:
            expression += ' - ' + generate_term(length + len(expression) + 3, depth, max_depth)
    return expression

def generate_term(length, depth, max_depth):
    term = generate_factor(length, depth, max_depth)
    while length + len(term) < 100 and random.choice([True, False]):
        if random.choice([True, False]):
            term += ' * ' + generate_factor(length + len(term) + 3, depth, max_depth)
        else:
            term = generate_factor(length + len(term) + 3, depth, max_depth) + ' * ' + term
    return term

def generate_factor(length, depth, max_depth):
    if random.choice([True, False]):
        factor = generate_variable_factor()
    elif random.choice([True, False]):
        factor = generate_constant_factor()
    else:
        factor = generate_expression_factor(length, depth, max_depth)

    if isinstance(factor, str) and factor.startswith('('):
        # 如果因子是一个括号包含的表达式，则添加幂次
        factor += generate_exponent()
    elif factor == 'x':
        # 如果因子是 'x'，则添加幂次
        factor += generate_exponent()

    return factor

def generate_variable_factor():
    return generate_power_function()

def generate_constant_factor():
    return str(generate_signed_integer())

def generate_expression_factor(length, depth, max_depth):
    if depth >= max_depth:
        return generate_variable_factor()
    return '(' + generate_expression(length + 2, depth + 1, max_depth) + ')'

def generate_power_function():
    return 'x'

def generate_exponent():
    return '^ ' + generate_positive_integer(8)

def generate_signed_integer():
    return random.choice(['', '+', '-']) + generate_non_zero_integer()

def generate_non_zero_integer():
    return random.choice([str(i) for i in range(0, 100000)])

def generate_positive_integer(maxx):
    return random.choice([str(i) for i in range(0, maxx)])

print(generate_expression(0, 0, 1))
