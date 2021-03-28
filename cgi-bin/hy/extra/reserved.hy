;;; Get a frozenset of Hy reserved words
;; Copyright 2021 the authors.
;; This file is part of Hy, which is free software licensed under the Expat
;; license. See the LICENSE.

(import sys keyword)

(setv _cache None)

(defn names []
  "Return a frozenset of reserved symbol names.

  The result of the first call is cached.

  This function can be used to get a list (actually, a ``frozenset``) of the
  names of Hy's built-in functions, macros, and special forms. The output
  also includes all Python reserved words. All names are in unmangled form
  (e.g., ``not-in`` rather than ``not_in``).

  Examples:
    ::

       => (import hy.extra.reserved)
       => (in \"defclass\" (hy.extra.reserved.names))
       True
  "
  (global _cache)
  (if (is _cache None) (do
    (setv _cache (frozenset (map unmangle (+
      hy.core.__all__
      (list (.keys hy.core.bootstrap.__macros__))
      (list (.keys hy.core.macros.__macros__))
      keyword.kwlist
      (list (.keys hy.compiler._special_form_compilers))
      (list hy.compiler._bad_roots)))))))
  _cache)
