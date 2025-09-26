(ns user
  (:require [clojure.repl.deps :as deps]))

(defn reset []
  (deps/sync-deps)
  )
