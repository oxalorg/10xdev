(ns openai
  (:require [hato.client :as client]
            [clojure.data.json :as json]))

(def api-key (System/getenv "OPENAI_API_KEY"))

(defn call-openai [prompt]
  (let [url "https://api.openai.com/v1/chat/completions"
        headers {"Authorization" (str "Bearer " api-key)
                 "Content-Type" "application/json"}
        body (json/write-str {:model "gpt-3.5-turbo"
                              :messages [{"role" "user"
                                          "content" prompt}]})]
    (-> (client/post url
                     {:headers headers
                      :body body
                      :as :json})
        :body)))

(defn change-my-prompt [prompt]
  (str "abcdcd " prompt)
  )

(comment

  (println '(if 10 :true :false))

  '(def x (change-my-prompt "My name is xyz"))
  (json/read-str (call-openai x))

  )


(def resp {"id" "chatcmpl-C7jpMK9G9OVtvzrXllxx2sv1pzFvT",
           "object" "chat.completion",
           "created" 1755960492,
           "model" "gpt-3.5-turbo-0125",
           "choices"
           [{"index" 0,
             "message"
             {"role" "assistant",
              "content" "Hello, XYZ! How can I assist you today?",
              "refusal" nil,
              "annotations" []},
             "logprobs" nil,
             "finish_reason" "stop"}],
           "usage"
           {"prompt_tokens" 11,
            "completion_tokens" 11,
            "total_tokens" 22,
            "prompt_tokens_details" {"cached_tokens" 0, "audio_tokens" 0},
            "completion_tokens_details"
            {"reasoning_tokens" 0,
             "audio_tokens" 0,
             "accepted_prediction_tokens" 0,
             "rejected_prediction_tokens" 0}},
           "service_tier" "default",
           "system_fingerprint" nil})

(->>
 (get resp "choices")
 (map #(get % "message"))
 )
