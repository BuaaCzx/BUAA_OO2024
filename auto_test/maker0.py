import random

def generate_expression():
    return generate_additive_expression(0)

def generate_additive_expression(length):
    expression = generate_term(length)
    while length + len(expression) < 100 and random.choice([True, False]):
        if random.choice([True, False]):
            expression += ' + ' + generate_term(length + len(expression) + 3)
        else:
            expression += ' - ' + generate_term(length + len(expression) + 3)
    return expression

def generate_term(length):
    term = random.choice(['', '+', '-']) + generate_factor(length)
    while length + len(term) < 100 and random.choice([True, False]):
        if random.choice([True, False]):
            term += ' * ' + generate_factor(length + len(term) + 3)
        else:
            term = generate_factor(length + len(term) + 3) + ' * ' + term
    return term

def generate_factor(length):
    if random.choice([True, False]):
        factor = generate_variable_factor()
    elif random.choice([True, False]):
        factor = generate_constant_factor()
    else:
        factor = generate_expression_factor()
    
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

def generate_expression_factor():
    return '(' + generate_expression() + ')'

def generate_power_function():
    return 'x'

def generate_exponent():
    return '^ ' + generate_positive_integer(8)

def generate_signed_integer():
    return random.choice(['', '+', '-']) + generate_non_zero_integer()

def generate_non_zero_integer():
    return random.choice([str(i) for i in range(1, 1000)])

def generate_positive_integer(maxx):
    return random.choice([str(i) for i in range(1, maxx)])

print(generate_expression())
