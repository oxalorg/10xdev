(ns ornament-is-amazing-wow-really-wow.omg
  (:require [lambdaisland.ornament :as o]
            [lambdaisland.hiccup :as h]))

(o/defstyled freebies-link :a
  {:font-size "1rem"
   :color "#cff9cf"
   :text-decoration "underline"})

(str freebies-link)
;; => "demo__freebies_link"

(o/css freebies-link)
;; => ".demo__freebies_link{font-size:1rem;color:#cff9cf;text-decoration:underline}"

(h/html
 [freebies-link {:href "/hoc-tees"} "freebies"])

(h/render
 [freebies-link {:href "/hoc-tees"} "freebies"])

(o/defined-styles)

(o/defstyled ta)
