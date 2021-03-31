from io import TextIOWrapper

class MyTextIOWrapper(TextIOWrapper):
    def close(self):
        return None
