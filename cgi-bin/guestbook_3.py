#!/usr/bin/env python3
import sys
sys.path.append("cgi-bin/hy-0.20.0+129.g9733a3dd-py3.6.egg")
import hy
import guestbook_hy

if __name__ == "__main__":
    guestbook_hy.main(sys.argv)
