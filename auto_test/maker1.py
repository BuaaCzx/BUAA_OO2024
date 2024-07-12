import random
import sys

def generate_expression(length, depth, max_depth, args, func, max_length):
    return generate_additive_expression(length, depth, max_depth, args, func, max_length)

def generate_additive_expression(length, depth, max_depth, args, func, max_length):
    expression = generate_term(length, depth, max_depth, args, func, max_length)
    while length + len(expression) < max_length and random.choice([True, False]):
        if random.choice([True, False]):
            expression += '+' + generate_term(length + len(expression) + 1, depth, max_depth, args, func, max_length)
        else:
            expression += '-' + generate_term(length + len(expression) + 1, depth, max_depth, args, func, max_length)
    return expression

def generate_term(length, depth, max_depth, args, func, max_length):
    term = random.choice(['', '+', '-']) + generate_factor(length, depth, max_depth, args, func, max_length)
    while length + len(term) < max_length and random.choice([True, False]):
        term += '*' + generate_factor(length + len(term) + 1, depth, max_depth, args, func, max_length)
    return term

def generate_factor(length, depth, max_depth, args, func, max_length):

    factors = ([
        generate_variable_factor(args),
        generate_constant_factor(),
        generate_expression_factor(length, depth + 1, max_depth, args, func, max_length), 
        generate_exponent_factor(length, depth + 1, max_depth, args, func, max_length)
    ])



    if func :
        factors.append(generate_function_factor(length, depth + 1, max_depth, args, func))
        # weight = [0.3, 0.3, 0.2, 0.1, 0.1]
        # weight = [0.3, 0.3, 0.2, 0.2]

    factor = random.choice(factors)

    if isinstance(factor, str) and factor.startswith('('):
        # 如果因子是一个括号包含的表达式，则随机添加幂次
        if random.choice([True, False]):
            factor += generate_exponent()
    elif factor in args:
        # 如果因子是 'x'，则随机添加幂次
        if random.choice([True, False]):
            factor += generate_exponent()
    elif isinstance(factor, str) and factor.startswith('e'):
        if random.choice([True, False]):
            factor += generate_exponent()

    return factor

def generate_function_factor(length, depth, max_depth, args, func):
    if depth >= max_depth:
        return generate_variable_factor(args)
    funcs = ['f', 'g', 'h']
    return random.choice(funcs) + '(' + generate_factor(length, depth, max_depth, args, func) + ',' + generate_factor(length, depth, max_depth, args, func) + ',' + generate_factor(length, depth, max_depth, args, func) + ')'

def generate_exponent_factor(length, depth, max_depth, args, func, max_length):
    if depth >= max_depth:
        return generate_variable_factor(args)
    factor = random.choice([
        generate_variable_factor(args),
        generate_constant_factor(),
        generate_expression_factor(length, depth + 1, max_depth, args, func, max_length)
    ])
    return 'exp(' + factor + ')'

def generate_variable_factor(args):
    return random.choice(args)

def generate_constant_factor():
    return str(generate_signed_integer())

def generate_expression_factor(length, depth, max_depth, args, func, max_length):
    if depth >= max_depth:
        return generate_variable_factor(args)
    return '(' + generate_expression(length + 2, depth + 1, max_depth, args, func, max_length) + ')'

def generate_zero():
    num = random.randint(0, 2)
    return '0' * num

def generate_exponent():
    return '^' + random.choice(['', '+']) + generate_zero() + generate_positive_integer(8)

def generate_signed_integer():
    return random.choice(['', '+', '-']) + generate_zero() + generate_positive_integer(9)

def generate_positive_integer(maxx):
    return str(random.choice([random.randint(0, maxx)]))

def generate_bigint():
    res = ''
    t = random.randint(1, 15)
    for _ in range(t):
        res += generate_positive_integer(99999)
    return res

# print(3)
# print('f(x,y,z)=' + generate_expression(0, 0, 3, ['x', 'y', 'z'], False, 100))
# print('g(x,y,z)=' + generate_expression(0, 0, 3, ['x', 'y', 'z'], False, 100))
# print('h(x,y,z)=' + generate_expression(0, 0, 3, ['x', 'y', 'z'], False, 100))
# print(generate_expression(0, 0, 5, ['x'], True, 100))
print(0)
print(generate_expression(0, 0, 5, ['x'], False, 100))