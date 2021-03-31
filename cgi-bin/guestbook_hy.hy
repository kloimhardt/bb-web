(import [sys [path]])

(.append path "cgi-bin/transit_python-0.8.284-py3.6.egg")

(import [guestbook.core [main :as core-main]]
        [guestbook.frame [update-app-state text-io-bytes-wrapper]]
        [io [TextIOWrapper BytesIO]])

(defn main [argv]
 (core-main))
