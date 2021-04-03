(import
 [transit.transit_types :as tt]
 [guestbook.py_tools [postwalk subs]]
 [guestbook.frame [app-state]])

(when (get app-state :import-sympy)
  (import sympy))

(defn sympy->transit [it]
 (if (get app-state :import-sympy)
  (cond
   [(instance? sympy.Symbol it) (tt.Symbol (str it))]
   [(instance? sympy.FunctionClass it) (tt.Symbol (str it))]
   [(instance? sympy.log it) (str it)]
   [(instance? sympy.core.add.Add it) (str it)]
   [(instance? sympy.core.numbers.One it) 1]
   [True it])
  it))

(defn hy->transit [it]
 (cond
  [(instance? HyKeyword it)
   (tt.Keyword (subs (str it) 1))]
  [True it]))

(defn convert-to-transit-types [data]
 (postwalk (comp sympy->transit hy->transit) data))
