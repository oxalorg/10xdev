(ns gemini-client
  (:require [hato.client :as http]
            [clojure.data.json :as json]))

(def ^:private gemini-base-url "https://generativelanguage.googleapis.com/v1beta/models")

(defn create-client
  "Creates a Gemini API client with the given API key.
   
   Usage:
   (def client (create-client \"your-api-key-here\"))
   "
  [api-key]
  {:api-key api-key
   :http-client (http/build-http-client {:connect-timeout 10000
                                         :request-timeout 30000})})

(defn- prepare-request-body
  "Prepares the request body for Gemini API calls."
  [messages & {:keys [temperature max-tokens model]
               :or {temperature 0.7
                    max-tokens 1024
                    model "gemini-pro"}}]
  (let [contents (if (string? messages)
                   [{:parts [{:text messages}]}]
                   (mapv (fn [msg]
                           (cond
                             (string? msg) {:parts [{:text msg}]}
                             (map? msg) (if (:role msg)
                                          {:role (:role msg)
                                           :parts [{:text (:content msg)}]}
                                          {:parts [{:text (:text msg)}]})
                             :else {:parts [{:text (str msg)}]}))
                         (if (sequential? messages) messages [messages])))]
    {:contents contents
     :generationConfig {:temperature temperature
                        :maxOutputTokens max-tokens}}))

(defn- make-request
  "Makes an HTTP request to the Gemini API."
  [client model request-body]
  (let [url (str gemini-base-url "/" model ":generateContent")
        response (http/post url
                            {:http-client (:http-client client)
                             :query-params {:key (:api-key client)}
                             :headers {"Content-Type" "application/json"}
                             :body (json/write-str request-body)
                             :as :text})]
    (if (= 200 (:status response))
      (json/read-str (:body response) :key-fn keyword)
      (throw (ex-info "Gemini API request failed"
                      {:status (:status response)
                       :body (:body response)
                       :response response})))))

(defn generate-text
  "Generates text using Google's Gemini API.
   
   Parameters:
   - client: Client created with create-client
   - messages: String or vector of messages
   - options: Optional map with :temperature, :max-tokens, :model
   
   Usage:
   (generate-text client \"Hello, how are you?\")
   (generate-text client \"Explain quantum computing\" {:temperature 0.3 :max-tokens 2048})
   (generate-text client [{:role \"user\" :content \"Hello\"}
                          {:role \"assistant\" :content \"Hi there!\"}
                          {:role \"user\" :content \"How are you?\"}])
   "
  [client messages & {:keys [temperature max-tokens model]
                      :or {temperature 0.7
                           max-tokens 1024
                           model "gemini-pro"}
                      :as options}]
  (let [request-body (prepare-request-body messages options)
        response (make-request client model request-body)]
    (-> response
        :candidates
        first
        :content
        :parts
        first
        :text)))

(defn generate-text-stream
  "Generates streaming text using Google's Gemini API.
   
   Parameters:
   - client: Client created with create-client
   - messages: String or vector of messages
   - callback-fn: Function to call with each chunk of text
   - options: Optional map with :temperature, :max-tokens, :model
   
   The callback-fn will be called with each chunk of generated text.
   
   Usage:
   (generate-text-stream client \"Tell me a story\" 
                        (fn [chunk] (print chunk) (flush)))
   "
  [client messages callback-fn & {:keys [temperature max-tokens model]
                                  :or {temperature 0.7
                                       max-tokens 1024
                                       model "gemini-pro"}
                                  :as options}]
  (let [request-body (assoc (prepare-request-body messages options)
                            :stream true)
        url (str gemini-base-url "/" model ":streamGenerateContent")
        response (http/post url
                            {:http-client (:http-client client)
                             :query-params {:key (:api-key client)}
                             :headers {"Content-Type" "application/json"}
                             :body (json/write-str request-body)
                             :as :stream})]
    (if (= 200 (:status response))
      (with-open [reader (java.io.BufferedReader.
                          (java.io.InputStreamReader. (:body response)))]
        (loop []
          (when-let [line (.readLine reader)]
            (when-not (empty? line)
              (try
                (let [data (json/read-str line :key-fn keyword)
                      text (-> data :candidates first :content :parts first :text)]
                  (when text
                    (callback-fn text)))
                (catch Exception e
                  ;; Skip malformed JSON lines
                  nil)))
            (recur))))
      (throw (ex-info "Gemini streaming API request failed"
                      {:status (:status response)
                       :response response})))))

(defn list-models
  "Lists available Gemini models.
   
   Usage:
   (list-models client)
   "
  [client]
  (let [url (str gemini-base-url)
        response (http/get url
                           {:http-client (:http-client client)
                            :query-params {:key (:api-key client)}
                            :headers {"Content-Type" "application/json"}
                            :as :text})]
    (if (= 200 (:status response))
      (json/read-str (:body response) :key-fn keyword)
      (throw (ex-info "Gemini API request failed"
                      {:status (:status response)
                       :body (:body response)
                       :response response})))))

(comment
  ;; Example usage:

  ;; 1. Create a client
  (def client (create-client "your-gemini-api-key"))

  ;; 2. Simple text generation
  (generate-text client "What is the capital of France?")

  ;; 3. Text generation with options
  (generate-text client "Explain machine learning"
                 :temperature 0.3
                 :max-tokens 2048)

  ;; 4. Conversation-style messages
  (generate-text client [{:role "user" :content "Hello"}
                         {:role "assistant" :content "Hi! How can I help you?"}
                         {:role "user" :content "Tell me about Clojure"}])

  ;; 5. Streaming text generation
  (generate-text-stream client "Write a short poem about programming"
                        (fn [chunk] (print chunk) (flush)))

  ;; 6. List available models
  (list-models client))
