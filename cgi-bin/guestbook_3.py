#!/usr/bin/env python3
import hy
#from hy_module import guestbook_3
import cgi
import sys
import os

from transit.writer import Writer
from transit.reader import Reader
from transit.transit_types import Keyword
from io import StringIO, BytesIO

def uu():
    form = cgi.FieldStorage()
    if "say" not in form or "to" not in form:
        print("<H1>Error</H1>")
        print("Please fill in the name and addr fields.")
        print(form)
        return
    print("<p>name:", form["say"].value)
    print("<p>addr:", form["to"].value)

def read_transit1():
    print("Content-Type: application/transit+json")
    print()
    daten= sys.stdin.buffer.read(int(os.environ["CONTENT_LENGTH"]))
    vals2 = reader.read(BytesIO(daten))
    writer = Writer(sys.stdout, "json")
    writer.write(vals2)

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
    if qs=="route=messages":
        write_transit()
    elif qs=="route=message":
        read_transit()
    #else:
    #    guestbook_3.main()
