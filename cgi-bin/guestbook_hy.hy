(import [sys [path]]
        [io [TextIOWrapper BytesIO]])
(.append path "cgi-bin/transit_python-0.8.284-py3.6.egg")
(.append path ".")

(import [guestbook.core [main :as core-main]]
        [guestbook.frame [update-app-state]])

(defn main [argv]
 (if (first (rest argv))
  (do
   (do
    (update_app_state {"environ"  {"QUERY_STRING"  "route=messages"}})
    (core-main))
   (do
    (setv wrapper (TextIOWrapper (BytesIO) :encoding "utf-8"))
    (setv msg  "[\"^ \",\"~:name\",\"q\",\"~:message\",\"ruhig\"]")
    (.write wrapper msg)
    (.seek wrapper 0 0)
    (update_app_state {"environ" {"QUERY_STRING" "route=message"
                                "CONTENT_LENGTH" (str (len msg))}
                       "stdin"  wrapper})
    (core-main)))
 (core-main)))

(comment
 (+ 3 4)
 (identity {:b 4})
)
