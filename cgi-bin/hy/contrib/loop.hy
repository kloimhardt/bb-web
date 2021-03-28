;;; Hy tail-call optimization
;; Copyright 2021 the authors.
;; This file is part of Hy, which is free software licensed under the Expat
;; license. See the LICENSE.

"The loop/recur macro allows you to construct functions that use tail-call
optimization to allow arbitrary levels of recursion.


.. versionadded:: 0.10.0

The ``loop`` / ``recur`` macro gives programmers a simple way to use
tail-call optimization (TCO) in their Hy code.

    A tail call is a subroutine call that happens inside another
    procedure as its final action; it may produce a return value which
    is then immediately returned by the calling procedure. If any call
    that a subroutine performs, such that it might eventually lead to
    this same subroutine being called again down the call chain, is in
    tail position, such a subroutine is said to be tail-recursive,
    which is a special case of recursion. Tail calls are significant
    because they can be implemented without adding a new stack frame
    to the call stack. Most of the frame of the current procedure is
    not needed any more, and it can be replaced by the frame of the
    tail call. The program can then jump to the called
    subroutine. Producing such code instead of a standard call
    sequence is called tail call elimination, or tail call
    optimization. Tail call elimination allows procedure calls in tail
    position to be implemented as efficiently as goto statements, thus
    allowing efficient structured programming.

    -- Wikipedia (https://en.wikipedia.org/wiki/Tail_call)
"

(import [hy.contrib.walk [prewalk]])

(defn --trampoline-- [f]
  "Wrap f function and make it tail-call optimized."
  ;; Takes the function "f" and returns a wrapper that may be used for tail-
  ;; recursive algorithms. Note that the returned function is not side-effect
  ;; free and should not be called from anywhere else during tail recursion.

  (setv result None)
  ;; We have to put this in a list because of Python's
  ;; weirdness around local variables.
  ;; Assigning directly to it later would cause it to
  ;; shadow in a new scope.
  (setv active [False])
  (setv accumulated [])

  (fn [#* args]
    (.append accumulated args)
    (when (not (first active))
      (assoc active 0 True)
      (while (> (len accumulated) 0)
        (setv result (f #* (.pop accumulated))))
      (assoc active 0 False)
      result)))


(defmacro/g! fnr [signature #* body]
  (setv new-body (prewalk
    (fn [x] (if (and (symbol? x) (= x "recur")) g!recur-fn x))
    body))
  `(do
    (import [hy.contrib.loop [--trampoline--]])
    (with-decorator
      --trampoline--
      (defn ~g!recur-fn [~@signature] ~@new-body))
    ~g!recur-fn))


(defmacro defnr [name lambda-list #* body]
  (if (not (= (type name) HySymbol))
    (macro-error name "defnr takes a name as first argument"))
  `(do (require hy.contrib.loop)
       (setv ~name (hy.contrib.loop.fnr ~lambda-list ~@body))))


(defmacro/g! loop [bindings #* body]
  "``loop`` establishes a recursion point. With ``loop``, ``recur``
  rebinds the variables set in the recursion point and sends code
  execution back to that recursion point. If ``recur`` is used in a
  non-tail position, an exception is raised. which
  causes chaos. Fixing this to detect if recur is in a tail-call position
  and erroring if not is a giant TODO.

  Usage: ``(loop bindings #* body)``

  Examples:
    ::

       => (require [hy.contrib.loop [loop]])
       => (defn factorial [n]
       ...  (loop [[i n] [acc 1]]
       ...    (if (zero? i)
       ...      acc
       ...      (recur (dec i) (* acc i)))))
       => (factorial 1000)"
  (setv fnargs (map (fn [x] (first x)) bindings)
        initargs (map second bindings))
  `(do (require hy.contrib.loop)
       (hy.contrib.loop.defnr ~g!recur-fn [~@fnargs] ~@body)
       (~g!recur-fn ~@initargs)))
