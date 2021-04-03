(import [guestbook.transit_tools :as tt]
        [guestbook.frame :as f]
        [transit.transit_types [Keyword :as TransitKeyword]])

(when (get f.app-state :import-sympy)
  (import [sympy [*]]))

(defn assert-equal [x y]
  (assert (= x y)))

(defn test-convert-to-transit-types-1 []
 (assert-equal (tt.convert-to-transit-types {:a {:b {:c 1}}})
               {(TransitKeyword "a")
                {(TransitKeyword "b")
                 {(TransitKeyword "c") 1}}}))

(defn test-convert-to-transit-types-1 []
 (if (get f.app-state :import-sympy)
  (do
   (setv x (Symbol "x"))
   (assert-equal
    (tt.convert-to-transit-types
     (limit (/ (sin x) x) x 0))
    1)
   (assert-equal
    (tt.convert-to-transit-types
      (integrate (/ 1 x) x))
    "log(x)"))
  (assert-equal 1 1)))
