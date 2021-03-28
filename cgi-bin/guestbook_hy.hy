(import sys)
(sys.path.append "cgi-bin/transit_python-0.8.284-py3.6.egg")
(sys.path.append ".")

(import [guestbook.core :as core]
        [guestbook.frame :as frame])

(defn main []
 (core.main))

(comment
 (+ 3 4)
 (identity {:b 4})
)
