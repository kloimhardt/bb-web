(import [sys [path]])

(.append path "cgi-bin/transit_python-0.8.284-py3.6.egg")

(import [guestbook.core [core/main]]
        [guestbook.frame [f/update-app-state]]
        [io [TextIOWrapper BytesIO]])

(defn main [argv]
 (if (first (rest argv))
  (do
   (do
    (f/update_app_state {:environ {"QUERY_STRING"  "route=messages"}})
    (core/main))
   (do
    (setv wrapper (TextIOWrapper (BytesIO) :encoding "utf-8"))
    (setv msg  "[\"^ \",\"~:name\",\"q\",\"~:message\",\"ruhig\"]")
    (.write wrapper msg)
    (.seek wrapper 0 0)
    (f/update_app_state {:environ {"QUERY_STRING" "route=message"
                                "CONTENT_LENGTH" (str (len msg))}
                       :stdin  wrapper})
    (core/main)))
 (core/main)))

(comment
 (+ 3 4)
 (identity {:b 4})
)
