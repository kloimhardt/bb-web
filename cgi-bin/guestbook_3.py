#!/usr/bin/env python3
import sys
sys.path.append("cgi-bin/hy-0.20.0+129.g9733a3dd-py3.6.egg")
import hy
from guestbook.frame import update_app_state
import guestbook_hy
import io

if __name__ == "__main__":
    #update_app_state({"environ": {"QUERY_STRING" : "route=messages",
    #                              "CONTENT_LENGTH" : "35",
    #                              "stdin" : io.BytesIO(b'["^ ","~:name","q","~:message","r"]')}})
    guestbook_hy.main()
