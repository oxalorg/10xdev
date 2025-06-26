(ns lox
  (:require [clojure.string :as str]))

(def tokens
  #{;; Single-character tokens.
    :left-paren :right-paren :left-brace :right-brace,
    :comma :dot :minus :plus :semicolon :slash :star,
    ;; one or two character tokens.
    :bang :bang-equal,
    :equal :equal-equal,
    :greater :greater-equal,
    :less :less-equal,
    ;; literals.
    :identifier :string :number,
    ;; keywords.
    :and :class :else :false :fun :for :if :nil :or,
    :print :return :super :this :true :var :while,

    :eof})

(defrecord Token [type lexeme literal line]
  Object
  (toString [_]
    (str type " " lexeme " " literal)))

(defn new-token [type]
  (map->Token {:type type
               :lexeme ""
               :literal nil
               :line -1}))

(defn report [line where msg]
  (println (str "[line " line "] Error " where ": " msg)))

(defn error [line msg]
  (report line "" msg))

(defn scan-token [src]
  (let [c (first src)]
    (case c
      \) (new-token :left-paren)
      \( (new-token :right-paren)
      \{ (new-token :left-brace)
      \} (new-token :right-brace)
      \, (new-token :comma)
      \. (new-token :dot)
      \- (new-token :minus)
      \+ (new-token :plus)
      \; (new-token :semicolon)
      \* (new-token :star))))

(defn scan-tokens [src]
  (loop [tokens []
         start 0
         current 0
         line 1]
    (let [eof? (>= current (count src))]
      (if eof?
        (conj tokens (map->Token {:type :eof
                                  :lexeme ""
                                  :literal nil
                                  :line line}))
        (while (not (str/blank? src))
          (let [token (scan-token src)]
            (recur (conj tokens token) ))
          )
        ))
    )
  )

(defn run [src]
  (let [tokens (scan-tokens src)]
    tokens
    )
  )
