(import [guestbook.frame :as f])

(defn assert-equal [x y]
  (assert (= x y)))

(defn wrapper-read [w length]
 (.seek w 0 0)
 (f.decode-bytes (-> w (. buffer) (.read length))))

(defn test_sprint []
 (setv out-wrapper (f.text-io-bytes-wrapper ""))
 (f.update_app_state {:stdout out-wrapper})
 (setv o-text "hello-out")
 (f.sprint o-text)
 (assert-equal o-text (wrapper-read out-wrapper (len o-text))))

(defn test_eprint []
 (setv err-wrapper (f.text-io-bytes-wrapper ""))
 (f.update_app_state {:stderr err-wrapper})
 (setv e-text "hello-err")
 (f.eprint e-text)
 (assert-equal e-text (wrapper-read err-wrapper (len e-text))))

(defn test_write-log []
 (setv log-wrapper (f.my-text-io-bytes-wrapper ""))
 (f.update_app_state {:f-open-log (fn [] log-wrapper)})
 (setv log-text "hello-log")
 (f.write-log log-text)
 (assert-equal log-text (wrapper-read log-wrapper (len log-text))))
