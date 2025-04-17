(ns wow-ornament-is-amazing.trust-me
  (:require [lambdaisland.ornament :as o]
            [lambdaisland.hiccup :as h]))

(o/defstyled freebies-link :a
  {:font-size "1rem"
   :color "#cff9cf"
   :text-decoration "underline"})

(str freebies-link)

(o/css freebies-link)
(print (o/css freebies-link))

(h/html
 [freebies-link {:href "/hoc-tees"} "freebies"])

(h/render
 [freebies-link {:href "/hoc-tees"} "freebies"])

(o/defstyled tailwind-is-good-too freebies-link
  :flex :bg-red-100 :text-green-800
  ([is-it?]
   ;; FIXME: `is-it?` is ignored, do we need it or do we deprecate it
   [:<>
    "Do you like utility tokens?: "
    [:strong (-> [:true :false :true]
                 shuffle
                 first)]]))

(o/defined-styles)
(println (o/defined-styles))


;; but you can cascade css
;; also render functions
(o/defstyled page-grid :div
  :relative :h-full :md:flex
  [:>.content :flex-1 :p-2 :md:p-10 :bg-gray-100]
  ([sidebar content]
   [:<>
    sidebar
    [:div.content
     content]]))

;; https://github.com/green-coder/girouette
(o/set-tokens! {:tw-version 3})

(o/set-tokens! {:colors {:primary "001122"}
                :fonts {:system "-apple-system,BlinkMacSystemFont,Segoe UI,Helvetica,Arial,sans-serif,Apple Color Emoji,Segoe UI Emoji"}
                :components [{:id :full-center
                              :garden {:display "inline-flex"
                                       :align-items "center"}}
                             {:id :full-center-bis
                              :garden [:& :inline-flex :items-center]}
                             {:id :custom-bullets
                              :rules "custom-bullets = <'bullets-'> bullet-char
                                 <bullet-char> = #\".\""
                              :garden (fn [{[bullet-char] :component-data}]
                                        [:&
                                         {:list-style "none"
                                          :padding 0
                                          :margin 0}
                                         [:li
                                          {:padding-left "1rem"
                                           :text-indent "-0.7rem"}]
                                         ["li:before"
                                          {:content bullet-char}]])}
                             {:id :font-size
                              :rules "
    font-size = <'font-size-'> #\"0-9\"+
    "
                              :garden (fn [{[font-size] :component-data}]
                                        {:font-size "var(--font-size-" font-size ")"})}
                             ]})

(o/defstyled referenced :div
  {:color :blue})

(o/defstyled referer :p
  [referenced {:color :red}] ;; use as classname
  [:.foo referenced]) ;; use as style rule

(o/rules referer)
(o/as-garden referer)
(o/css referer)

(o/defstyled menu :nav
  {:padding "2rem"}
  [:a {:color "blue"}]
  [:&:hover {:background-color "#888"}])

(o/css menu)

;; :at-media extension
(o/defstyled eps-container :div
  {:display "grid"
   :grid-gap "1rem"}
  [:at-media {:min-width "40rem"}
   {:grid-gap "2rem"}])

@o/registry

(o/rules eps-container)
(println (o/css eps-container))
(spit "really-trust-me.css" (o/defined-styles))

(defn lol []
  (println "BUllshit")
  :bullshit)

(lol)
(Math/round 10/2)

(clojure.math/round 10/3)
