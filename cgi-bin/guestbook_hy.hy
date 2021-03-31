(import [sys [path]])

(.append path "cgi-bin/transit_python-0.8.284-py3.6.egg")

(import [guestbook.core [main :as core-main]]
        [guestbook.frame [update-app-state text-io-bytes-wrapper]]
        [io [TextIOWrapper BytesIO]])

(defn main [argv]
 (if (first (rest argv))
  (do
   (do
    (update_app_state {:environ {"QUERY_STRING"  "route=messages"}})
    (core-main))
   (do
    (setv msg "[\"^ \",\"~:name\",\"q\",\"~:message\",\"ruhig\"]")
    (update_app_state {:environ {"QUERY_STRING" "route=message"
                                 "CONTENT_LENGTH" (str (len msg))}
                       :stdin (text-io-bytes-wrapper msg)})
    (core-main)))
 (core-main)))

(comment
 (+ 3 4)
 (identity {:b 4})
)
