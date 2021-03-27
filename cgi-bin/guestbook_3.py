#!/usr/bin/env python3
import sys
import os
from transit.writer import Writer
from transit.reader import Reader
from transit.transit_types import Keyword
from io import BytesIO
import hy
from hy_module import guestbook_3

def read_transit():
    daten= sys.stdin.buffer.read(int(os.environ["CONTENT_LENGTH"]))
    reader = Reader("json")
    vals2 = reader.read(BytesIO(daten))
    print("Content-Type: text/html")
    print()
    print(vals2[Keyword("message")])

def write_transit():
    print("Content-Type: application/transit+json")
    print()
    writer = Writer(sys.stdout, "json")
    writer.write([os.environ["QUERY_STRING"], "abc", 1234567890])

if __name__ == "__main__":
    qs=os.environ["QUERY_STRING"]
    if qs=="route_py=messages":
        write_transit()
    elif qs=="route_py=message":
        read_transit()
    else:
        guestbook_3.main()
