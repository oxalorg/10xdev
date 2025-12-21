(ns macro)

(defn add [a b] (+ a b))

(macroexpand '(defn add [a b] (+ a b)))
(macroexpand '(fn ([a b] (+ a b))))
;; => (fn* ([a b] (+ a b)))
