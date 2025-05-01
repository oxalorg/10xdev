#!/usr/bin/env bb

(require '[babashka.curl :as curl]
         '[cheshire.core :as json]
         '[clojure.string :as str]
         '[clojure.java.io :as io])

(defn parse-authinfo-token []
  (try
    (when-let [auth-lines (seq (line-seq (io/reader (str (System/getProperty "user.home") "/.authinfo"))))]
      (some (fn [line]
              (when (re-find #"machine api.github\.com" line)
                (second (re-find #"password\s+(\S+)" line))))
            auth-lines))
    (catch Exception e
      (println "❌ Failed to read ~/.authinfo:" (.getMessage e))
      (System/exit 1))))

(def token (parse-authinfo-token))

(when-not token
  (println "❌ No GitHub token found in ~/.authinfo")
  (System/exit 1))

(def headers
  {"Authorization" (str "Bearer " token)
   "Accept" "application/vnd.github+json"
   "User-Agent" "bb-star-script"})

(defn fetch-repos [user]
  (let [url (str "https://api.github.com/users/" user "/repos?per_page=100")]
    (try
      (let [{:keys [status body]} (curl/get url {:headers headers})]
        (cond
          (= status 200) (json/parse-string body true)
          (= status 401) (do (println "❌ Unauthorized. Check your GitHub token.") (System/exit 1))
          :else (do (println "❌ Failed to fetch repos. Status:" status) [])))
      (catch Exception e
        (println "❌ Error fetching repos:" (.getMessage e))
        []))))

(defn starred? [owner repo]
  (let [url (str "https://api.github.com/user/starred/" owner "/" repo)]
    (try
      (let [{:keys [status]} (curl/get url {:headers headers})]
        (case status
          204 true
          404 false
          (do (println "⚠️ Unexpected response when checking star status of" repo "Status:" status)
              false)))
      (catch Exception e
        (println "⚠️ Error checking if" repo "is starred:" (.getMessage e))
        false))))

(defn star-repo [owner repo]
  (let [url (str "https://api.github.com/user/starred/" owner "/" repo)]
    (try
      (let [{:keys [status]} (curl/put url {:headers headers})]
        (if (= status 204)
          (println "⭐️ Starred" repo)
          (println "❌ Failed to star" repo "Status:" status)))
      (catch Exception e
        (println "❌ Error starring" repo ":" (.getMessage e))))))

(defn -main []
  (let [repos (fetch-repos "lambdaisland")]
    (doseq [{:keys [name owner]} repos]
      (if (starred? (:login owner) name)
        (println "✅ Already starred" name)
        (do
          (star-repo (:login owner) name)
          (Thread/sleep 2000))))))

(-main)
