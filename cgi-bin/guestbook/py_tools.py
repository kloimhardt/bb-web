import io
import hy
from functools import partial

class MyTextIOWrapper(io.TextIOWrapper):
    def close(self):
        return None

def hyeval (s):
    try:
        #return None
        return hy.eval(hy.read_str(s))
    except:
        return "Something went wrong"

def identity(e):
    return e

def walk(inner, outer, coll):
    if isinstance(coll, list):
        return outer([inner(e) for e in coll])
    elif isinstance(coll, dict):
        return outer(dict([inner(e) for e in iter(coll.items())]))
    elif isinstance(coll, tuple):
        return outer([inner(e) for e in coll])
    else:
        return outer(coll)

def prewalk(fn, coll):
    return walk(partial(prewalk, fn), identity, fn(coll))

def postwalk(fn, coll):
    return walk(partial(postwalk, fn), fn, coll)

def subs(s, m):
    return s[m:]
