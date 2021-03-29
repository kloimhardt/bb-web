#!/usr/bin/env python3
import sys
sys.path.append("cgi-bin/hy-0.20.0+129.g9733a3dd-py3.6.egg")
import hy
from guestbook.frame import update_app_state
import guestbook_hy
import io

if len(sys.argv) > 1:
    wrapper=io.TextIOWrapper(io.BytesIO(), encoding='utf-8')
    msg='["^ ","~:name","q","~:message","ruhig"]'
    wrapper.write(msg)
    wrapper.seek(0, 0)
    update_app_state({"environ": {"QUERY_STRING" : "route=message",
                                  "CONTENT_LENGTH" : str(len(msg))},
                      "stdin" : wrapper})

if __name__ == "__main__":
    guestbook_hy.main()
