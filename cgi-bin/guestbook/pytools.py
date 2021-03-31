import io
import hy

class MyTextIOWrapper(io.TextIOWrapper):
    def close(self):
        return None

def hyeval (s):
    return hy.eval(hy.read_str(s))
